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
    private TurnController turnController;

    public Server(int numPlayers) {
        this.numPlayers = numPlayers;
        this.lobbyController = new LobbyController(this, numPlayers);
    }

    public void startListening(int port) {
        //when all players are connected this try close the serverSocket to refuse other eventual connections
        try(ServerSocket serverSocket = new ServerSocket(port)){
           int connected = 0;
           while (connected < numPlayers){
               Socket clientSocket = serverSocket.accept();
               ClientManagerSocket clientManagerSocket = new ClientManagerSocket(clientSocket);
               ListenerClientManagerSocket listenerClientManagerSocket = new ListenerClientManagerSocket(lobbyController, clientSocket, clientManagerSocket);

               //ListenerClientManagerSocket.start();
               connected++;
           }
        }catch (IOException e){
            System.err.println("Server error: " + e.getMessage());
        }
    }

    public void fullLobby(List<Player> lobbyPlayers, Map<String, ClientManagerSocket> clientManagers) {
        gameController = new GameController(this, lobbyPlayers, clientManagers);
        gameController.startGame();
    }

    public void login(String namePlayer, ColorEnum totemColor, ClientManagerSocket cm) {
        lobbyController.addPlayer(namePlayer, totemColor, cm);
    }

    public void placeTotem(String PlayerName, int index){}

}




