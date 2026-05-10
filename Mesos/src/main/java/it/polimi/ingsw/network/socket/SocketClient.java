/**
 * @author Giuse
 */
package it.polimi.ingsw.network.socket;

import it.polimi.ingsw.userInterface.TUIView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SocketClient {

    private final String host;
    private final int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * @throws IOException
     * This method open the connection, starts the listener and launch the view
     * It throws the exception if the connection to the server fails
     */
    public void connect(ViewInterface view) throws IOException {
        Socket socket = new Socket(host, port);//It waits until the server accepts the connection

        //New input channel
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //New output channel to the server: auto-flush true empties the buffer as soon as it gets filled
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        ClientViewSocket sender = new ClientViewSocket(out);
        view.init(sender);

        //Listener: Runnable::run for TUI , uses a view method for the dispatcher
       ListenerClientViewSocket listener = new ListenerClientViewSocket(in, view, view.getUIDispatcher());
        Thread listenerThread = new Thread(listener, "listener-client");
        listenerThread.setDaemon(false);//thread doesn't die with main thread
        listenerThread.start();
    }
}
