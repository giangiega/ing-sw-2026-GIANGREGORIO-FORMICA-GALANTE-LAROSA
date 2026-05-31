package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;

public class AckEvent implements ServerEvent{
    private final boolean isFirst;

    public AckEvent(boolean isFirst) {
        this.isFirst = isFirst;
    }

    @Override
    public void updateView(ViewInterface view){
        if(isFirst) {
            view.resetInputState();
            view.askNumPlayers();   // first player's login is called inside askNumPlayers()
        }
        else view.askLogin();
    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onAck(isFirst);
    }
}
