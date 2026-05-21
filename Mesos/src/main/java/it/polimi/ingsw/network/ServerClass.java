package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.persistence.SavedGameState;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * this class would be an interface but RmiClient already implements an interface
 */
abstract public class ServerClass {
    protected static LobbyController lobbyController;
    protected static GameController gameController;
    protected static int connected = 0;
    protected static boolean needReset = false;
    protected static SavedGameState pendingSave = null;

    public LobbyController getLobbyController(){
        return lobbyController;
    }
    public GameController getGameController(){
        return gameController;
    }

    public synchronized void initLobby(int numPlayers){
        if (lobbyController != null)
            return;
        lobbyController = new LobbyController(numPlayers);
    }

    public void fullLobby(List<Player> lobbyPlayers, Map<String, ClientConnection> clientManagers){
        if(gameController != null)
            return;
        gameController = new GameController(lobbyPlayers, clientManagers);
        gameController.startGame();
    }

    public void restoreFromSave(List<Player> lobbyPlayers,
                                Map<String, ClientConnection> clientManagers) {
        if (gameController != null || pendingSave == null)
            return;
        gameController = new GameController(lobbyPlayers, clientManagers);
        gameController.restoreGame(pendingSave);
        pendingSave = null;

        for (Player p : lobbyPlayers){
            gameController.handleReconnection(p.getName(), clientManagers.get(p.getName()));
        }
    }

    public static boolean isRecoveryPending() {
        return pendingSave != null;
    }

    // if Server has to resume the previous game, it has to start without disconnected players
    public static int getRequiredPlayersToResume() {
        if (pendingSave == null)
            return 0;
        int required = 0;
        for (Player p : pendingSave.getGame().getPlayers()) {
            if (!pendingSave.getDisconnectedPlayers().contains(p.getName())) {
                required++;
            }
        }
        return required;
    }

    public static boolean isOriginalPlayer(String name) {
        if (pendingSave == null) return false;
        return pendingSave.getGame().getPlayers().stream()
                .anyMatch(p -> p.getName().equals(name));
    }

    public void handleDisconnection(String playerName){};

    /**
     * This method resets the server
     */
    public synchronized void resetServer() {
        lobbyController = null;
        gameController = null;
        connected = 0;
        needReset = false;
    }
}
