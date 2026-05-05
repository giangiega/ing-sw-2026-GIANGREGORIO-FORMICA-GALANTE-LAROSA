package it.polimi.ingsw.network.clientInterface;

import it.polimi.ingsw.network.socket.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

public class PlaceTotemOperation implements ClientOperation {
    private final char position;

    public PlaceTotemOperation(char position) {
        this.position = position;
    }
    @Override
    public void executeOp(Server server, ClientManagerSocket cm) {
        server.getGameController().placeTotem(cm.getPlayerName(), position);

    }
}
