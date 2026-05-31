package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.network.serverInterface.ServerEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * author Ale
 * this class is only for testes: our controllers use method of ClientConnection interface and
 * thi methods are overrided in our RMI and Socket network, so we use this class to avoid the
 * network level that doesn't need testing: with this class records in a list ServerEvents received
 * by a client
 *
 */
public class FakeClientConnection implements ClientConnection {

    private String playerName;
    private final Map<String, Integer> eventCounts = new HashMap<>();

    FakeClientConnection(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void sendEvent(ServerEvent event) {
        String key = event.getClass().getSimpleName();
        eventCounts.put(key, eventCounts.getOrDefault(key, 0) + 1);
    }

    @Override
    public void setPlayerName(String name) {
        this.playerName = name;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    public int countOf(String eventName) {
        return eventCounts.getOrDefault(eventName, 0);
    }

    public boolean received(String eventName) {
        return countOf(eventName) > 0;
    }

    public void clearEvents() {
        eventCounts.clear();
    }

    public int eventCount(){
        int count = 0;
        for(String event: eventCounts.keySet()){
            count += eventCounts.get(event);
        }

        return count;
    }
}
