package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;
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
            if(lobbyPlayers.size() >= 2)
                view.invalidChoice("The lobby is full");
            else {
                view.invalidChoice("Name or color already used");
                view.askLogin();
            }

        }
    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onLogged(result, name, color, lobbyPlayers);
    }
}
