package it.polimi.ingsw.network.clientInterface;

import it.polimi.ingsw.network.RMI.VirtualServer;
import it.polimi.ingsw.network.socket.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

import java.rmi.RemoteException;

public interface ClientOperation {
    public void executeOp(Server server, ClientManagerSocket cm);
    public void sendViaRmi(VirtualServer server) throws RemoteException;
}
