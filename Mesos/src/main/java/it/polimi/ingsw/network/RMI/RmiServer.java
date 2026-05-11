package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.ServerClass;
import it.polimi.ingsw.network.serverInterface.AckEvent;
import it.polimi.ingsw.network.serverInterface.LoggedEvent;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ale
 */
public class RmiServer extends ServerClass implements VirtualServer {
    private GameController gameController;
    private LobbyController lobbyController;
    private Map<RmiClient, RmiClientManager> clientManagerMap;
    int connected = 0;

    @Override
    public synchronized void initLobby(int numPlayers) throws RemoteException {
        if (lobbyController != null)
            return;
        lobbyController = new LobbyController(this, numPlayers);
    }

    @Override
    public void fullLobby(List<Player> lobbyPlayers, Map<String, ClientConnection> clientManagers){
        gameController = new GameController(lobbyPlayers, clientManagers);
        gameController.startGame();
    }

    @Override
    public GameController getGameController(){
        return this.gameController;
    }

    @Override
    public LobbyController getLobbyController(){
        return this.lobbyController;
    }

    public void startListening(int port) throws RemoteException {
        final String serverName = "MesosServer";

        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(this, 0);
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind(serverName, stub);
        System.out.println("Server listening on port " + port);
    }

    @Override
    public void connect(RmiClient client) throws RemoteException{
        RmiClientManager cm = new RmiClientManager(client);
        clientManagerMap.put(client, cm);
        connected++;
        cm.sendEvent(new AckEvent(connected == 0));
    }

    @Override
    public void numPlayerChoice(int numPlayers) throws RemoteException{
        if (numPlayers < 2 || numPlayers > 5) return;
        this.initLobby(numPlayers);
    }

    @Override
    public void login(String name, ColorEnum totemColor, RmiClient client) throws RemoteException{
        RmiClientManager cm = clientManagerMap.get(client);

        if (lobbyController == null) {
            cm.sendEvent(new LoggedEvent(false, cm.getPlayerName(),
                    totemColor, new ArrayList<>()));
            return;
        }

        lobbyController.addPlayer(name, totemColor, cm);
    }

    @Override
    public void placeTotem(RmiClient client, char position) throws RemoteException {
        RmiClientManager cm = clientManagerMap.get(client);
        gameController.placeTotem(cm.getPlayerName(), position);
    }

    @Override
    public void chooseCard(RmiClient client, List<Integer> upperCards, List<Integer> lowerCards,
                           List<Integer> upperBuildings, List<Integer> lowerBuildings) throws RemoteException {
        RmiClientManager cm = clientManagerMap.get(client);
        gameController.resolveAction(cm.getPlayerName(), upperCards, lowerCards, upperBuildings, lowerBuildings);
    }
}
