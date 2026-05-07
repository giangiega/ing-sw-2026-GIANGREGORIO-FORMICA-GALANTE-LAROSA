package it.polimi.ingsw.network;

import it.polimi.ingsw.network.serverInterface.ServerEvent;

public interface ClientConnection {
    public void sendEvent(ServerEvent serverEvent);
}
