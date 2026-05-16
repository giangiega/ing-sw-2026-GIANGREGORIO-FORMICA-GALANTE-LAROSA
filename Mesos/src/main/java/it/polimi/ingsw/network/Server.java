package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.network.serverInterface.AckEvent;
import it.polimi.ingsw.network.socket.ClientManagerSocket;
import it.polimi.ingsw.network.socket.ListenerClientManagerSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Ale
 * this class accepts clients' requests of connection to the server after specifying a numPlayers
 * for the game
 */

public class Server extends ServerClass{

    public synchronized void initLobbySocket(int numPlayers) {
        this.initLobby(numPlayers);
        getLobbyController().setServer(this);
    }

    /**
     * accept clients' requests of connection to server, creates a ClientManagerSocket and a
     * ListenerClientManagerSocket for each client, then starts the client's listener
     * @param port
     */
    public void startListening(int port) {
        //when all players are connected this try close the serverSocket to refuse other eventual connections
        try(ServerSocket serverSocket = new ServerSocket(port)){
           System.out.println("Server listening on port " + port);

           while (true){
               Socket clientSocket = serverSocket.accept();
               ClientManagerSocket clientManagerSocket = new ClientManagerSocket(clientSocket);
               ListenerClientManagerSocket listenerClientManagerSocket = new ListenerClientManagerSocket(this, clientSocket, clientManagerSocket);

               new Thread(() -> {
                   try{
                       listenerClientManagerSocket.startClientListener();
                   }catch (IOException e){
                       throw new RuntimeException("Client disconnected");
                   }
               }).start();

               clientManagerSocket.sendEvent(new AckEvent(connected == 0));
               connected++; // only first player receive "true" , he inserts numPlayers
           }
        }catch (IOException e){
            System.err.println("Server error: " + e.getMessage());
        }
    }

    @Override
    public LobbyController getLobbyController(){
        return this.lobbyController;
    }

    @Override
    public GameController getGameController(){
        return this.gameController;
    }

    /**
     * @author Giuse
     * @param playerName : name of the player who disconnected
     * This method checks to see if the gameController exists, then
     * it calls its method: handleDisconnection
     */
    @Override
    public void handleDisconnection(String playerName){
        if (playerName == null) return;
        if (gameController != null) {
            gameController.handleDisconnection(playerName);
        } else if (lobbyController != null) {//Game hasn't started yet, clean lobby
            resetServer();
        }
    }
}




