package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.ViewInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndGameEvent implements ServerEvent {
    List<String> winnerNames;
    Map<String, Integer> finalScores;
    public EndGameEvent(List<Player> winners,  Map<Player, Integer> scores) {
        this.winnerNames = new ArrayList<>();
        for (Player player : winners)
            this.winnerNames.add(player.getName());

        this.finalScores = new HashMap<>();
        for (Map.Entry<Player, Integer> entry : scores.entrySet()) {
            this.finalScores.put(entry.getKey().getName(), entry.getValue());
        }
    }

    @Override
    public void updateView(ViewInterface view){

    }
}
