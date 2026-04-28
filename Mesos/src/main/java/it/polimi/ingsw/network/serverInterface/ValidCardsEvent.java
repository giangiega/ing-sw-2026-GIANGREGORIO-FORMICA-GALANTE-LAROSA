package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.BuildingCard;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.ViewInterface;

import java.util.List;

public class ValidCardsEvent implements ServerEvent {
    List<CharacterCard> tribe;

    public ValidCardsEvent(List<CharacterCard> tribe) {
        this.tribe = tribe;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.showValidCards(tribe);

    }

}
