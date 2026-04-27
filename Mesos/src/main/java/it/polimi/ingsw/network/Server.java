package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.controller.TurnController;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TurnOrderTile;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * @author Ale
 * this class accepts clients' requests of connection to the server after specifying a numPlayers
 * for the game
 */

public class Server {
    private LobbyController lobbyController;
    private GameController gameController;
    private final int numPlayers;

    /**
     * constructor to build a server (so a game) based on numPlayers
     * @param numPlayers
     */
    public Server(int numPlayers) {
        this.numPlayers = numPlayers;
        this.lobbyController = new LobbyController(this, numPlayers);
    }

    /**
     * accept clients' requests of connection to server, creates a ClientManagerSocket and a
     * ListenerClientManagerSocket for each client, then starts the client's listener
     * @param port
     */
    public void startListening(int port) {
        //when all players are connected this try close the serverSocket to refuse other eventual connections
        try(ServerSocket serverSocket = new ServerSocket(port)){
           int connected = 0;
           Socket clientSocket = null;
           while (connected < numPlayers && (clientSocket = serverSocket.accept()) != null){
               //Socket clientSocket = serverSocket.accept();
               ClientManagerSocket clientManagerSocket = new ClientManagerSocket(clientSocket);
               ListenerClientManagerSocket listenerClientManagerSocket = new ListenerClientManagerSocket(this, clientSocket, clientManagerSocket);

               new Thread(() -> {
                   try{
                       listenerClientManagerSocket.startClientListener();
                   }catch (IOException e){
                       throw new RuntimeException();
                   }
               }).start();
               connected++;
           }
        }catch (IOException e){
            System.err.println("Server error: " + e.getMessage());
        }
    }

    /**
     * check if the lobby is completed to start the game
     * @param lobbyPlayers
     * @param clientManagers
     */
    public void fullLobby(List<Player> lobbyPlayers, Map<String, ClientManagerSocket> clientManagers) {
        gameController = new GameController(this, lobbyPlayers, clientManagers);
        gameController.startGame();
    }

    public LobbyController getLobbyController(){
        return this.lobbyController;
    }

    public GameController getGameController(){
        return this.gameController;
    }
}




