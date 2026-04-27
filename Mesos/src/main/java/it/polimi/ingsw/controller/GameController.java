package it.polimi.ingsw.controller;

import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.exceptions.InvalidPlayerActionException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.serverInterface.EndGameEvent;
import it.polimi.ingsw.network.serverInterface.ServerEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController {
    private final Server server;
    private final List<Player> players;
    private final Map<String, ClientManagerSocket> clientManagers;
    private Game game;
    private TurnController turnController;

    public GameController(Server server, List<Player> players, Map<String, ClientManagerSocket> clientManagers) {
        this.server = server;
        this.players = players;
        this.clientManagers = clientManagers;
    }

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

        turnController = new TurnController(this, players.size());
        turnController.startPlacementPhase(game.getBoard().getTurnOrderTile());
        /*Player firstPlayer = game.getBoard().getTurnOrderTile().getSlots().getFirst();
        ClientManagerSocket firstCM = clientManagers.get(firstPlayer.getName());*/
        // gestirò in TurnController
    }

    public synchronized void placeTotem(String playerName, char letter) {
        Player p = getPlayerByName(playerName);
        if(p == null)
            return;
        OfferTile tile = getOfferTileByLetter(letter);
        if(tile == null)
            return;

        try {
            game.placeTotem(p, tile);
        } catch(InvalidPlayerActionException e) {
            //
        }

    }


    // scrivere il costruttore di MoveTotemEvent
    void sendMoveTotem(String playerName) {
        ClientManagerSocket cm = clientManagers.get(playerName);
        if(cm != null) {
            //cm.sendEvent(new MoveTotemEvent(game.getBoard().getOfferTrack(),
            // game.getBoard().getTurnOrderTile()));
        }
    }

    // helper methods to access players or tiles into the previous methods
    private void broadcastEvent(ServerEvent serverEvent) {
        for(ClientManagerSocket cm : clientManagers.values()) {
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
