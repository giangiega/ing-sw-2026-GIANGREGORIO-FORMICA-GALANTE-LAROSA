package it.polimi.ingsw.controller;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.exceptions.InvalidPlayerActionException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.cards.buildings.BuildingDeck;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.decks.Deck;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.GameConfig;
import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.network.serverInterface.*;

import java.util.ArrayList;
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

    private List<String> disconnectedPlayers;
    /**
     * Timer used when only one player is left connected.
     * If no one reconnects within SUSPENSION_TIMEOUT_SECONDS, the sole
     * remaining player is declared the winner.
     */
    private static final int SUSPENSION_TIMEOUT_SECONDS = 60;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> suspensionFuture;

    private boolean gameOver = false;
    private boolean isSuspended = false;



    public GameController(List<Player> players, Map<String, ClientConnection> clientManagers) {
        this.players = players;
        this.clientManagers = clientManagers;
        this.disconnectedPlayers = new ArrayList<>();
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
            turnController.startPlacementPhase(game.getBoard().getTurnOrderTile());
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
        broadcastEvent(new EndGameEvent(winners, finalScores));
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
     * @param playerName
     * @return null if the player doesn't exist or the totem's color
     */
    public ColorEnum getPlayerColor(String playerName) {
        Player p = getPlayerByName(playerName);
        return p != null ? p.getTotemColor() : null;
    }

    /**
     * @author Giuse
     * @param playerName : name of the disconnected player
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

        if(connectedCount == 0){//No player il game: game ends instantly
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
     * @param playerName : player the returned
     * This method remove the player's name from the disconnected list. After that, it updates his entry
     * in the clientManagers and stops the clock, if one had even started. It sends the current board state
     * to the player
     */
    public synchronized void handleReconnection(String playerName, ClientConnection newCm) {
        if(gameOver) return;//You can't reconnect to the game if it is over
        if (!disconnectedPlayers.contains(playerName)) return;//Wrong client

        disconnectedPlayers.remove(playerName);
        clientManagers.put(playerName, newCm);

        boolean wasSuspended = isSuspended;

        cancelSuspensionTimer();

        newCm.sendEvent(new ReconnectedTotemEvent(getPlayerByName(playerName).getTotemColor()));

        broadcastEvent(new PlayerReconnectedEvent(playerName));

        // Send the full current game state to the reconnected client so their
        // view is up-to-date before they need to act.
        if (game != null) {
            newCm.sendEvent(new UpdateRoundEvent(game.getCurrentRound()));
            newCm.sendEvent(new UpdateOfferTrackEvent(
                    game.getBoard().getOfferTrack(),
                    game.getBoard().getTurnOrderTile()));
            newCm.sendEvent(new UpdateRowsEvent(
                    game.getBoard().getUpperRow(),
                    game.getBoard().getLowerRow(),
                    game.getBoard().getBuildingUpperRow(),
                    game.getBoard().getBuildingLowerRow()));
            newCm.sendEvent(new UpdateAllPlayersEvent(players));
            newCm.sendEvent(new UpdateAllTribesEvent(players));
            Player p = getPlayerByName(playerName);
            if (p != null) {
                newCm.sendEvent(new ValidCardsEvent(p.getTribe(),p.getBuildingCards()));
            }

            if (wasSuspended) {
                broadcastEvent(new GameResumedEvent());
                turnController.resumeAfterSuspension(
                        game.getBoard().getTurnOrderTile(),
                        game.getBoard().getOfferTrack()
                );
            }
        }
    }
    /**********Timer handling**********/

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




}
