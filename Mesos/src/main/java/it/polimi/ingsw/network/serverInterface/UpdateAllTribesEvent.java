package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateAllTribesEvent implements ServerEvent {
    private final List<String> names;
    private final List<Map<CharacterEnum, List<CharacterCard>>> tribes;
    private final List<List<BuildingCard>> buildings;

    public UpdateAllTribesEvent(List<Player> players) {
        this.names = new ArrayList<>();
        this.tribes = new ArrayList<>();
        this.buildings = new ArrayList<>();
        for (Player p : players) {
            names.add(p.getName());
            tribes.add(p.getTribe());
            buildings.add(new ArrayList<>(p.getBuildingCards()));
        }
    }

    public List<String> getNames() { return names; }
    public List<Map<CharacterEnum, List<CharacterCard>>> getTribes() { return tribes; }
    public List<List<BuildingCard>> getBuildings() { return buildings; }

    @Override
    public void updateView(ViewInterface view) {
        view.updateAllTribes(names, tribes,buildings);

    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onUpdateAllTribes(names, tribes,buildings);
    }
}
