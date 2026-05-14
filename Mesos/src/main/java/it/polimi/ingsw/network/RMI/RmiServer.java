package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.enums.ColorEnum;
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

/**
 * @author Ale
 */
public class RmiServer extends ServerClass implements VirtualServer {
    private final Map<VirtualView, RmiClientManager> clientManagerMap = new ConcurrentHashMap<>();

    public synchronized void initLobbyRmi(int numPlayers) throws RemoteException {
        this.initLobby(numPlayers);
        getLobbyController().setServer(this);
    }

    public void startListening(int port) throws RemoteException {
        final String serverName = "MesosServer";

        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(this, 0);
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind(serverName, stub);
        System.out.println("Server listening on port " + port);
    }

    /**
     * @author Giuse
     * @param playerName : name of the player who left the game
     * This method propagates the disconnection to the GameController
     */
    @Override
    public void handleDisconnection(String playerName) {
        if (gameController != null && playerName != null)
            gameController.handleDisconnection(playerName);
    }

    /**
     * RmiClient calls this method to "complete" (add him to server's client list)
     * the connection with server
     * @param client
     * @throws RemoteException
     */
    @Override
    public synchronized void connect(VirtualView client) throws RemoteException {
        RmiClientManager cm = new RmiClientManager(client, () -> {
            RmiClientManager manager = clientManagerMap.get(client);
            if (gameController != null && manager != null)
                gameController.handleDisconnection(manager.getPlayerName());
        });
        clientManagerMap.put(client, cm);
        cm.sendEvent(new AckEvent(connected == 0));
        connected++;
    }

    @Override
    public void numPlayerChoice(int numPlayers) throws RemoteException{
        if (numPlayers < 2 || numPlayers > 5) return;
        this.initLobbyRmi(numPlayers);
    }

    @Override
    public void login(String name, ColorEnum totemColor, VirtualView client) throws RemoteException{
        RmiClientManager cm = clientManagerMap.get(client);
        if(cm == null) return;

        if (lobbyController == null) {
            cm.sendEvent(new LoggedEvent(false, cm.getPlayerName(),
                    totemColor, new ArrayList<>()));
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
