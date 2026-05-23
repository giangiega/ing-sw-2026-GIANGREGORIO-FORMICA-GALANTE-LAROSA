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

    private volatile boolean isReconnecting = false;

    private static final int INITIAL_RETRY_DELAY_MS = 2_000;
    private static final int MAX_RETRY_DELAY_MS = 30_000;
    private ViewInterface view;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * @param view : new view
     * @throws IOException
     * This method creates the new desired view
     */
    public void connect(ViewInterface view) throws IOException {
        this.view = view;
        doConnect();
    }

    /**
     * @throws IOException
     * This method open the connection, starts the listener and launch the view
     * It throws the exception if the connection to the server fails.
     * It can be called multiple times
     */
    public void doConnect() throws IOException {
        Socket socket = new Socket(host, port);//It waits until the server accepts the connection

        //New input channel
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //New output channel to the server: auto-flush true empties the buffer as soon as it gets filled
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

        ClientViewSocket sender = new ClientViewSocket(out);
        view.init(sender);


        ListenerClientViewSocket listener = new ListenerClientViewSocket(in, view, view.getUIDispatcher(), this::scheduleReconnect);
        Thread listenerThread = new Thread(listener, "listener-client");
        listenerThread.setDaemon(false);//thread doesn't die with main thread
        listenerThread.start();
    }
    /**
     * This method launches a background thread that retries doConnect() with
     * exponential backoff (2 s → 4 s → 8 s … capped at 30 s).
     * Once the connection is re-established, it prompts the player to log in again;
     * the server's LobbyController will recognise the nickname as a reconnection and resume the game.
     */
    private void scheduleReconnect() {
        if (isReconnecting) return; // manages run condition
        isReconnecting = true;

        new Thread(() -> {
            int delayMs = INITIAL_RETRY_DELAY_MS;
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    System.out.println("[SocketClient] reconnecting in " + delayMs / 1000 + "s … ");
                    Thread.sleep(delayMs);

                    try {
                        view.resetInputState();
                        doConnect();
                        // Connection re-established: ask the player to re-enter credentials
                        // view.getUIDispatcher().accept(view::askLogin); ERROR, it calls AckEvent two times for every client
                        return;
                    } catch (IOException e) {
                        System.err.println("[SocketClient] reconnect failed: " + e.getMessage());
                        delayMs = Math.min(delayMs * 2, MAX_RETRY_DELAY_MS);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isReconnecting = false;
            }
        }, "socket-reconnect").start();
    }
}
