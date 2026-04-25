package it.polimi.ingsw.network;
import com.google.gson.JsonObject;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.OfferTile;
import it.polimi.ingsw.model.TurnOrderTile;
import it.polimi.ingsw.network.clientInterface.ClientOperation;
import it.polimi.ingsw.network.serverInterface.ServerEvent;

import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;
import java.util.List;

public class ClientManagerSocket {
    private final Socket socket;
    private final PrintWriter out;

    // autoFlush = true sends the message immediately after every println
    public ClientManagerSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendEvent(ServerEvent serverEvent) {}


}
