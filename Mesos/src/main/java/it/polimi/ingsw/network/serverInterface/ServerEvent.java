package it.polimi.ingsw.network.serverInterface;

import com.google.gson.JsonObject;
import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.ViewInterface;

public interface ServerEvent {
    public void updateView(ViewInterface view);
}

