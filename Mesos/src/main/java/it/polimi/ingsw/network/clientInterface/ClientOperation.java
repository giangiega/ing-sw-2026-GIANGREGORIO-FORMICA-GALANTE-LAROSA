package it.polimi.ingsw.network.clientInterface;

import it.polimi.ingsw.network.socket.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

public interface ClientOperation {
    public void executeOp(Server server, ClientManagerSocket cm);
}
