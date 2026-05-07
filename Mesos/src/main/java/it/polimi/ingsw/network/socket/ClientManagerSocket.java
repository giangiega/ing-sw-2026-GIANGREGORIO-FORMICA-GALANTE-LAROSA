package it.polimi.ingsw.network.socket;
import com.google.gson.Gson;
import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.network.serverInterface.ServerEvent;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.IOException;

/**
 * @author Ale
 * this class has to send at clients the update of them view after server side checks,
 * ClientManagerSocket sends ServerEvent objects
 */

public class ClientManagerSocket implements ClientConnection {
    private final PrintWriter out;
    private String PlayerName;
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
        this.gson = GsonFactory.serverEventGson();
    }

    /**
     * serialize a json message and send it to client to execute a specific operation
     * @param serverEvent
     */
    public void sendEvent(ServerEvent serverEvent) {
        String jsonMessage = gson.toJson(serverEvent, ServerEvent.class);
        out.println(jsonMessage);
    }

    public void setPlayerName(String PlayerName) {
        this.PlayerName = PlayerName;
    }
    public String getPlayerName(){
        return PlayerName;
    }
}
