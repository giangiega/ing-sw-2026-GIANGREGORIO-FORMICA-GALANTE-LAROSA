package it.polimi.ingsw.network.clientInterface;

import it.polimi.ingsw.network.RMI.RmiClient;
import it.polimi.ingsw.network.RMI.VirtualServer;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.socket.ClientManagerSocket;

import java.rmi.RemoteException;

public class BuildingUpperRowChoiceOperation implements ClientOperation{
    private final int chosenIndex;
    private final boolean chosenIsBuilding;

    public BuildingUpperRowChoiceOperation(int chosenIndex, boolean chosenIsBuilding) {
        this.chosenIndex = chosenIndex;
        this.chosenIsBuilding = chosenIsBuilding;
    }

    @Override
    public void executeOp(Server server, ClientManagerSocket cm) {
        server.getGameController()
                .submitBuildingUpperRowChoice(cm.getPlayerName(), chosenIndex, chosenIsBuilding);
    }

    @Override
    public void sendViaRmi(VirtualServer server, RmiClient client) throws RemoteException {
        server.buildingUpperRowChoice(client, chosenIndex, chosenIsBuilding);
    }
}
