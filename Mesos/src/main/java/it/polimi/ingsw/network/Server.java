package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TurnOrderTile;

import java.util.List;
import java.util.Map;

public class Server {
    private GameController gameController;
    private final int numPlayers;
    private LobbyController lobbyController;
    private TurnController turnController;

    public Server(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public void startListening() {}

    public void fullLobby(List<Player> lobbyPlayers, Map<String, ClientManagerSocket> clientManagers) {
        gameController = new GameController(this, lobbyPlayers, clientManagers);
        gameController.startGame();
    }

    public void login(String namePlayer, ColorEnum totemColor, ClientManagerSocket cm) {
        lobbyController.addPlayer(namePlayer, totemColor, cm);
    }

    public void placeTotem(String namePlayer , TurnOrderTile ){
        turnController.startPlacementPhase(index);
    }



}
