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
    private final Map<String, ColorEnum> ghostPlayers = new HashMap<>();

    public LobbyController(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    /**
     * @author Giuse
     * @param playerName : name of the player who disconnected
     * This method puts the name of the player who left the lobby in a map: ghostPlayers.
     * It makes possible to player who disconnected form the lobby to join back before the game starts
     * It isn't used once the game has started.
     * If the player is indeed a ghost: remove it from lobbyPlayers, clean the manager and
     * insert him in ghostPlayers map
     */
    public synchronized void handleLobbyDisconnection(String playerName) {
        Player ghost = null;
        for (Player p : lobbyPlayers) {
            if (p.getName().equals(playerName)) {
                ghost = p;
                break;
            }
        }
        if (ghost == null) return;//If it isn't a ghost player, nothing to do

        lobbyPlayers.remove(ghost);
        clientManagers.remove(playerName);
        ghostPlayers.put(playerName, ghost.getTotemColor());
    }

    /**
     * Manages the synchronized player's login.
     * Checks that the chosen name and totem color are valid.
     * Sends a LoggedEvent and calls fullLobby at the end, where GameController is created,
     */
    public synchronized void addPlayer(String name, ColorEnum color, ClientConnection cm) {

        if(gameStarted){//Game has already started: the behaviour is different
            GameController gameController = server.getGameController();
            if(gameController.getGameOver()){//Work with GameController to stop reconnection after the game is over
                List<String> lobbyNames = new ArrayList<>();
                for(Player p : lobbyPlayers) lobbyNames.add(p.getName());
                cm.sendEvent(new LoggedEvent(false, name, color, lobbyNames));
                return;
            }

            //Checking to see if the player was in the game: game is not over
            String disconnectedPlayer = null;
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
                ColorEnum originalColor = gameController.getPlayerColor(name);

                List<String> lobbyNames = new ArrayList<>();
                for(Player p : lobbyPlayers) lobbyNames.add(p.getName());
                cm.sendEvent(new LoggedEvent(true, name, originalColor, lobbyNames));

                cm.setPlayerName(name);
                gameController.handleReconnection(name, cm);
            }
            return;
        }else{//Game hasn't started yet-->normale behaviour
            if (lobbyPlayers.size() >= numPlayers) {//Already max capacity
                List<String> lobbyNames = new ArrayList<>();
                for(Player p : lobbyPlayers)
                    lobbyNames.add(p.getName());
                cm.sendEvent(new LoggedEvent(false, name, color, lobbyNames));
                return;
            }

            //Lobby is still not full: player can join
            if (ghostPlayers.containsKey(name)) {//Player was a ghost one
                ColorEnum originalColor = ghostPlayers.get(name);

                //Checking to see if the totem has been taken while player was offline
                boolean colorTaken = false;
                for(Player p : lobbyPlayers){
                    if(p.getTotemColor().equals(originalColor)){
                        colorTaken = true;
                    }
                }
                if (colorTaken) {//New login if the totem has been taken
                    ghostPlayers.remove(name);
                    cm.sendEvent(new LoggedEvent(false, name, originalColor, new ArrayList<>()));
                    return;
                }

                ghostPlayers.remove(name);
                Player player = new Player(name, originalColor);
                lobbyPlayers.add(player);
                clientManagers.put(name, cm);
                cm.setPlayerName(name);

                List<String> playerNames = new ArrayList<>();
                for (Player p : lobbyPlayers) playerNames.add(p.getName());
                cm.sendEvent(new LoggedEvent(true, name, originalColor, playerNames));

                if (lobbyPlayers.size() == numPlayers) {
                    gameStarted = true;
                    server.fullLobby(lobbyPlayers, clientManagers);
                }
                return;
            }

            //Player was not a ghost one
            for (Player p : lobbyPlayers) {
                if (p.getName().equals(name) || p.getTotemColor().equals(color)){//Name/Totem not free
                    cm.sendEvent(new LoggedEvent(false, name, color, new ArrayList<>()));
                    return;
                }

            }
            //Player joining lobby
            Player player = new Player(name, color);
            lobbyPlayers.add(player);
            clientManagers.put(name, cm);
            cm.setPlayerName(name);

            List<String>  playerNames = new ArrayList<>();
            for(Player p : lobbyPlayers)
                playerNames.add(p.getName());

            cm.sendEvent(new LoggedEvent(true, name, color,playerNames));

            if(lobbyPlayers.size() == numPlayers) {//If after the player has joined the lobby it is full, game can start
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
