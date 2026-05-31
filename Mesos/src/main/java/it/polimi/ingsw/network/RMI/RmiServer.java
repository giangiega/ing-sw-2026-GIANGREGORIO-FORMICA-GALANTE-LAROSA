package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.network.ServerClass;
import it.polimi.ingsw.network.serverInterface.AckEvent;
import it.polimi.ingsw.network.serverInterface.LoggedEvent;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Ale
 */
public class RmiServer extends ServerClass implements VirtualServer {
    private final Map<VirtualView, RmiClientManager> clientManagerMap = new ConcurrentHashMap<>();

    public synchronized void initLobbyRmi(int numPlayers) throws RemoteException {
        this.initLobby(numPlayers);
        if(lobbyController != null)
            getLobbyController().setServer(this);
    }

    public void startListening(int port) throws RemoteException {
        final String serverName = "MesosServer";

        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(this, port+1);
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind(serverName, stub);
        System.out.println("Server listening on port " + port);

        if (ServerClass.isRecoveryPending() && lobbyController != null)
            getLobbyController().setServer(this);

    }

    /**
     * @author Giuse
     * @param playerName : name of the player who left the game
     * This method propagates the disconnection to the GameController
     */
    @Override
    public void handleDisconnection(String playerName) {
        if (playerName == null) {
            return;
        }
        if (gameController != null) {
            gameController.handleDisconnection(playerName);
        } else if (lobbyController != null) {
            lobbyController.handleLobbyDisconnection(playerName);
        }
    }

    /**
     *
     * @param droppedCm
     * @param id
     */
    private void handleClientDrop(RmiClientManager droppedCm, int id) {
        String name = droppedCm != null ? droppedCm.getPlayerName() : null;
        boolean wasMaster = false;

        synchronized (ServerClass.clientManagers) {
            if (!ServerClass.clientManagers.isEmpty()) {
                wasMaster = (id == java.util.Collections.min(ServerClass.clientManagers.keySet()));
            }

            ServerClass.clientManagers.remove(id);
        }
        if (droppedCm != null) {
            clientManagerMap.values().remove(droppedCm);
        }

        if (name != null) {
            //if client was logged
            handleDisconnection(name);
        } else {
            //pre-login disconnection
            if (lobbyController == null) {
                synchronized (ServerClass.clientManagers) {
                    if (!ServerClass.clientManagers.isEmpty()) {
                        if(wasMaster) {
                            int nextMasterId = java.util.Collections.min(ServerClass.clientManagers.keySet());
                            ClientConnection nextMaster = ServerClass.clientManagers.get(nextMasterId);

                            //AckEvent for showing askNumPlayers
                            if (nextMaster != null) {
                                nextMaster.sendEvent(new AckEvent(true));
                            }
                        }
                    } else {
                        //no players in lobby, reset server
                        needReset = true;
                        resetServer();
                    }
                }
            }
        }
    }

    /**
     * RmiClient calls this method to "complete" (add him to server's client list)
     * the connection with server
     * @param client
     * @throws RemoteException
     */
    @Override
    public synchronized void connect(VirtualView client) throws RemoteException {
        //If a manager already exists, then reconnection. Stop it and build a new one
        RmiClientManager old = clientManagerMap.get(client);
        if (old != null) {
            old.stopSilently();
        }

        if (needReset) {
            resetServer(); //connected = 0
        }

        AtomicReference<RmiClientManager> cmRef = new AtomicReference<>();

        int id = ServerClass.connected;

        //Using cm to avoid race condition: map has already been updated by reconnection
        RmiClientManager cm = new RmiClientManager(client, () -> {
            RmiClientManager self = cmRef.get();
            handleClientDrop(self, id);
            //handleDisconnection(self != null ? self.getPlayerName() : null);
        });

        synchronized (ServerClass.clientManagers) {
            ServerClass.clientManagers.put(id, cm);
        }

        cmRef.set(cm);
        clientManagerMap.put(client, cm);
        cm.sendEvent(new AckEvent(connected == 0 && !ServerClass.isRecoveryPending()));
        connected++;

    }

    @Override
    public void numPlayerChoice(int numPlayers) throws RemoteException {
        if (numPlayers < 2 || numPlayers > 5) return;
        this.initLobbyRmi(numPlayers);
    }

    @Override
    public void login(String name, ColorEnum totemColor, VirtualView client) throws RemoteException{
        RmiClientManager cm = clientManagerMap.get(client);
        if(cm == null) return;

        if (lobbyController == null) {
            LoggedEvent loginWithoutNumPlayerChosen = new LoggedEvent(false, cm.getPlayerName(),
                    totemColor, new ArrayList<>());
            loginWithoutNumPlayerChosen.setNumPlayersChosen(false);
            cm.sendEvent(loginWithoutNumPlayerChosen);
            return;
        }

        lobbyController.addPlayer(name, totemColor, cm);
    }

    @Override
    public void placeTotem(VirtualView client, char position) throws RemoteException {
        RmiClientManager cm = clientManagerMap.get(client);
        if((cm != null) && (gameController != null)) {
            gameController.placeTotem(cm.getPlayerName(), position);
        }
    }

    @Override
    public void chooseCard(VirtualView client, List<Integer> upperCards, List<Integer> lowerCards,
                           List<Integer> upperBuildings, List<Integer> lowerBuildings) throws RemoteException {
        RmiClientManager cm = clientManagerMap.get(client);
        if((cm != null) && (gameController != null)){
            gameController.resolveAction(cm.getPlayerName(), upperCards, lowerCards, upperBuildings, lowerBuildings);
        }
    }
    /**
     * @author Giuse
     * @param client
     * This method only function is to check if the connection is still alive
     * If the client can reach this method, the RMI connection is alive.
     * If the connection is dead the call will throw a RemoteException on the client side,
     * triggering the reconnect logic.
     */
    @Override
    public void ping(VirtualView client) throws RemoteException {
    }
}
