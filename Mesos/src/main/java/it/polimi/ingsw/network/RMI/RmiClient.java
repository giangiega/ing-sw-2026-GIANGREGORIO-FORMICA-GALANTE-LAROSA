package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.io.IOException;

public class RmiClient implements VirtualView {

    private final String host;
    private final int port;

    public RmiClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect(ViewInterface view) throws IOException {}

}
