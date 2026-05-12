package it.polimi.ingsw.network.clientInterface;

import it.polimi.ingsw.network.RMI.RmiClient;
import it.polimi.ingsw.network.RMI.VirtualServer;
import it.polimi.ingsw.network.socket.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

import java.rmi.RemoteException;

public class NumPlayersOperation implements ClientOperation {
    private final int numPlayers;

    public NumPlayersOperation(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    @Override
    public void executeOp(Server server, ClientManagerSocket cm) {
        if (numPlayers < 2 || numPlayers > 5) return;
        server.initLobbySocket(numPlayers);
    }
    /**
     * @author Giuse
     * @param server : RMI server
     * @param client : RMI client
     * @throws RemoteException
     * This method send the "operation" numPlaterChoice thanks to the RMI protocol
     */
    @Override
    public void sendViaRmi(VirtualServer server, RmiClient client) throws RemoteException {
        server.numPlayerChoice(numPlayers);
    }
}
