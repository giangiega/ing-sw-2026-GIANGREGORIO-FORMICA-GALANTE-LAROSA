package it.polimi.ingsw.network.clientInterface;
import it.polimi.ingsw.network.RMI.RmiClient;
import it.polimi.ingsw.network.RMI.VirtualServer;
import it.polimi.ingsw.network.socket.ClientManagerSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.network.serverInterface.LoggedEvent;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class LoginOperation implements ClientOperation {

    private final String namePlayer;
    private final ColorEnum totemColor;

    public LoginOperation(String namePlayer, ColorEnum totemColor) {
        this.namePlayer = namePlayer;
        this.totemColor = totemColor;
    }
    @Override
    public void executeOp(Server server, ClientManagerSocket cm) {
        if (server.getLobbyController() == null) {
            LoggedEvent loginWithoutNumPlayerChosen = new LoggedEvent(false, namePlayer,
                    totemColor, new ArrayList<>());
            loginWithoutNumPlayerChosen.setNumPlayersChosen(false);
            cm.sendEvent(loginWithoutNumPlayerChosen);
            return;
        }
        server.getLobbyController().addPlayer(namePlayer,totemColor,cm);
    }

    /**
     * @author Giuse
     * @param server : RMI server
     * @param client : RMI client
     * @throws RemoteException
     * This method send the "operation" login thanks to the RMI protocol
     */
    @Override
    public void sendViaRmi(VirtualServer server, RmiClient client) throws RemoteException {
        server.login(namePlayer, totemColor, client);
    }
}
