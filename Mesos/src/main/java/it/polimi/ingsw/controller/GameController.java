package it.polimi.ingsw.controller;

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
import it.polimi.ingsw.network.socket.ClientManagerSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.serverInterface.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameController {
    private final Server server;
    private final List<Player> players;
    private final Map<String, ClientConnection> clientManagers;
    private Game game;
    private TurnController turnController;

    public GameController(Server server, List<Player> players, Map<String, ClientConnection> clientManagers) {
        this.server = server;
        this.players = players;
        this.clientManagers = clientManagers;
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

        turnController = new TurnController(this, players.size());
        turnController.startPlacementPhase(game.getBoard().getTurnOrderTile());
    }

    /**
     * Places a totem on a specific OfferTile (calls placeTotem from Game, model).
     * Sends an event that updates the board with the totem.
     */
    public synchronized void placeTotem(String playerName, char letter) {
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
            clientManagers.get(playerName).sendEvent(new ValidCardsEvent(player.getTribe()));

        broadcastEvent(new UpdateAllPlayersEvent(players));
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
            cm.sendEvent(serverEvent);
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
        Map<Player, Integer> finalScores = game.calculateFinalScores();
        List<Player> winners = game.getWinner(finalScores);
        broadcastEvent(new EndGameEvent(winners, finalScores));
    }

}
