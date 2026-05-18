/**
 * @author Giuse
 */
package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;

public class ReconnectedTotemEvent implements ServerEvent {
    private final ColorEnum originalColor;

    public ReconnectedTotemEvent(ColorEnum originalColor) {
        this.originalColor = originalColor;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.showReconnectedTotem(originalColor);
    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onReconnectedTotem(originalColor);
    }
}
