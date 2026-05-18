/**
 * @author Giuse
 */
package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;

/**
 * Sent to all clients when a second player reconnects and the suspended game can continue.
 */
public class GameResumedEvent implements ServerEvent {

    @Override
    public void updateView(ViewInterface view) {
        view.showGameResumed();
    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onGameResumed();
    }
}
