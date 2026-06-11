package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.enums.ColorEnum;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @author Ale
 * this interface contains the remote methods that will be calld by the client to do a
 * "client operation"
 */
public interface VirtualServer extends Remote {
    public void initLobby(int numPlayers) throws RemoteException;
    public void connect(VirtualView view) throws RemoteException;
    public void numPlayerChoice(int numPlayers) throws RemoteException;
    public void login(String name, ColorEnum color, VirtualView client) throws RemoteException;
    public void placeTotem(VirtualView client, char position) throws RemoteException;
    public void chooseCard(VirtualView client, List<Integer> upperCards, List<Integer> lowerCards,
                    List<Integer> upperBuildings, List<Integer> lowerBuildings) throws RemoteException;
    /**
     * @author Giuse
     * @param client  the calling client stub, used to identify the sender
     * This method is ì called periodically by RmiClient.
     * If this throws a RemoteException the connection is considered lost.
     */
    public void ping(VirtualView client) throws RemoteException;
}
