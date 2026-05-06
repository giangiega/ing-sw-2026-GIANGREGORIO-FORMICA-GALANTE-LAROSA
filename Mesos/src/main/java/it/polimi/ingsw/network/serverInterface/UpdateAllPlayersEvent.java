package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.util.List;

public class UpdateAllPlayersEvent implements ServerEvent {
    private final List<String> names;
    private final List<Integer> foods;
    private final List<Integer> pps;

    public UpdateAllPlayersEvent(List<Player> players) {
        this.names = players.stream().map(Player::getName).toList();
        this.foods = players.stream().map(Player::getFood).toList();
        this.pps = players.stream().map(Player::getPP).toList();
    }

    @Override
    public void updateView(ViewInterface view) {
        view.updateAllPlayers(names, foods, pps);
    }
}
