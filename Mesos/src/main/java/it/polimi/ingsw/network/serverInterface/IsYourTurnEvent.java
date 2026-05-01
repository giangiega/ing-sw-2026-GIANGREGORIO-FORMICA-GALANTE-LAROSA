package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.BuildingCard;
import it.polimi.ingsw.model.OfferTile;
import it.polimi.ingsw.model.TribeCard;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.util.ArrayList;
import java.util.List;

public class IsYourTurnEvent implements ServerEvent {
    private final OfferTile offerTile;
    private final List<BuildingCard> buildingUpperRow;
    private final List<BuildingCard> buildingLowerRow;

    public IsYourTurnEvent(OfferTile offerTile,  List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow) {
        this.offerTile = offerTile;
        this.buildingUpperRow = buildingUpperRow;
        this.buildingLowerRow = buildingLowerRow;
    }

    @Override
    public void updateView(ViewInterface view){
        view.selectCard(offerTile.getCountUpperArrow(), offerTile.getCountLowerArrow(),
               buildingUpperRow, buildingLowerRow);
    }



}
