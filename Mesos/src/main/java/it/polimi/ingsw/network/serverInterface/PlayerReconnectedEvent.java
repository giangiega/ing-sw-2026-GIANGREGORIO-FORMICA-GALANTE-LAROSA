/**
 * @author Giuse
 */
package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;

public class PlayerReconnectedEvent implements ServerEvent {
    private final String playerName;

    /**
     * @param playerName : name of the player who returned to the game
     * Constructor of this class: it assigns the name of the player who returned to the game
     */
    public PlayerReconnectedEvent(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.showPlayerReconnected(playerName);
    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onPlayerReconnected(playerName);
    }
}