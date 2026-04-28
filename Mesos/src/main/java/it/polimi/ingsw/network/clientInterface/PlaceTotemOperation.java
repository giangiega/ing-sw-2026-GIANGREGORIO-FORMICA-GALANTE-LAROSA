package it.polimi.ingsw.network.clientInterface;

import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

public class PlaceTotemOperation implements ClientOperation {
    private int index;

    @Override
    public void executeOp(Server server, ClientManagerSocket cm) {
       //server.getTurnController().metodo che gestisce il placement;
    }
}
