package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;

public interface ServerEvent {
    public void updateView(ViewInterface view);

    /**
     * this method calls the right rmi methods based on the type of ServerEvent
     * @param view: is a RmiClient
     * @throws RemoteException
     */
    public void updateViewRmi(VirtualView view) throws RemoteException;
}

