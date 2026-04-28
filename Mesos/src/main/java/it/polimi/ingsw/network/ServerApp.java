package it.polimi.ingsw.network;

import java.io.IOException;
import java.util.Scanner;

public class ServerApp {
    /**
     * @author Ric
     * @param args
     * this class take the information about numPLayers to create Server, before than players login
     */
    public static void main(String[] args) throws IOException {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Scanner scanner = new Scanner(System.in);
        int numPlayers = 0;

        while (numPlayers < 2 || numPlayers > 5) {
            System.out.print("Number of players (2-5): ");
            numPlayers = scanner.nextInt();
        }

        Server server = new Server(numPlayers);
        server.startListening(port);
    }
}
