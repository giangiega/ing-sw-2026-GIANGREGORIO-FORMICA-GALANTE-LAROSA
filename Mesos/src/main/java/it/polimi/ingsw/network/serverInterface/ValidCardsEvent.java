package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;
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

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onValidCards(tribe);
    }
}
