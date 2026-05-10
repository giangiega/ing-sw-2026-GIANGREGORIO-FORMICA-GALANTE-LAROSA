package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.network.ClientSender;
import it.polimi.ingsw.network.clientInterface.ClientOperation;

public class ClientViewRMI implements ClientSender {

    @Override
    public void sendOperation(ClientOperation operation) {
        // gestione della init nelle view, così funzionano sia con Socket che con RMI
    }
}
