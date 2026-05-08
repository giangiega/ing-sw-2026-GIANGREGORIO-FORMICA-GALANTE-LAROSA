package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.ServerClass;
import it.polimi.ingsw.network.serverInterface.AckEvent;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

/**
 * @author Ale
 */
public class RmiServer extends ServerClass implements VirtualServer {
    private GameController gameController;
    private LobbyController lobbyController;
    int connected = 0;

    public synchronized void initLobby(int numPlayers) throws RemoteException {
        if (lobbyController != null)
            return;
        lobbyController = new LobbyController(this, numPlayers);
    }

    @Override
    public void fullLobby(List<Player> lobbyPlayers, Map<String, ClientConnection> clientManagers){

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
        cm.sendEvent(new AckEvent(connected == 0));
    }
}
