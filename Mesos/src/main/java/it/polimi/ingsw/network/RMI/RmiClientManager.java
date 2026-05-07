package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.network.serverInterface.ServerEvent;

import java.rmi.RemoteException;

/**
 * @author Ale
 */
public class RmiClientManager implements ClientConnection {
    RmiClient client;

    public RmiClientManager(RmiClient client){
        this.client = client;
    }

    @Override
    public void sendEvent(ServerEvent serverEvent){
        try{
            serverEvent.updateViewRmi(client);
        }catch (RemoteException e){
            //gestire eccezione
        }
    }
}
