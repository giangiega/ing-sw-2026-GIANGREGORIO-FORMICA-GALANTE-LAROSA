package it.polimi.ingsw.network.serverInterface;

import com.google.gson.JsonObject;
import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

public interface ServerEvent {
    public void executeOp(Server server, ClientManagerSocket cm);
}
