package it.polimi.ingsw.network.clientInterface;

import it.polimi.ingsw.network.RMI.RmiClient;
import it.polimi.ingsw.network.RMI.VirtualServer;
import it.polimi.ingsw.network.socket.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

import java.rmi.RemoteException;

public class PlaceTotemOperation implements ClientOperation {
    private final char position;

    public PlaceTotemOperation(char position) {
        this.position = position;
    }
    @Override
    public void executeOp(Server server, ClientManagerSocket cm) {
        server.getGameController().placeTotem(cm.getPlayerName(), position);

    }
    /**
     * @author Giuse
     * @param server : RMI server
     * @param client : RMI client
     * @throws RemoteException
     * This method send the "operation" placeTotem thanks to the RMI protocol
     */
    @Override
    public void sendViaRmi(VirtualServer server, RmiClient client) throws RemoteException {
        server.placeTotem(client, position);
    }
}
