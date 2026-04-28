package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.ViewInterface;
import java.util.ArrayList;
import java.util.List;

public class LoggedEvent implements ServerEvent {
    private final String name;
    private final String color;
    private boolean result;
    private final List<String> lobbyPlayers;

    public LoggedEvent(boolean result, String name, ColorEnum color, List<String> lobbyPlayers) {
        this.name = name;
        this.color = color.toString();
        this.result = result;
        this.lobbyPlayers = lobbyPlayers;
    }

    @Override
    public void updateView(ViewInterface view){
        if(result){
            view.showLobby(lobbyPlayers);
        }else{
            view.invalidChoice("Name or color already used");
        }

    }
}
