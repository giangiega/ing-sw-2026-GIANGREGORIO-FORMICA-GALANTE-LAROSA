package it.polimi.ingsw.network;

import it.polimi.ingsw.network.RMI.RmiServer;

import java.rmi.RemoteException;
import java.util.Scanner;

public class ServerApp {
    /**
     * @author Ric
     * @param args
     * this class take the information about numPLayers to create Server, before than players login
     */
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        Scanner scanner = new Scanner(System.in);

        System.out.print("Choose network protocol: \n[0] Socket\n[1] RMI: \n");
        String protocol = scanner.nextLine().trim().toLowerCase();

        while(!protocol.equals("0") && !protocol.equals("1")){
            System.err.print("Invalid protocol\n");
            System.out.print("Choose protocol: \n[0] Socket\n[1] RMI: \n");
            protocol = scanner.nextLine().trim().toLowerCase();
        }

        if (protocol.equals("0")) {
                new Server().startListening(port);
        } else if (protocol.equals("1")) {
            try {
                new RmiServer().startListening(port);
            }catch (RemoteException e){
                System.err.println("Server error: " + e.getMessage());
            }
        }
    }
}

