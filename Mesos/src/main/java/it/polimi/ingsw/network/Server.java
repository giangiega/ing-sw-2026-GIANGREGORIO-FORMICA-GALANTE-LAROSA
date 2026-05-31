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
        if(lobbyController != null)
            getLobbyController().setServer(this);
    }

    /**
     * accept clients' requests of connection to server, creates a ClientManagerSocket and a
     * ListenerClientManagerSocket for each client, then starts the client's listener
     * @param port
     */
    public void startListening(int port) {
        // Recovery: setServer was never called since NumPlayersOperation is skipped
        if (ServerClass.isRecoveryPending() && lobbyController != null) {
            getLobbyController().setServer(this);
        }
        ServerClass.connected = 0; // reset for first player

        //when all players are connected this try close the serverSocket to refuse other eventual connections
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    int id = ServerClass.connected;
                    ClientManagerSocket clientManagerSocket = new ClientManagerSocket(clientSocket);
                    ListenerClientManagerSocket listener = new ListenerClientManagerSocket(this, clientSocket, clientManagerSocket);
                    synchronized (ServerClass.clientManagers){
                        ServerClass.clientManagers.put(id, clientManagerSocket);
                    }

                    new Thread(() -> {
                        try {
                            listener.startClientListener();
                        } catch (IOException e) {
                            System.err.println("[Server] Client handler I/O error: " + e.getMessage());
                        } finally {
                            handleSocketClientDrop(clientManagerSocket, id);

                            try { clientSocket.close(); } catch (IOException ignored) {}
                        }
                    }, "client-handler-" + clientSocket.getPort()).start();

                    boolean isFirst = ServerClass.connected == 0 && !ServerClass.isRecoveryPending();
                    clientManagerSocket.sendEvent(new AckEvent(isFirst));
                    ServerClass.connected++;
                } catch (IOException e) {
                    if (!Thread.currentThread().isInterrupted()) {
                        System.err.println("[Server] Accept error: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Server] Fatal socket error: " + e.getMessage());
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
        if (playerName == null) {
            return;
        }
        if (gameController != null) {
            gameController.handleDisconnection(playerName);
        } else if (lobbyController != null) {
            lobbyController.handleLobbyDisconnection(playerName);
        }
    }

    private void handleSocketClientDrop(ClientManagerSocket cm, int id) {
        String name = cm != null ? cm.getPlayerName() : null;
        boolean wasMaster = false;

        synchronized (ServerClass.clientManagers) {
            if (!ServerClass.clientManagers.isEmpty()) {
                wasMaster = (id == java.util.Collections.min(ServerClass.clientManagers.keySet()));
            }

            ServerClass.clientManagers.remove(id);
        }

        if (name != null) {
            handleDisconnection(name);
        }
        //disconnection pre-lobby
        else if (lobbyController == null) {
            synchronized (ServerClass.clientManagers) {
                if (!ServerClass.clientManagers.isEmpty()) {
                    if (wasMaster) {
                        // Troviamo il giocatore rimasto con l'ID più basso e lo promuoviamo
                        int nextMasterId = java.util.Collections.min(ServerClass.clientManagers.keySet());
                        ClientConnection nextMaster = ServerClass.clientManagers.get(nextMasterId);

                        if (nextMaster != null) {
                            // Gli mandiamo TRUE per sbloccargli la schermata del numero giocatori
                            nextMaster.sendEvent(new AckEvent(true));
                        }
                    }
                } else {
                    //no players in lobby, reset server
                    needReset = true;
                    resetServer();
                }
            }
        }
    }
}




