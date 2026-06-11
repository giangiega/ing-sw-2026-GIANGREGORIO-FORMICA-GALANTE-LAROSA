package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;

public interface ServerEvent {
    public void updateView(ViewInterface view);

    /**
     * This method calls the right rmi remote method of RmiClient based on the type of ServerEvent
     * @param client: is a RmiClient
     * @throws RemoteException Exception thrown by RMI protocol
     */
    public void updateViewRmi(VirtualView client) throws RemoteException;
}

