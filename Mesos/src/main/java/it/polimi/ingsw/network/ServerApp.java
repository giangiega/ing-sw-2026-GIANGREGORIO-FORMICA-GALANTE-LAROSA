package it.polimi.ingsw.network;

import java.io.IOException;
import java.util.Scanner;

public class ServerApp {
    /**
     * @author Ric
     * @param args
     * this class take the information about numPLayers to create Server, before than players login
     */
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        new Server().startListening(port);
    }
}

