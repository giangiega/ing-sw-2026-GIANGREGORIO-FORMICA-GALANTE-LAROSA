/**
 * @author Giuse
 */
package it.polimi.ingsw.network.socket;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;


import it.polimi.ingsw.network.serverInterface.ServerEvent;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Consumer;

public class ListenerClientViewSocket implements Runnable {

    private final BufferedReader in;
    private final ViewInterface view;
    private final Gson gson;
    private final Consumer<Runnable> uiDispatcher;
    /**
     * @param in : reading channel
     * @param view : view
     * @param uiDispatcher : dispatch for UI: Runnable for TUI
     */
    public ListenerClientViewSocket(BufferedReader in, ViewInterface view, Consumer<Runnable> uiDispatcher){
        this.in = in;
        this.view = view;
        this.uiDispatcher = uiDispatcher;
        this.gson = GsonFactory.serverEventGson();
    }
    @Override
    public void run() {
        try {
            String json;
            while((json = in.readLine()) != null){
                handleMessage(json);
            }
            handleServerClosedConnection();//connection closed without problems
        } catch (IOException e){
            handleNetworkError(e);//network was shout down
        }
    }

    /**
     * @param json : JSON string received form server
     * This method deserializes il JSON in a ServerEvent, then gives the execution to
     * the UI thread thanks to the dispatcher.
     * The try-catch on JsonSyntaxExecption prevents the killing of the  listener
     */
    private void handleMessage(String json){
        try{
            ServerEvent event = gson.fromJson(json, ServerEvent.class);

            uiDispatcher.accept(()-> event.updateView(view));
        }catch (JsonSyntaxException | JsonIOException e){
            System.err.println("From ListenerClientViewSocket: invalid JSON, got ignored "+json);
        }
    }

    /**
     * It shows that the connection was closed without any errors
     */
    private void handleServerClosedConnection(){
        System.out.println("From ListenerClientViewSocket: connection closed by the server");
    }

    /**
     * It shows that the connection was lost
     */
    private void handleNetworkError(Exception e){
        System.err.println("From ListenerClientViewSocket: lost connection" +e.getMessage());
    }
}
