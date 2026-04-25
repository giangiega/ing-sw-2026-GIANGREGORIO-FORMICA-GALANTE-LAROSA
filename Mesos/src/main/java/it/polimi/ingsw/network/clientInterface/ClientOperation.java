package it.polimi.ingsw.network.clientInterface;

import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

public interface ClientOperation {
    public void executeOp(Server server, ClientManagerSocket cm);
}
