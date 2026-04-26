package it.polimi.ingsw.network;

import java.util.Scanner;

public class ServerApp {
    /**
     * @author Ric
     * @param args
     * this class take the information about numPLayers to create Server, before than players login
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numPlayers = 0;

        while (numPlayers < 2 || numPlayers > 5) {
            System.out.print("Number of players (2-5): ");
            numPlayers = scanner.nextInt();
        }

        Server server = new Server(numPlayers);
        server.startListening();
    }
}
