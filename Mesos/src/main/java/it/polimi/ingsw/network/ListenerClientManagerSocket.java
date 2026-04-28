package it.polimi.ingsw.network;

import com.google.gson.Gson;
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
     * constructor with a RuntimeTypeAdapterFactory used for creating the correct class
     * of a specific operation
     * @param clientSocket
     * @param clientManagerSocket
     * @throws IOException
     */
    public ListenerClientManagerSocket(Server server, Socket clientSocket, ClientManagerSocket clientManagerSocket) throws IOException {
        this.server = server;
        this.clientManagerSocket = clientManagerSocket;
        this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.gson = GsonFactory.clientOperationGson();
    }

    /**
     * reads the input buffer and creates the correct operation class based on the json message
     * @throws IOException
     */
    public void startClientListener() throws IOException {
        String jsonString;

        while((jsonString = input.readLine()) != null){
            //create correct operation class based on json field "op"
            ClientOperation clientOperation = gson.fromJson(jsonString, ClientOperation.class);
            clientOperation.executeOp(server, clientManagerSocket);
        }
    }


}
