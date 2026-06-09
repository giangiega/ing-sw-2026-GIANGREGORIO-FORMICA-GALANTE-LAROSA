package it.polimi.ingsw.controller;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.network.ServerClass;
import it.polimi.ingsw.network.serverInterface.InvalidChoiceEvent;
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
     * @param playerName name of the player who disconnected
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
     * @param name name of the player
     * @param color chosen color for the player's totem
     * @param cm manager for the player
     * Manages the synchronized player's login.
     * Checks that the chosen name and totem color are valid.
     * Sends a LoggedEvent and calls fullLobby at the end, where GameController is created,
     */
    public synchronized void addPlayer(String name, ColorEnum color, ClientConnection cm) {

        if (gameStarted) {
            GameController gameController = server.getGameController();
            if (gameController.getGameOver()) {
                List<String> lobbyNames = new ArrayList<>();
                for (Player p : lobbyPlayers) lobbyNames.add(p.getName());
                cm.sendEvent(new LoggedEvent(false, name, color, lobbyNames));
                return;
            }

            String disconnectedPlayer = null;
            for (String s : gameController.getDisconnectedPlayers()) {
                if (s.equals(name)) {
                    disconnectedPlayer = name;
                    break;
                }
            }
            if (disconnectedPlayer == null) {
                List<String> lobbyNames = new ArrayList<>();
                for (Player p : lobbyPlayers) lobbyNames.add(p.getName());
                cm.sendEvent(new LoggedEvent(false, name, color, lobbyNames));
            } else {
                ColorEnum originalColor = gameController.getPlayerColor(name);
                List<String> lobbyNames = new ArrayList<>();
                for (Player p : lobbyPlayers) lobbyNames.add(p.getName());
                cm.sendEvent(new LoggedEvent(true, name, originalColor, lobbyNames));
                cm.setPlayerName(name);
                gameController.handleReconnection(name, cm);
            }
            return;
        }

        // Game not started yet
        if (lobbyPlayers.size() >= numPlayers) {
            List<String> lobbyNames = new ArrayList<>();
            for (Player p : lobbyPlayers) lobbyNames.add(p.getName());
            cm.sendEvent(new LoggedEvent(false, name, color, lobbyNames));
            return;
        }

        // if namePlayer was not in the previous game, a new game starts
        if (ServerClass.isRecoveryPending() && !ServerClass.isOriginalPlayer(name)) {
            clientManagers.put(name, cm); // in this way players can have the abort message

            for (ClientConnection cms : clientManagers.values()) {
                cms.sendEvent(new InvalidChoiceEvent(
                        "Recovery aborted: '" + name + "' was not in the previous game. " +
                                "Disconnect and reconnect to start a new game."));
            }

            lobbyPlayers.clear();
            clientManagers.clear();

            ServerClass.setPendingSave();
            server.resetServer(); // connected=0, lobbyController=null, gameController=null
        }

        if (ghostPlayers.containsKey(name)) {
            ColorEnum originalColor = ghostPlayers.get(name);
            boolean colorTaken = false;
            for (Player p : lobbyPlayers) {
                if (p.getTotemColor().equals(originalColor)) {
                    colorTaken = true;
                }
            }
            if (colorTaken) {
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

            triggerStartIfReady();
            return;
        }

        // Normal new player
        for (Player p : lobbyPlayers) {
            if (p.getName().equals(name) || p.getTotemColor().equals(color)) {
                cm.sendEvent(new LoggedEvent(false, name, color, new ArrayList<>()));
                return;
            }
        }

        Player player = new Player(name, color);
        lobbyPlayers.add(player);
        clientManagers.put(name, cm);
        cm.setPlayerName(name);

        List<String> playerNames = new ArrayList<>();
        for (Player p : lobbyPlayers) playerNames.add(p.getName());
        cm.sendEvent(new LoggedEvent(true, name, color, playerNames));

        triggerStartIfReady();
    }

    // Helper to avoid duplicating the recovery/normal start check
    private void triggerStartIfReady() {
        if (ServerClass.isRecoveryPending()) {
            if (lobbyPlayers.size() == ServerClass.getRequiredPlayersToResume()) {
                gameStarted = true;
                server.restoreFromSave(lobbyPlayers, clientManagers);
            }
        } else {
            if (lobbyPlayers.size() == numPlayers) {
                gameStarted = true;
                server.fullLobby(lobbyPlayers, clientManagers);
            }
        }
    }

    /**
     * set the right server (socket or RMI) based network protocol type
     */
    public void setServer(ServerClass server) {
        if (this.server != null)
            return;
        this.server = server;
    }

}
