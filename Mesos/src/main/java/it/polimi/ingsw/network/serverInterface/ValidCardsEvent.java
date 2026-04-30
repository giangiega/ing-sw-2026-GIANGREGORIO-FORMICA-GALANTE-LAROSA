package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.util.List;
import java.util.Map;

public class ValidCardsEvent implements ServerEvent {
    Map<CharacterEnum, List<CharacterCard>> tribe;

    public ValidCardsEvent(Map<CharacterEnum, List<CharacterCard>> tribe) {
        this.tribe = tribe;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.showValidCards(tribe);

    }

}
