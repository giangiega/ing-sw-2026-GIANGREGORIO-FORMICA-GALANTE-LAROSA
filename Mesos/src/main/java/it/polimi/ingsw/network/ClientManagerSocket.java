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
 * this class has to send at clients the update of them view after server side checks
 */

public class ClientManagerSocket {
    private final Socket clientSocket;
    private final PrintWriter out;
    private String PlayerName;
    //server manda ServerEvent e il listener riceve ClientOperation
    private final RuntimeTypeAdapterFactory<ServerEvent> factory;
    private final GsonBuilder gsonBuilder;
    private final Gson gson;

    // autoFlush = true sends the message immediately after every println
    public ClientManagerSocket(Socket socket) throws IOException {
        this.clientSocket = socket;
        //da capire se va bene così o serve il costruttore di InputStreamReader
        this.out = new PrintWriter(socket.getOutputStream(), true );
        this.factory = RuntimeTypeAdapterFactory.of(ServerEvent.class, "op");
        factory.registerSubtype(EndGameEvent.class, "endGameEvent");
        factory.registerSubtype(LoggedEvent.class, "loggedEvent");
        factory.registerSubtype(MoveTotemEvent.class, "moveTotemEvent");
        this.gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapterFactory(factory);
        this.gson = new Gson();
    }

    public void sendEvent(ServerEvent serverEvent) {
        JsonObject json = gson.toJsonTree(serverEvent).getAsJsonObject();
        json.addProperty("op", "");
    }

    public void setPlayerName(String PlayerName) {
        this.PlayerName = PlayerName;
    }
    public String getPlayerName(){
        return PlayerName;
    }
}
