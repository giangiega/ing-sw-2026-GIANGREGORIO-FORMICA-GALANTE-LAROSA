package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.util.ArrayList;
import java.util.List;

public class GameStartedEvent implements ServerEvent {
    private final List<OfferTile> offerTrack;
    private final TurnOrderTile turnOrderTile;
    private final List<TribeCard> upperRow;
    private final List<TribeCard> lowerRow;
    private final List<BuildingCard> buildingUpperRow;
    private final List<BuildingCard> buildingLowerRow;

    public GameStartedEvent(List<OfferTile> offerTrack, TurnOrderTile tile, List<TribeCard> upperRow,
                            List<TribeCard> lowerRow, List<BuildingCard> buildingUpperRow,  List<BuildingCard> buildingLowerRow) {
        this.offerTrack = offerTrack;
        this.turnOrderTile = tile;
        this.upperRow = upperRow;
        this.lowerRow = lowerRow;
        this.buildingUpperRow = buildingUpperRow;
        this.buildingLowerRow = buildingLowerRow;
    }

    private List<String> getTurnOrderNames(){
        List<String> turnOrderNames = new ArrayList<>();
        for(Player p : turnOrderTile.getOrder()){
            turnOrderNames.add(p.getName());
        }
        return turnOrderNames;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.showGameStart();
        view.updateOfferTrack(offerTrack);
        view.updateTurnOrder(getTurnOrderNames());
        view.updateRows(upperRow, lowerRow, buildingUpperRow, buildingLowerRow);
    }
}
