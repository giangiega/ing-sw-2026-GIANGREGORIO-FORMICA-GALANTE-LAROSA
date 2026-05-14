package it.polimi.ingsw.controller;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.network.ServerClass;
import it.polimi.ingsw.network.socket.ClientManagerSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.serverInterface.LoggedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyController {
    private ServerClass server;
    private final int numPlayers;
    private final List<Player> lobbyPlayers = new ArrayList<>();
    private final Map<String, ClientConnection> clientManagers = new HashMap<>();
    private boolean gameStarted = false;

    public LobbyController(int numPlayers) {
        this.numPlayers = numPlayers;
    }
    /**
     * Manages the synchronized player's login.
     * Checks that the chosen name and totem color are valid.
     * Sends a LoggedEvent and calls fullLobby at the end, where GameController is created,
     */
    public synchronized void addPlayer(String name, ColorEnum color, ClientConnection cm) {

        if(gameStarted){//Game has already started: the behaviour is different
            //Checking to see if the player was in the game
            String disconnectedPlayer = null;
            GameController gameController = server.getGameController();

            for(String S : gameController.getDisconnectedPlayers()){
                if(S.equals(name)){//The player has left the game and now is joining back
                    disconnectedPlayer = name;
                    break;
                }
            }
            if(disconnectedPlayer == null){//if it isn't a reconnection --> full lobby
                List<String> lobbyNames = new ArrayList<>();
                for(Player p : lobbyPlayers) lobbyNames.add(p.getName());
                cm.sendEvent(new LoggedEvent(false, name, color, lobbyNames));
            }else{//reconnection
                clientManagers.put(name, cm);
                cm.setPlayerName(name);
                server.handleDisconnection(name);
            }
            return;
        }else{//Game hasn't started yet-->normale behaviour
            if (lobbyPlayers.size() >= numPlayers) {
                List<String> lobbyNames = new ArrayList<>();
                for(Player p : lobbyPlayers)
                    lobbyNames.add(p.getName());
                cm.sendEvent(new LoggedEvent(false, name, color, lobbyNames));
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
            cm.setPlayerName(name);

            List<String>  playerNames = new ArrayList<>();
            for(Player p : lobbyPlayers)
                playerNames.add(p.getName());

            cm.sendEvent(new LoggedEvent(true, name, color,playerNames));

            if(lobbyPlayers.size() == numPlayers) {
                gameStarted = true;
                server.fullLobby(lobbyPlayers, clientManagers);
            }
        }
    }

    public void setServer(ServerClass server){
        if(this.server != null)
            return;
        this.server = server;
    }

}
