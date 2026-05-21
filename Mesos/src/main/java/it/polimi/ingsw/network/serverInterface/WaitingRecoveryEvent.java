package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;

public class WaitingRecoveryEvent implements ServerEvent {
    private final int playersStillNeeded;

    public WaitingRecoveryEvent(int playersStillNeeded) {
        this.playersStillNeeded = playersStillNeeded;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.showWaitingForRecovery(playersStillNeeded);
    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onWaitingForRecovery(playersStillNeeded);
    }
}
