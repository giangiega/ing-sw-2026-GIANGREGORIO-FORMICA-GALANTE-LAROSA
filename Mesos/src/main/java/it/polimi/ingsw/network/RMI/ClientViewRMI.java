/**
 * @author Giuse
 */
package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.network.ClientSender;
import it.polimi.ingsw.network.clientInterface.ClientOperation;

import java.rmi.RemoteException;


public class ClientViewRMI implements ClientSender {

    private final VirtualServer server;
    private final RmiClient client;

    /**
     * @param server the remote server stub obtained from the RMI registry
     * Constructor of this class
     */
    public ClientViewRMI(VirtualServer server, RmiClient client) {
        this.server = server;
        this.client = client;
    }

    /**
     * @param operation : the action the player just performed
     * Forwards the player's action to the server via RMI.
     * Any RemoteException is handled here
     */
    @Override
    public void sendOperation(ClientOperation operation) {
        try {
            operation.sendViaRmi(server, client);
        } catch (RemoteException e) {
            System.err.println("[ClientViewRMI] Failed to send "
                    + operation.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}