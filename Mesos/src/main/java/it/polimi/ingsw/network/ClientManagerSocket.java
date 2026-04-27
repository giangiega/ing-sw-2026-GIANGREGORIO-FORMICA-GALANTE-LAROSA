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

/**
 * @author Ale
 * this class has to send at clients the update of them view after server side checks
 */

public class ClientManagerSocket {
    private final Socket clientSocket;
    private final PrintWriter out;
    private String PlayerName;

    // autoFlush = true sends the message immediately after every println
    public ClientManagerSocket(Socket socket) throws IOException {
        this.clientSocket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendEvent(ServerEvent serverEvent) {}

    public void setPlayerName(String PlayerName) {
        this.PlayerName = PlayerName;
    }
    public String getPlayerName(){
        return PlayerName;
    }

}
