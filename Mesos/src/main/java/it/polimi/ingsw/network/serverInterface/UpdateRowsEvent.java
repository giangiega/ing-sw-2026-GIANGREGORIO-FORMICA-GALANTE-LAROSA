package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.util.List;

public class UpdateRowsEvent implements ServerEvent {
    private final List<TribeCard> upperRow;
    private final List<TribeCard> lowerRow;
    private final List<BuildingCard> buildingUpperRow;
    private final List<BuildingCard> buildingLowerRow;

    public UpdateRowsEvent(List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow) {
        this.upperRow = upperRow;
        this.lowerRow = lowerRow;
        this.buildingUpperRow = buildingUpperRow;
        this.buildingLowerRow = buildingLowerRow;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.updateRows(upperRow, lowerRow, buildingUpperRow, buildingLowerRow);
    }
}
