package it.polimi.ingsw.network;

import it.polimi.ingsw.network.clientInterface.ClientOperation;

public interface ClientSender {
    void sendOperation(ClientOperation operation);
}
