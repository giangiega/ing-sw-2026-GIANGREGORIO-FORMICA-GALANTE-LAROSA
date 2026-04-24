package it.polimi.ingsw.network;
import com.google.gson.JsonObject;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.OfferTile;
import it.polimi.ingsw.model.TurnOrderTile;

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

    public void logged(boolean result,  String name, ColorEnum color) {
        JsonObject message = new JsonObject();
        message.addProperty("op", "logged");
        message.addProperty("result", result);
        message.addProperty("name", name);
        message.addProperty("totemColor", color.toString());
        out.println(message);
    }

    public void moveTotem(List<OfferTile> offerTrack, TurnOrderTile turnOrderTile) {
        JsonObject message = new JsonObject();
        message.addProperty("op", "moveTotem");
        //continua
    }
}
