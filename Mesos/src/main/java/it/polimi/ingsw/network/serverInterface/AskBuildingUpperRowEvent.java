package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;


import java.rmi.RemoteException;
import java.util.List;

public class AskBuildingUpperRowEvent implements ServerEvent {
    private final List<TribeCard> upperRow;
    private final List<BuildingCard> buildingUpperRow;

    public AskBuildingUpperRowEvent(List<TribeCard> upperRow, List<BuildingCard> buildingUpperRow) {
        this.upperRow = upperRow;
        this.buildingUpperRow = buildingUpperRow;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.askBuildingUpperRowChoice(upperRow, buildingUpperRow);
    }

    @Override
    public void updateViewRmi(VirtualView client) throws RemoteException {
        client.onAskBuildingUpperRow(upperRow, buildingUpperRow);
    }
}
