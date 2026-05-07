package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.network.RMI.VirtualView;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.rmi.RemoteException;
import java.util.List;

public class IsYourTurnEvent implements ServerEvent {
    private final OfferTile offerTile;
    private final List<TribeCard> upperRow;
    private final List<TribeCard> lowerRow;
    private final List<BuildingCard> buildingUpperRow;
    private final List<BuildingCard> buildingLowerRow;

    public IsYourTurnEvent(OfferTile offerTile, List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow) {
        this.offerTile = offerTile;
        this.upperRow = upperRow;
        this.lowerRow = lowerRow;
        this.buildingUpperRow = buildingUpperRow;
        this.buildingLowerRow = buildingLowerRow;
    }

    @Override
    public void updateView(ViewInterface view){
        int cardsUpper = 0;
        int cardsLower = 0;

        for (TribeCard c : upperRow) {
            if(!c.isEventCard())
                cardsUpper++;
        }
        for (TribeCard c : lowerRow) {
            if(!c.isEventCard())
                cardsLower++;
        }
        view.selectCard(offerTile.getCountUpperArrow(), offerTile.getCountLowerArrow(), cardsUpper,
                cardsLower, upperRow, lowerRow, buildingUpperRow, buildingLowerRow);
    }

    @Override
    public void updateViewRmi(VirtualView view) throws RemoteException {

    }
}
