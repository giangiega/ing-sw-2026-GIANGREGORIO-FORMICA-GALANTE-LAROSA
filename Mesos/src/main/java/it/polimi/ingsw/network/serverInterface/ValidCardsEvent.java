package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class ValidCardsEvent implements ServerEvent {
    Map<CharacterEnum, List<CharacterCard>> tribe;
    List<BuildingCard> buildings;

    public ValidCardsEvent(Map<CharacterEnum, List<CharacterCard>> tribe, List<BuildingCard> buildings) {
        this.tribe = tribe;
        this.buildings= buildings;
    }

    public Map<CharacterEnum, List<CharacterCard>> getTribe() { return tribe; }
    public List<BuildingCard> getBuildings() { return buildings; }

    @Override
    public void updateView(ViewInterface view) {
        view.showValidCards(tribe,buildings);

    }


    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onValidCards(tribe,buildings);
    }
}
