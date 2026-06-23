package it.polimi.ingsw.network;
import it.polimi.ingsw.network.RMI.RmiClient;
import it.polimi.ingsw.network.socket.SocketClient;
import it.polimi.ingsw.userInterface.*;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Client application must run this main. It setups the program before than the game.
 */
public class ClientApp {
    private static final String RESET = "\033[0m";
    private static final String GREEN = "\033[0;32m";
    private static final String RED = "\033[0;31m";
    private static final String BOLD = "\033[1m";

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        Scanner scanner = new Scanner(System.in);
        int protocol;
        int ui;

        String host = args[0];
        int socketPort = Integer.parseInt(args[1]);
        int rmiPort = Integer.parseInt(args[2]);
        String clientIp = args[3];
        int callBackPort = rmiPort + 2;
        System.setProperty("java.rmi.server.hostname", clientIp);


        System.out.println(GREEN + BOLD + "Choose a network protocol:" + RESET);
        System.out.println("  [1] Socket");
        System.out.print("  [2] RMI\n--> ");
        while (true) {
            if (scanner.hasNextInt()) {
                protocol = scanner.nextInt();
                if (protocol == 1 || protocol == 2)
                    break;
            } else scanner.next();
        }
        System.out.println(GREEN + BOLD + "Choose an interface:" + RESET);
        System.out.println("  [1] TUI");
        System.out.print("  [2] GUI\n--> ");
        while (true) {
            if (scanner.hasNextInt()) {
                ui = scanner.nextInt();
                if (ui == 1 || ui == 2)
                    break;
            } else scanner.next();
        }

        ViewInterface view;
        if (ui == 1) {
            view = new TUIView();
        } else {
            view = new GUIView();
        }

        if (protocol == 1) {
            try {
                new SocketClient(host, socketPort).connect(view);
            } catch (IOException e) {
                System.err.println(RED + BOLD + "Connection failed: " + e.getMessage() + RESET);
            }
        } else try {
            new RmiClient(host, rmiPort, callBackPort).connect(view);
        } catch (IOException e) {
            System.err.println(RED + BOLD + "Connection failed: " + e.getMessage() + RESET);
        }
    }

}
