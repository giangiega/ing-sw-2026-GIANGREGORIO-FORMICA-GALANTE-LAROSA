package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.LobbyController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Ale
 * this class has to take the messages from the clients and call the right operation class to
 * modify the model
 */

public class ListenerClientManagerSocket {
    private final LobbyController lobbyController;
    private final Socket clientSocket;
    private final ClientManagerSocket clientManagerSocket;
    private final BufferedReader input;

    /**
     * constructor
     * @param lobbyController
     * @param clientSocket
     * @param clientManagerSocket
     * @throws IOException
     */
    public ListenerClientManagerSocket(LobbyController lobbyController, Socket clientSocket, ClientManagerSocket clientManagerSocket) throws IOException {
        this.lobbyController = lobbyController;
        this.clientSocket = clientSocket;
        this.clientManagerSocket = clientManagerSocket;
        this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }


}
