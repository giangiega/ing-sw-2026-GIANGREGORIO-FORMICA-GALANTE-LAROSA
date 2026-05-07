package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.network.Server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Ale
 */
public class RmiServer implements VirtualServer {
    private final GameController gameController;
    private final LobbyController lobbyController;

    public RmiServer(GameController gameController, LobbyController lobbyController){
        this.gameController = gameController;
        this.lobbyController = lobbyController;
    }

    public static void main() throws RemoteException {
        //problema: per creare il lobbyController serve il Server, ma non ha senso usarlo
        // in RMI, possibile soluzione con interfaccia RmiServer già ne implementa una e
        // quella già ne estende un'altra, valutare se fare una super classe ServerClass
        // per RmiServer e Server
    }
}
