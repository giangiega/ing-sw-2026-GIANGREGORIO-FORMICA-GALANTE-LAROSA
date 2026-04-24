package it.polimi.ingsw.network;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private Game game;
    private final int numPlayers;
    private final List<Player> lobbyPlayers = new ArrayList<>();
    private final Map<String, ClientManagerSocket> clientManagers = new HashMap<>();


    public Server(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    /**
     *
     * @param name
     * @param color
     * @param cm
     * if the
     */
    public synchronized void addPlayer(String name, ColorEnum color, ClientManagerSocket cm) {
        boolean invalidName = false;
        boolean invalidColor = false;

        for (Player lobbyPlayer : lobbyPlayers) {
            if (lobbyPlayer.getName().equals(name))
                invalidName = true;
            if (lobbyPlayer.getTotemColor().equals(color))
                invalidColor = true;
        }

        if (invalidName || invalidColor) {
            cm.logged(false, name, color);
            return;
        } // capire come gestire a schermo l'invalidità

        Player player = new Player(name, color);
        lobbyPlayers.add(player);
        clientManagers.put(name, cm);
        cm.logged(true, name, color);

        if(lobbyPlayers.size() == numPlayers)
            startGame();

    }

    private void startGame() {
        GameConfig config = GameConfig.create(numPlayers);

        CardFactory cardFactory = new CardFactory();
        Deck tribeDeck = cardFactory.buildTribeDeck(config);
        BuildingDeck bd1 = cardFactory.buildBuildingDeck(EraEnum.I, config);
        BuildingDeck bd2 = cardFactory.buildBuildingDeck(EraEnum.II, config);
        BuildingDeck bd3 = cardFactory.buildBuildingDeck(EraEnum.III, config);

        Board board = new Board(config, tribeDeck, bd1, bd2, bd3);
        game = new Game(new ArrayList<>(lobbyPlayers), board, config);
        game.startGame();

        Player firstPlayer = game.getBoard().getTurnOrderTile().getSlots().getFirst();

        ClientManagerSocket firstCM = clientManagers.get(firstPlayer.getName());
        firstCM.moveTotem(game.getBoard().getOfferTrack(), game.getBoard().getTurnOrderTile());
    }

}
