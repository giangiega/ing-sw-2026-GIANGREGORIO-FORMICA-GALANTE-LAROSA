package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;
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

    @Override
    public void updateView(ViewInterface view) {
        view.showGameStart();
        view.updateOfferTrack(offerTrack);
        view.updateTurnOrder(turnOrderTile);
        view.updateRows(upperRow, lowerRow, buildingUpperRow, buildingLowerRow);
    }

    @Override
    public void updateViewRmi(VirtualView view) throws RemoteException {

    }
}
