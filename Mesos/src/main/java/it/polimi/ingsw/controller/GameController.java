package it.polimi.ingsw.controller;

import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameController {
    private final Server server;
    private final List<Player> players;
    private final Map<String, ClientManagerSocket> clientManagers;
    private Game game;

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

        Player firstPlayer = game.getBoard().getTurnOrderTile().getSlots().getFirst();
        ClientManagerSocket firstCM = clientManagers.get(firstPlayer.getName());

    }
}
