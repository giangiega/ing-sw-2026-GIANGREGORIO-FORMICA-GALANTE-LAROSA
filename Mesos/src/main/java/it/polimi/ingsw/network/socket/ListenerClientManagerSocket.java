package it.polimi.ingsw.network.socket;

import com.google.gson.Gson;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.clientInterface.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Ale
 * this class has to take the messages from the clients and call the right operation class to
 * modify the model, ListenerClietnManager receives ClientOperation objects
 */

public class ListenerClientManagerSocket {
    private final Server server;
    private final ClientManagerSocket clientManagerSocket;
    private final BufferedReader input;
    private final Gson gson;

    /**
     * Constructor with a RuntimeTypeAdapterFactory used for creating the correct class
     * of a specific operation
     * @param clientSocket client's socket
     * @param clientManagerSocket client's manager
     * @throws IOException thrown exception
     */
    public ListenerClientManagerSocket(Server server, Socket clientSocket, ClientManagerSocket clientManagerSocket) throws IOException {
        this.server = server;
        this.clientManagerSocket = clientManagerSocket;
        this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.gson = GsonFactory.clientOperationGson();
    }

    /**
     * @throws IOException thrown exception
     * This method reads the input buffer and creates the correct operation class based on the json message
     */
    public void startClientListener() throws IOException {
        try{
            String jsonString;

            while((jsonString = input.readLine()) != null){
                //create correct operation class based on json field "op"
                ClientOperation clientOperation = gson.fromJson(jsonString, ClientOperation.class);
                clientOperation.executeOp(server, clientManagerSocket);
            }
        }finally{
            //If stopSilently() was called because the player reconnected via a different
            //protocol and a new ClientConnection replaced this one,
            //no reconnection needed to avoid re-adding the player to disconnectedPlayers.
            if (!clientManagerSocket.isInvalidated()){
                server.handleDisconnection(clientManagerSocket.getPlayerName());
            }
        }
    }
}
