package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.network.serverInterface.ServerEvent;

import java.rmi.RemoteException;
import java.util.concurrent.*;

/**
 * @author Ale
 */
public class RmiClientManager implements ClientConnection {
    private final VirtualView client;
    private String playerName;

    private final BlockingQueue<ServerEvent> eventQueue;
    private final Runnable onDisconnect;
    private final ScheduledExecutorService heartbeat;
    private final Thread queueThread;

    private volatile boolean stopped = false;

    private static final int HEARTBEAT_INTERVAL_S = 1;

    /**
     * constructor, starts a separate thread for handling the correct parallel execution of
     * updateViewRmi between clients: as soon as there is an event to be sent for that client,
     * it is queued and sent by the thread dedicated to that client.
     * @param client
     * @param onDisconnect : invoked when a RemoteException signals that the client can no longer
     *                       be reached
     */
    public RmiClientManager(VirtualView client, Runnable onDisconnect) {
        this.client = client;
        this.onDisconnect = onDisconnect;
        this.eventQueue = new LinkedBlockingQueue<>();

        // Thread that sends event
        this.queueThread = new Thread(this::processQueue, "rmi-queue-" + client.hashCode());
        this.queueThread.setDaemon(true); // non blocca lo shutdown della JVM
        this.queueThread.start();

        // Heartbeat server→client: rileva crash anche a queue vuota
        this.heartbeat = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "rmi-heartbeat-" + client.hashCode());
            t.setDaemon(true);
            return t;
        });
        this.heartbeat.scheduleAtFixedRate(
                this::pingClient,
                HEARTBEAT_INTERVAL_S, HEARTBEAT_INTERVAL_S, TimeUnit.SECONDS
        );
    }

    /**
     * This method sends a ping to the client: if it doesn't respond, it means that the connection is lost
     */
    private void pingClient() {
        try {
            client.onPing(); // metodo leggero aggiunto a VirtualView
        } catch (RemoteException e) {
            triggerDisconnect();
        }
    }
    /**
     * puts the ServerEvent in queue, the separate thread (processQueue) will resolve it,
     * this is to prevent the block of server (when is using RMI) after the GameController
     * calls broadcastEvent
     * @param serverEvent
     */
    @Override
    public void sendEvent(ServerEvent serverEvent){
        if (stopped) return;
        eventQueue.offer(serverEvent);
    }

    /**
     * takes the first ServerEvent from BlockingQueue and calls updateViewRmi on it
     */
    private void processQueue() {
        while (!stopped) {
            try {
                ServerEvent event = eventQueue.take();
                if (stopped) break;
                event.updateViewRmi(client);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (RemoteException e) {
                System.err.println("[RmiClientManager] send failed for " + playerName);
                triggerDisconnect();
                break;
            }
        }
    }

    /**
     * This method stops the heartbeat and queue thread; then it notifies the server
     */
    private synchronized void triggerDisconnect() {
        if (stopped) return; // già gestito, evita doppia chiamata
        stopped = true;
        heartbeat.shutdownNow();
        queueThread.interrupt();
        if (playerName != null) {
            onDisconnect.run();
        }
    }
    /**
     * This method is called by RmiServer when a client reconnects.
     * It stops this manager without calling onDisconnect because a reconnection is not a disconnection
     */
    public void stopSilently() {
        stopped = true;
        heartbeat.shutdownNow();
        queueThread.interrupt();
    }

    @Override
    public void setPlayerName(String name){
        this.playerName = name;
    }

    @Override
    public String getPlayerName(){
        return this.playerName;
    }
}
