package it.polimi.ingsw.network;

import it.polimi.ingsw.network.RMI.RmiServer;
import it.polimi.ingsw.persistence.PersistenceManager;
import it.polimi.ingsw.persistence.SavedGameState;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class ServerApp {
    /**
     * @author Ric
     * @param args
     * this class take the information about numPLayers to create Server, before than players login
     * UPDATE : it creates both RMI and Socket Servers so players can choose both at the same time
     */
    public static void main(String[] args) {
        int socketPort = Integer.parseInt(args[0]);
        int rmiPort    = Integer.parseInt(args[1]);

        try { // concezione sbagliata del numero di giocatori
            SavedGameState save = PersistenceManager.load();
            if (save != null) {
                ServerClass.pendingSave = save;
                // Game starts logically with numPlayers, but actually without disconnected ones.
                int numPlayers = save.getGame().getPlayers().size();
                new Server().initLobby(numPlayers);
                System.out.println("[Recovery] Found saved game with " + numPlayers + " players. Waiting for reconnections.");
            }
        } catch (IOException e) {
            System.err.println("[Recovery] Could not load save file: " + e.getMessage());
        }

        new Thread(() -> {
            new Server().startListening(socketPort);
        }, "socket-server").start();

        new Thread(() -> {
            try {
                new RmiServer().startListening(rmiPort);
            } catch (RemoteException e) {
                System.err.println("RMI server error: " + e.getMessage());
            }
        }, "rmi-server").start();

        System.out.println("Socket server on port " + socketPort);
        System.out.println("RMI server on port " + rmiPort);
    }
}

