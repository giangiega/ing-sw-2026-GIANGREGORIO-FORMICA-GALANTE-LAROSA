package it.polimi.ingsw.controller;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.serverInterface.LoggedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyController {
    private final Server server;
    private final int numPlayers;
    private final List<Player> lobbyPlayers = new ArrayList<>();
    private final Map<String, ClientManagerSocket> clientManagers = new HashMap<>();

    public LobbyController(Server server, int numPlayers) {
        this.server = server;
        this.numPlayers = numPlayers;
    }

    public synchronized void addPlayer(String name, ColorEnum color, ClientManagerSocket cm) {
        for (Player p : lobbyPlayers) {
            if (p.getName().equals(name) || p.getTotemColor().equals(color)){
                cm.sendEvent(new LoggedEvent(false, name, color, new ArrayList<>()));
                return;
            }

        }

        Player player = new Player(name, color);
        lobbyPlayers.add(player);
        clientManagers.put(name, cm);
        cm.sendEvent(new LoggedEvent(true, name, color,new ArrayList<>()));

        if(lobbyPlayers.size() == numPlayers)
            server.fullLobby(lobbyPlayers, clientManagers);

    }

}
