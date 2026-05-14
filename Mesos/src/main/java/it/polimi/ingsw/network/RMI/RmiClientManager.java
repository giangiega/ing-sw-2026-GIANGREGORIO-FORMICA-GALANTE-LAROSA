package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.network.serverInterface.ServerEvent;

import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Ale
 */
public class RmiClientManager implements ClientConnection {
    private final VirtualView client;
    private String playerName;

    private final BlockingQueue<ServerEvent> eventQueue;
    private final Runnable onDisconnect;

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

        new Thread(() -> {
            processQueue();
        }).start();
    }

    /**
     * puts the ServerEvent in queue, the separate thread (processQueue) will resolve it,
     * this is to prevent the block of server (when is using RMI) after the GameController
     * calls broadcastEvent
     * @param serverEvent
     */
    @Override
    public void sendEvent(ServerEvent serverEvent){
        try{
            eventQueue.put(serverEvent);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            System.err.println("Error entering queue for " + playerName);
        }
    }

    /**
     * takes the first ServerEvent from BlockingQueue and calls updateViewRmi on it
     */
    private void processQueue() {
        while (true) {
            try {
                ServerEvent event = eventQueue.take();
                event.updateViewRmi(client);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (RemoteException e) {
                System.err.println("RMI communication error with " + playerName);
                if(playerName != null){
                    onDisconnect.run();
                }
                break;
            }
        }
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
