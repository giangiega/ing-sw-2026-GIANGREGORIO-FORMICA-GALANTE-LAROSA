package it.polimi.ingsw.network.clientInterface;

import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

public class PlaceTotemOperation implements ClientOperation {
    private int index;

    public void executeOp(Server server, ClientManagerSocket cm) {
       server.placeTotem(cm.getPlayerName(), index);
    }
}
