package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.model.Player;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * this class would be an interface but RmiClient already implements an interface
 */
abstract public class ServerClass {
    protected static LobbyController lobbyController;
    protected static GameController gameController;
    protected static int connected = 0;

    public LobbyController getLobbyController(){
        return lobbyController;
    }
    public GameController getGameController(){
        return gameController;
    }

    public synchronized void initLobby(int numPlayers){
        if (lobbyController != null)
            return;
        lobbyController = new LobbyController(numPlayers);
    }

    public void fullLobby(List<Player> lobbyPlayers, Map<String, ClientConnection> clientManagers){
        if(gameController != null)
            return;
        gameController = new GameController(lobbyPlayers, clientManagers);
        gameController.startGame();
    }
}
