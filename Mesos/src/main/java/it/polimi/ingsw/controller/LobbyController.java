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
    private boolean gameStarted = false;

    public LobbyController(Server server, int numPlayers) {
        this.server = server;
        this.numPlayers = numPlayers;
    }
    /**
     * Manages the synchronized player's login.
     * Checks that the chosen name and totem color are valid.
     * Sends a LoggedEvent and calls fullLobby at the end, where GameController is created,
     */
    public synchronized void addPlayer(String name, ColorEnum color, ClientManagerSocket cm) {

        if (gameStarted || lobbyPlayers.size() >= numPlayers) {
            cm.sendEvent(new LoggedEvent(false, name, color, new ArrayList<>()));
            return;
        }

        for (Player p : lobbyPlayers) {
            if (p.getName().equals(name) || p.getTotemColor().equals(color)){
                cm.sendEvent(new LoggedEvent(false, name, color, new ArrayList<>()));
                return;
            }

        }
        Player player = new Player(name, color);
        lobbyPlayers.add(player);
        clientManagers.put(name, cm);
        List<String>  playerNames = new ArrayList<>();
        for(Player p : lobbyPlayers) {
            playerNames.add(p.getName());
        }

        cm.sendEvent(new LoggedEvent(true, name, color,playerNames));

        if(lobbyPlayers.size() == numPlayers) {
            gameStarted = true;
            server.fullLobby(lobbyPlayers, clientManagers);
        }

    }

}
