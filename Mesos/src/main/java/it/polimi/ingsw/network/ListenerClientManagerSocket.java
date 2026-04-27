package it.polimi.ingsw.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import it.polimi.ingsw.controller.LobbyController;
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
 * modify the model
 */

public class ListenerClientManagerSocket {
    private final Server server;
    private final LobbyController lobbyController;
    private final Socket clientSocket;
    private final ClientManagerSocket clientManagerSocket;
    private final BufferedReader input;
    private final RuntimeTypeAdapterFactory<ServerEvent> factory;
    private final GsonBuilder builderGson;
    private final Gson gson;

    /**
     * constructor
     * @param lobbyController
     * @param clientSocket
     * @param clientManagerSocket
     * @throws IOException
     */
    public ListenerClientManagerSocket(Server server, LobbyController lobbyController, Socket clientSocket, ClientManagerSocket clientManagerSocket) throws IOException {
        this.server = server;
        this.lobbyController = lobbyController;
        this.clientSocket = clientSocket;
        this.clientManagerSocket = clientManagerSocket;
        this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.factory = RuntimeTypeAdapterFactory.of(ServerEvent.class, "op");
        factory.registerSubtype(EndGameEvent.class, "endGameEvent");
        factory.registerSubtype(LoggedEvent.class, "loggedEvent");
        factory.registerSubtype(MoveTotemEvent.class, "moveTotemEvent");
        this.builderGson = new GsonBuilder();
        builderGson.registerTypeAdapterFactory(factory);
        this.gson = builderGson.create();
    }

    public void startClientListener() throws IOException {
        String jsonString;

        while((jsonString = input.readLine()) != null){
            //create correct operation class based on json field "op"
            ServerEvent event = gson.fromJson(jsonString, ServerEvent.class);
            event.executeOp(server, clientManagerSocket);
        }
    }


}
