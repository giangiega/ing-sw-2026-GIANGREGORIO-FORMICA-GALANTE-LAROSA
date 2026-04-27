package it.polimi.ingsw.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.network.clientInterface.*;
import it.polimi.ingsw.network.serverInterface.EndGameEvent;
import it.polimi.ingsw.network.serverInterface.LoggedEvent;
import it.polimi.ingsw.network.serverInterface.MoveTotemEvent;
import it.polimi.ingsw.network.serverInterface.ServerEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    private final RuntimeTypeAdapterFactory<ClientOperation> factory;
    private final GsonBuilder builderGson;
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
        this.factory = RuntimeTypeAdapterFactory.of(ClientOperation.class, "op");
        factory.registerSubtype(BuildingChoiceOperation.class, "buildingChoiceOperation");
        factory.registerSubtype(ChooseCardOperation.class, "chooseCardOperation");
        factory.registerSubtype(LoginOperation.class, "loginOperation");
        factory.registerSubtype(PlaceTotemOperation.class, "placeTotemOperation");
        this.builderGson = new GsonBuilder();
        builderGson.registerTypeAdapterFactory(factory);
        this.gson = builderGson.create();
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
