package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.util.List;
import java.util.Map;

public class UpdatePlayerEvent implements ServerEvent {
    private final String name;
    private final int food;
    private final int prestigePoints;
    private final Map<CharacterEnum, List<CharacterCard>> tribe;

    public UpdatePlayerEvent(Player player) {
        this.name = player.getName();
        this.food = player.getFood();
        this.prestigePoints = player.getPP();
        this.tribe = player.getTribe();
    }

    @Override
    public void updateView(ViewInterface view) {
        view.updatePlayer(name, food, prestigePoints, tribe);
    }
}
