package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Player;

import java.util.List;
import java.util.Map;

public class Server {
    private GameController gameController;
    private final int numPlayers;

    public Server(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public void startListening() {}

    public void fullLobby(List<Player> lobbyPlayers, Map<String, ClientManagerSocket> clientManagers) {
        gameController = new GameController(this, lobbyPlayers, clientManagers);
        gameController.startGame();
    }



}
