package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;

public class UpdateRoundEvent implements ServerEvent {
    private final int currentRound;

    public UpdateRoundEvent(int currRound) {
        this.currentRound = currRound;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.updateRound(currentRound);
    }

    @Override
    public void updateViewRmi(VirtualView view) throws RemoteException {

    }
}
