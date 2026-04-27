package it.polimi.ingsw.network;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.OfferTile;
import it.polimi.ingsw.model.TurnOrderTile;
import it.polimi.ingsw.network.clientInterface.BuildingChoiceOperation;
import it.polimi.ingsw.network.clientInterface.ClientOperation;
import it.polimi.ingsw.network.serverInterface.EndGameEvent;
import it.polimi.ingsw.network.serverInterface.LoggedEvent;
import it.polimi.ingsw.network.serverInterface.MoveTotemEvent;
import it.polimi.ingsw.network.serverInterface.ServerEvent;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.util.List;

/**
 * @author Ale
 * this class has to send at clients the update of them view after server side checks,
 * ClientManagerSocket sends ServerEvent objects
 */

public class ClientManagerSocket {
    private final PrintWriter out;
    private String PlayerName;
    private final RuntimeTypeAdapterFactory<ServerEvent> factory;
    private final GsonBuilder gsonBuilder;
    private final Gson gson;

    /**
     * constructor with a RuntimeTypeAdapterFactory used for building the correct json message
     * for a specific operation
     * @param clientSocket
     * @throws IOException
     */
    // autoFlush = true sends the message immediately after every println
    public ClientManagerSocket(Socket clientSocket) throws IOException {
        this.out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
        this.factory = RuntimeTypeAdapterFactory.of(ServerEvent.class, "op");
        factory.registerSubtype(EndGameEvent.class, "endGameEvent");
        factory.registerSubtype(LoggedEvent.class, "loggedEvent");
        factory.registerSubtype(MoveTotemEvent.class, "moveTotemEvent");
        this.gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapterFactory(factory);
        this.gson = gsonBuilder.create();
    }

    /**
     * serialize a json message and send it to client to execute a specific operation
     * @param serverEvent
     */
    public void sendEvent(ServerEvent serverEvent) {
        String jsonMessage = gson.toJson(serverEvent);
        out.println(jsonMessage);
    }

    public void setPlayerName(String PlayerName) {
        this.PlayerName = PlayerName;
    }
    public String getPlayerName(){
        return PlayerName;
    }
}
