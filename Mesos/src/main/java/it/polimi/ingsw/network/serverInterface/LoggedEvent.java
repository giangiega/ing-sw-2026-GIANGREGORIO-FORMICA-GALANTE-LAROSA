package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

public class LoggedEvent implements ServerEvent {
    private final String name;
    private final String color;

    public LoggedEvent(boolean result, String name, ColorEnum color) {
        this.name = name;
        this.color = color.toString();
    }

    @Override
    public void executeOp(Server server, ClientManagerSocket cm) {
        //deve chiamare addPlayer di lobbyController per aggiungere il player alla mappa client-cm
    }
}
