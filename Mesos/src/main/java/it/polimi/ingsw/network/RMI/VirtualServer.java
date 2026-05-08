package it.polimi.ingsw.network.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServer extends Remote {
    public void initLobby(int numPlayers) throws RemoteException;
    public void connect(RmiClient view) throws RemoteException;
}
