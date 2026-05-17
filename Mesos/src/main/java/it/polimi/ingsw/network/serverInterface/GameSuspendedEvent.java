/**
 * @author Giuse
 */
package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;

/**
 * Sent when only one player remains connected.
 * The game is paused until another player reconnects or the timeout expires.
 */
public class GameSuspendedEvent implements ServerEvent {
    private final int timeoutSeconds;

    /**
     * @param timeoutSeconds : amount of seconds to spent waiting
     * Constructor of this class: it assigns the amount of seconds spent waiting
     */
    public GameSuspendedEvent(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.showGameSuspended(timeoutSeconds);
    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onGameSuspended(timeoutSeconds);
    }
}