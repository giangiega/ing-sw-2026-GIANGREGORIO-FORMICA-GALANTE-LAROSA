package it.polimi.ingsw.network;

import it.polimi.ingsw.network.serverInterface.ServerEvent;

public interface ClientConnection {
    public void sendEvent(ServerEvent serverEvent);
    public void setPlayerName(String name);
    public String getPlayerName();
}
