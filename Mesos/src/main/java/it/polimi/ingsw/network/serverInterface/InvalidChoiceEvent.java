package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;

public class InvalidChoiceEvent implements ServerEvent {
    private final String message;

    public InvalidChoiceEvent(String message) {
        this.message = message;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.invalidChoice(message);
    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onInvalidChoice(message);
    }
}
