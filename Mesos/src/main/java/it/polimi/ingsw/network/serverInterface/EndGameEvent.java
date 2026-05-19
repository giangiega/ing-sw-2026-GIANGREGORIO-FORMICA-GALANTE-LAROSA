package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.database.RankingRow;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndGameEvent implements ServerEvent {
    List<String> winnerNames;
    Map<String, Integer> finalScores;
    List<RankingRow> ranking;
    Map<String, Integer> playersPosition;

    public EndGameEvent(List<Player> winners,  Map<Player, Integer> scores, List<RankingRow> ranking, Map<String, Integer> playersPosition) {
        this.winnerNames = new ArrayList<>();
        for (Player player : winners)
            this.winnerNames.add(player.getName());

        this.finalScores = new HashMap<>();
        for (Map.Entry<Player, Integer> entry : scores.entrySet()) {
            this.finalScores.put(entry.getKey().getName(), entry.getValue());
        }

        this.ranking = ranking;
        this.playersPosition = playersPosition;
    }

    @Override
    public void updateView(ViewInterface view){
        view.showFinalScore(winnerNames, finalScores);
        view.showLeaderboard(ranking, playersPosition);
    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onEndGame(winnerNames, finalScores, ranking, playersPosition);
    }
}
