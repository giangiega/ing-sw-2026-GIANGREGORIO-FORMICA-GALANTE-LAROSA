/**
 * @author Giuse
 */
package it.polimi.ingsw.network.clientInterface;
import it.polimi.ingsw.network.ClientViewSocket;
import it.polimi.ingsw.network.ListenerClientViewSocket;
import it.polimi.ingsw.network.ViewInterface;

import  java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class SocketClient {

    private final String host;
    private final int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * @return
     * @throws IOException
     * This method open the connection, starts the listener and launch the view
     * It throws the exception if the connection to the server fails
     */
    public Socket connect() throws IOException {
        Socket socket = new Socket(host, port);//It waits until the server accepts the connection

        //New input channel
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //New output channel to the server: auto-flush true empties the buffer as soon as it gets filled
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        //View
        ViewInterface view = new TuiView();

        //Listener: Runnable::run for TUI
        ListenerClientViewSocket listener = new ListenerClientViewSocket(in, view, Runnanble :: run);
        Thread listenerThread = new Thread(listener, "listener-client");
        listenerThread.setDaemon(true);//thread dies with main thread
        listenerThread.start();

        ClientViewSocket sender = new ClientViewSocket(out);
        //Passare il sender alla view ed avviarla
    }
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: SocketClient <host> <port>");
            System.exit(1);
        }

        String host = args[0];
        int port;
        try{
            port = Integer.parseInt(args[1]);
        }catch(NumberFormatException e){
            System.err.println("Invalid port number: " + args[1]);
            System.exit(1);
            return;
        }

        try {
            new SocketClient(host, port).connect();
        }catch(IOException e){
            System.err.println("Could not connect to: " + host + ":" + port);
            System.err.println("Cause: " + e.getMessage());
            System.exit(1);
        }
    }
}
