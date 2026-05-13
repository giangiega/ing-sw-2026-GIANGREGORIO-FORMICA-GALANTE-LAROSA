package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.Player;
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

    public UpdateAllTribesEvent(List<Player> players) {
        this.names = new ArrayList<>();
        this.tribes = new ArrayList<>();
        for (Player p : players) {
            names.add(p.getName());
            tribes.add(p.getTribe());
        }
    }

    public List<String> getNames() { return names; }
    public List<Map<CharacterEnum, List<CharacterCard>>> getTribes() { return tribes; }

    @Override
    public void updateView(ViewInterface view) {
        view.updateAllTribes(names, tribes);

    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onUpdateAllTribes(names, tribes);
    }
}
