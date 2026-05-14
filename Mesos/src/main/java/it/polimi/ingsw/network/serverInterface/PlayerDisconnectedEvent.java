/**
 * @author Giuse
 */
package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;

public class PlayerDisconnectedEvent implements ServerEvent {
    private final String playerName;

    /**
     * @param playerName
     * Constructor of this class: assigns the disconnected player's name
     */
    public PlayerDisconnectedEvent(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.showPlayerDisconnected(playerName);
    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onPlayerDisconnected(playerName);
    }
}