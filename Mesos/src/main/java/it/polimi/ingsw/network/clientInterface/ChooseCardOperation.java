package it.polimi.ingsw.network.clientInterface;

import it.polimi.ingsw.network.RMI.RmiClient;
import it.polimi.ingsw.network.RMI.VirtualServer;
import it.polimi.ingsw.network.socket.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

import java.rmi.RemoteException;
import java.util.List;

public class ChooseCardOperation implements ClientOperation {
    private final List<Integer> upperCards;
    private final List<Integer>  lowerCards;
    private final List<Integer> upperBuildings;
    private final List<Integer> lowerBuildings;

    public ChooseCardOperation(List<Integer> upperCards, List<Integer> lowerCards, List<Integer> upperBuildings, List<Integer> lowerBuildings) {
        this.upperCards = upperCards;
        this.lowerCards = lowerCards;
        this.upperBuildings = upperBuildings;
        this.lowerBuildings = lowerBuildings;
    }


    @Override
    public void executeOp(Server server, ClientManagerSocket cm) {
        server.getGameController().resolveAction(cm.getPlayerName(),upperCards,lowerCards,upperBuildings,lowerBuildings);
    }
    /**
     * @author Giuse
     * @param server : RMI server
     * @param client : RMI client
     * @throws RemoteException
     * This method send the "operation" chooseCard thanks to the RMI protocol
     */
    @Override
    public void sendViaRmi(VirtualServer server, RmiClient client) throws RemoteException {
        server.chooseCard(client, upperCards, lowerCards, upperBuildings, lowerBuildings);
    }
}
