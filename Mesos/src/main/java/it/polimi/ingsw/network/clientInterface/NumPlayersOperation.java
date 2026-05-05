package it.polimi.ingsw.network.clientInterface;

import it.polimi.ingsw.network.socket.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

public class NumPlayersOperation implements ClientOperation {
    private final int numPlayers;

    public NumPlayersOperation(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    @Override
    public void executeOp(Server server, ClientManagerSocket cm) {
        if (numPlayers < 2 || numPlayers > 5) return;
        server.initLobby(numPlayers);
    }
}
