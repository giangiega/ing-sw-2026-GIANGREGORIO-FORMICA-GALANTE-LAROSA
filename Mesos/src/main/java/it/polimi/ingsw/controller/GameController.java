package it.polimi.ingsw.controller;

import it.polimi.ingsw.database.DatabaseManager;
import it.polimi.ingsw.database.RankingRow;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.exceptions.InvalidPlayerActionException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.buildings.BuildingDeck;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.decks.Deck;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.GameConfig;
import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.network.serverInterface.*;
import it.polimi.ingsw.persistence.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameController {
    private final List<Player> players;
    private final Map<String, ClientConnection> clientManagers;
    private Game game;
    private TurnController turnController;
    private DatabaseManager database;

    private List<String> disconnectedPlayers;
    private Set<String> playersRequiredToResume = new HashSet<>();
    private boolean isRecoveryMode = false;
    /**
     * Players who reconnected during the current round (i.e. after their totem
     * was already stuck in slots from a previous absence). They are no longer
     * in disconnectedPlayers but still need to be pushed to the end of the
     * TurnOrderTile at the start of the next placement phase.
     * Cleared in endRound() after moveDisconnectedToEnd is applied.
     */
    private final List<String> reconnectedThisRound = new ArrayList<>();
    /**
     * Timer used when only one player is left connected.
     * If no one reconnects within SUSPENSION_TIMEOUT_SECONDS, the sole
     * remaining player is declared the winner.
     */
    private static final int SUSPENSION_TIMEOUT_SECONDS = 30;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> suspensionFuture;

    private boolean gameOver = false;
    private boolean isSuspended = false;



    public GameController(List<Player> players, Map<String, ClientConnection> clientManagers) {
        this.players = players;
        this.clientManagers = clientManagers;
        this.disconnectedPlayers = new ArrayList<>();
    }

    public Game getGame(){
        return this.game;
    }

    public TurnController getTurnController(){
        return  this.turnController;
    }

    /**
     * Initializes decks, board and model.
     * Creates GameConfig based on numPlayers and TurnController.
     */
    public void startGame() {
        GameConfig config = GameConfig.create(players.size());

        CardFactory cardFactory = new CardFactory();
        Deck tribeDeck = cardFactory.buildTribeDeck(config);
        BuildingDeck bd1 = cardFactory.buildBuildingDeck(EraEnum.I, config);
        BuildingDeck bd2 = cardFactory.buildBuildingDeck(EraEnum.II, config);
        BuildingDeck bd3 = cardFactory.buildBuildingDeck(EraEnum.III, config);

        Board board = new Board(config, tribeDeck, bd1, bd2, bd3);
        this.game = new Game(new ArrayList<>(players), board, config);
        game.startGame();
        broadcastEvent(new UpdateRoundEvent(1));
        broadcastEvent(new GameStartedEvent(game.getBoard().getOfferTrack(),
                game.getBoard().getTurnOrderTile(), game.getBoard().getUpperRow(),
                game.getBoard().getLowerRow(), game.getBoard().getBuildingUpperRow(),
                game.getBoard().getBuildingLowerRow()));
        broadcastEvent(new UpdateAllPlayersEvent(players));
        broadcastEvent(new UpdateAllTribesEvent(players));


        turnController = new TurnController(this, players.size());
        turnController.startPlacementPhase(game.getBoard().getTurnOrderTile());

        this.database = DatabaseManager.getDatabase();
    }

    /**
     * Places a totem on a specific OfferTile (calls placeTotem from Game, model).
     * Sends an event that updates the board with the totem.
     */
    public synchronized void placeTotem(String playerName, char letter) {
        if(isSuspended) return;//Ignore input if the game is suspended
        Player p = getPlayerByName(playerName);
        if(p == null)
            return;
        OfferTile tile = getOfferTileByLetter(letter);
        if(tile == null)
            return;

        try {
            game.placeTotem(p, tile);
        } catch (InvalidPlayerActionException e) {
            ClientConnection cms = clientManagers.get(playerName);
            cms.sendEvent(new InvalidChoiceEvent("Tile already occupied, choose another"));
            cms.sendEvent(new MoveTotemEvent(
                    game.getBoard().getOfferTrack(),
                    game.getBoard().getTurnOrderTile()
            ));
            return;
        }

        broadcastEvent(new UpdateOfferTrackEvent(game.getBoard().getOfferTrack(),
                game.getBoard().getTurnOrderTile()));

        turnController.onTotemPlaced(playerName, game.getBoard().getTurnOrderTile(),
                game.getBoard().getOfferTrack());
    }

    /**
     * @author Giuse
     * @param playerName player to ask for index
     */
    void sendAskBuildingUpperRow(String playerName) {
        ClientConnection cm = clientManagers.get(playerName);
        if (cm != null)
            cm.sendEvent(new AskBuildingUpperRowEvent(game.getBoard().getUpperRow(), game.getBoard().getBuildingUpperRow()));
    }

    /**
     * @author Giuse
     * @return list of player's to ask for building's power
     */
    List<String> collectPlayersNeedingBuildingChoice() {
        List<String> result = new ArrayList<>();
        for (Player p : game.getPlayers()) {
            if (disconnectedPlayers.contains(p.getName())) continue;
            for (BuildingCard c : p.getBuildingCards()) {
                if (c.getEffect().requiresChoice(p, game.getBoard())) {
                    result.add(p.getName());
                    break;
                }
            }
        }
        return result;
    }

    /**
     * @author Giuse
     * @param playerName player
     * @param chosenIndex chosen index
     * @param chosenIsBuilding the chosen card is a building
     */
    public synchronized void submitBuildingUpperRowChoice(String playerName, int chosenIndex, boolean chosenIsBuilding) {
        if (isSuspended) return;
        Player p = getPlayerByName(playerName);
        if (p == null) return;
        for (BuildingCard c : p.getBuildingCards()) {
            if (c.getEffect().requiresChoice(p, game.getBoard())) {
                c.getEffect().setChoice(chosenIndex, chosenIsBuilding);
                break;
            }
        }
        turnController.onBuildingChoiceReceived(playerName);
    }

    /**
     * Resolves the acquisition of cards and buildings for the player.
     * unplaceTotem() is called inside resolveAction() on Game model class !!!
     * Updates the board and sends an event that manages the pickable cards.
     */
    public synchronized void resolveAction(String playerName,
                                           List<Integer> upperCards, List<Integer> lowerCards,
                                           List<Integer> upperBuildings, List<Integer> lowerBuildings) {
        if(isSuspended) return;//Ignore input while suspended
        if (!playerName.equals(turnController.getCurrentResolvingPlayer()))
            return;

        OfferTile tile = getOfferTileByPlayer(playerName);
        if(tile == null)
            return;

        try {
            game.resolveAction(tile, upperCards, lowerCards, upperBuildings, lowerBuildings);
        } catch (InvalidPlayerActionException e) {
            ClientConnection cms = clientManagers.get(playerName);
            cms.sendEvent(new InvalidChoiceEvent(e.getMessage()));
            cms.sendEvent(new IsYourTurnEvent(tile, game.getBoard().getUpperRow(),
                    game.getBoard().getLowerRow(), game.getBoard().getBuildingUpperRow(),
                    game.getBoard().getBuildingLowerRow()));
            return;
        }

        Player player = getPlayerByName(playerName);
        if(player != null)
            clientManagers.get(playerName).sendEvent(new ValidCardsEvent(player.getTribe(),player.getBuildingCards()));

        broadcastEvent(new UpdateAllPlayersEvent(players));
        broadcastEvent(new UpdateAllTribesEvent(players));
        broadcastEvent(new UpdateRowsEvent(game.getBoard().getUpperRow(), game.getBoard().getLowerRow(),
                game.getBoard().getBuildingUpperRow(), game.getBoard().getBuildingLowerRow()));
        broadcastEvent(new UpdateOfferTrackEvent(game.getBoard().getOfferTrack(),
                game.getBoard().getTurnOrderTile()));

        turnController.onActionResolved();
    }

    /**
     * Manages the transition between rounds.
     * 10th round is the last.
     * Calls endRound() in Game model.
     */
    public void endRound() {
        try {
            game.endRound();
        } catch (InvalidPlayerActionException e) {
            broadcastEvent(new InvalidChoiceEvent("Can't end round"));
        }

        if (game.getCurrentRound() > 10) {
            endGame();
        } else {
            broadcastEvent(new UpdateRoundEvent(game.getCurrentRound()));
            broadcastEvent(new UpdateAllPlayersEvent(players));
            broadcastEvent(new UpdateAllTribesEvent(players));
            broadcastEvent(new UpdateBoardEvent(game.getBoard().getOfferTrack(),
                    game.getBoard().getTurnOrderTile(), game.getBoard().getUpperRow(),
                    game.getBoard().getLowerRow(), game.getBoard().getBuildingUpperRow(),
                    game.getBoard().getBuildingLowerRow()));

            // Ensure disconnected players are last in placement order for the next round
            List<String> toMoveToEnd = new ArrayList<>(disconnectedPlayers);
            toMoveToEnd.addAll(reconnectedThisRound);
            game.getBoard().getTurnOrderTile().moveDisconnectedToEnd(toMoveToEnd);

            reconnectedThisRound.clear();
            broadcastEvent(new UpdateOfferTrackEvent(
                    game.getBoard().getOfferTrack(),
                    game.getBoard().getTurnOrderTile()
            ));

            turnController.startPlacementPhase(game.getBoard().getTurnOrderTile());
            // update before snapshot so the round logic in recovery is correct

            try {
                TurnControllerSnapshot snap = turnController.getSnapshot();
                PersistenceManager.save(new SavedGameState(game, snap, disconnectedPlayers));
            } catch (IOException e) {
                System.err.println("[Persistence] Failed to save game state: " + e.getMessage());
            }
        }
    }

    // next 2 methods are only used by TurnController
    void sendIsYourTurn(String playerName) {
        ClientConnection cms = clientManagers.get(playerName);
        if (cms != null)
            cms.sendEvent(new IsYourTurnEvent(getOfferTileByPlayer(playerName), game.getBoard().getUpperRow(),
                    game.getBoard().getLowerRow(), game.getBoard().getBuildingUpperRow(),
                    game.getBoard().getBuildingLowerRow()));
    }

    void sendMoveTotem(String playerName) {
        ClientConnection cm = clientManagers.get(playerName);
        if(cm != null) {
            cm.sendEvent(new MoveTotemEvent(game.getBoard().getOfferTrack(),
             game.getBoard().getTurnOrderTile()));
        }
    }

    // helper methods to access players or tiles into the previous methods
    private void broadcastEvent(ServerEvent serverEvent) {
        for(ClientConnection cm : clientManagers.values()) {
            String name = cm.getPlayerName();
            if(name != null && disconnectedPlayers.contains(name)) {
                continue;
            }
            try{
                cm.sendEvent(serverEvent);
            }catch(Exception e){
                System.out.println("Broadcast: cannot send event to player" + name + ":" + e.getMessage());
            }
        }
    }

    private Player getPlayerByName(String name) {
        for(Player p : players) {
            if(p.getName().equals(name))
                return p;
        }
        return null;
    }

    private OfferTile getOfferTileByLetter(char letter) {
        for(OfferTile t : game.getBoard().getOfferTrack()) {
            if(t.getLetter() == letter)
                return t;
        }
        return null;
    }

    private OfferTile getOfferTileByPlayer(String name) {
        for(OfferTile t : game.getBoard().getOfferTrack()) {
            if(!t.getFreeOfferTile() && t.getOccupant() != null && t.getOccupant().getName().equals(name))
                return t;
        }
        return null;
    }

    private void endGame() {
        gameOver = true;//No more reconnection if the game has ended
        PersistenceManager.clear();
        Map<Player, Integer> finalScores = game.calculateFinalScores();
        List<Player> listOfToRemovePlayers =  new ArrayList<>();
        for(Player p : finalScores.keySet()) {
            if(getDisconnectedPlayers().contains(p.getName())) {
                listOfToRemovePlayers.add(p);
            }
        }
        for(Player p : listOfToRemovePlayers) {
            finalScores.remove(p);
        }
        List<Player> winners = game.getWinner(finalScores);
        int numPlayers = players.size();

        for (Player p: finalScores.keySet()){
            boolean winner;
            if(winners.contains(p)){
                winner = true;
            }else{
                winner = false;
            }
            database.saveResult(p.getName(), finalScores.get(p), numPlayers, winner);
        }

        List<RankingRow> ranking = database.getRanking(numPlayers);

        Map<String, Integer> playersPosition = new HashMap<>();
        for(Player p: finalScores.keySet())
            playersPosition.put(p.getName(), database.getPlayerPosition(p.getName(), numPlayers));

        broadcastEvent(new EndGameEvent(winners, finalScores, ranking, playersPosition));
    }

    /**
     * @author Giuse
     * @return gameOver
     * This method tells if the game is over
     */
    public boolean getGameOver() {
        return gameOver;
    }

    /**
     * @author Giuse
     * This method return the list of the names of the players who left the game
     */
    public List<String> getDisconnectedPlayers() {
        return disconnectedPlayers;
    }

    /**
     * @author Giuse
     * @return the number of connected players
     */
    public int getConnectedPlayersCount(){
        int connectedPlayers = 0;
        for(Player p : players){
            if(!disconnectedPlayers.contains(p.getName())){
                connectedPlayers++;
            }
        }
        return connectedPlayers;
    }

    /**
     * @author Giuse
     * @return true if the player is disconnected
     */
    public boolean isDisconnectedPlayer(String playerName){
       if(disconnectedPlayers.contains(playerName))
           return true;
       return false;
    }

    /**
     * @author Giuse
     * @param playerName name of the player whose totem's color is needed
     * @return null if the player doesn't exist or the totem's color
     */
    public ColorEnum getPlayerColor(String playerName) {
        Player p = getPlayerByName(playerName);
        return p != null ? p.getTotemColor() : null;
    }

    /**
     * @author Giuse
     * @param playerName name of the disconnected player
     * This method put the player in the disconnected players' list and notify this to all other players
     * via PlayerDisconnectedEvent. After that, if there's only one player left in the lobby, it starts a countdown.
     * If no player reconnect, the only remaining one is proclaimed as winner and endGame() is called.
     * Otherwise, it collabs with TurnController to skip the disconnected players' turns
     */
    public synchronized void handleDisconnection(String playerName) {
        //Checking for unknown or already-disconnected player
        if (getPlayerByName(playerName) == null) return;
        if (disconnectedPlayers.contains(playerName)) return;

        disconnectedPlayers.add(playerName);
        broadcastEvent(new PlayerDisconnectedEvent(playerName));

        // Nothing more to do if the game hasn't started
        if (game == null || turnController == null) return;

        int connectedCount = getConnectedPlayersCount();

        if(connectedCount == 0){//No player in game: game ends instantly
            System.out.println("Server: no player left in the game, game ended.");
            cancelSuspensionTimer();
            endGame();
        }
        else if (connectedCount == 1) { //one player left--> start timer
            broadcastEvent(new GameSuspendedEvent(SUSPENSION_TIMEOUT_SECONDS));
            startSuspensionTimer();
        } else{
            turnController.onPlayerDisconnected(playerName);
        }
    }

    /**
     * @author Giuse
     * @param playerName player that returned
     * This method remove the player's name from the disconnected list. After that, it updates his entry
     * in the clientManagers and stops the clock, if one had even started. It sends the current board state
     * to the player
     */
    public synchronized void handleReconnection(String playerName, ClientConnection newCm) {
        if(gameOver) return;//You can't reconnect to the game if it is over
        if (!disconnectedPlayers.contains(playerName)) return;//Wrong client

        disconnectedPlayers.remove(playerName);
        reconnectedThisRound.add(playerName);

        // Silence the old connection's disconnect handler before replacing it.
        // This prevents a race condition where a disconnect event from
        // the previous socket or RMI heartbeat fires after the new connection
        // is already active
        ClientConnection oldCm = clientManagers.get(playerName);
        if (oldCm != null && oldCm != newCm) {
            oldCm.stopSilently();
        }
        clientManagers.put(playerName, newCm);

        Player p = getPlayerByName(playerName);
        if (p == null) return;

        boolean wasSuspended = isSuspended;
        boolean wasRecovering = isRecoveryMode;

        newCm.sendEvent(new ReconnectedTotemEvent(p.getTotemColor()));
        broadcastEvent(new PlayerReconnectedEvent(playerName));

        // Send the full current game state to the reconnected client so their
        // view is up-to-date before they need to act.
        if (wasRecovering) {
            playersRequiredToResume.remove(playerName);
            if (playersRequiredToResume.isEmpty()) {
                isRecoveryMode = false;
                isSuspended = false;
                cancelSuspensionTimer();
                broadcastEvent(new GameResumedEvent());

                for (ClientConnection cm : clientManagers.values()) {
                    String name = cm.getPlayerName();
                    if (name != null && !disconnectedPlayers.contains(name)) {
                        sendFullStateToClient(cm, name);
                    }
                }

                turnController.resumeAfterSuspension(
                        game.getBoard().getTurnOrderTile(),
                        game.getBoard().getOfferTrack());

            } else {
                newCm.sendEvent(new WaitingRecoveryEvent(playersRequiredToResume.size())); // notify the player, server is back
            }
        } else {
            cancelSuspensionTimer();
            sendFullStateToClient(newCm, playerName);
            if (wasSuspended) {
                broadcastEvent(new GameResumedEvent());
                turnController.resumeAfterSuspension(
                        game.getBoard().getTurnOrderTile(),
                        game.getBoard().getOfferTrack());
            }
        }
    }

    private void sendFullStateToClient(ClientConnection cm, String playerName) {
        cm.sendEvent(new UpdateRoundEvent(game.getCurrentRound()));
        cm.sendEvent(new UpdateOfferTrackEvent(game.getBoard().getOfferTrack(), game.getBoard().getTurnOrderTile()));
        cm.sendEvent(new UpdateRowsEvent(game.getBoard().getUpperRow(), game.getBoard().getLowerRow(),
                game.getBoard().getBuildingUpperRow(), game.getBoard().getBuildingLowerRow()));
        cm.sendEvent(new UpdateAllPlayersEvent(players));
        cm.sendEvent(new UpdateAllTribesEvent(players));
        Player p = getPlayerByName(playerName);
        if (p != null)
            cm.sendEvent(new ValidCardsEvent(p.getTribe(), p.getBuildingCards()));
    }

    //*********Timer handling**********//

    /**
     * @author Giuse
     * This method cancel any ongoing timer
     */
    private void cancelSuspensionTimer() {
        isSuspended = false;//If I cancel the timer, the game is not suspended anymore
        if (suspensionFuture != null && !suspensionFuture.isDone()) {
            suspensionFuture.cancel(false);
            suspensionFuture = null;
        }
    }

    /**
     * @author Giuse
     * This method starts a new timer
     */
    private void startSuspensionTimer() {
        // Cancel any in-flight future WITHOUT clearing isSuspended
        if (suspensionFuture != null && !suspensionFuture.isDone()) {
            suspensionFuture.cancel(false);
            suspensionFuture = null;
        }
        isSuspended = true;
        suspensionFuture = scheduler.schedule(() -> {
            synchronized (this) {
                // Check if someone reconnected
                if (getConnectedPlayersCount() >= 2) return;
                System.out.println("Server: no reconnection during timeout, game is over.");
                // Declare the last connected player as winner
                endGame();
            }
        }, SUSPENSION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    public void restoreGame(SavedGameState state) {
        this.game = state.getGame();
        this.players.clear();
        this.players.addAll(this.game.getPlayers());

        this.isRecoveryMode = true;
        this.isSuspended = true;

        // Restore pre-crash disconnections exactly as they were
        this.disconnectedPlayers.clear();
        for (Player p : this.players) {
            this.disconnectedPlayers.add(p.getName());
        }

        // Players required to resume = those who were connected at crash time
        this.playersRequiredToResume.clear();
        for (Player p : this.players) {
            if (!state.getDisconnectedPlayers().contains(p.getName())) {
                playersRequiredToResume.add(p.getName());
            }
        }

        this.turnController = new TurnController(this, players.size());
        turnController.restoreFromSnapshot(state.getTurnSnapshot());
        turnController.restoreBoard(game.getBoard().getTurnOrderTile(),
                game.getBoard().getOfferTrack());

        this.database = DatabaseManager.getDatabase();
    }

}
