package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.OfferTile;
import it.polimi.ingsw.model.TribeCard;
import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.ViewInterface;

import java.util.List;

public class IsYourTurnEvent implements ServerEvent {
    private final List<TribeCard> upperRow;
    private final List<TribeCard> lowerRow;
    private final OfferTile offerTile;

    public IsYourTurnEvent(List<TribeCard> upperRow,List<TribeCard> lowerRow, OfferTile offerTile) {
        this.upperRow = upperRow;
        this.lowerRow = lowerRow;
        this.offerTile = offerTile;
    }

    @Override
    public void updateView(ViewInterface view){
        view.updateRows(upperRow, lowerRow);
        view.selectCard(offerTile.getCountUpperArrow(), offerTile.getCountLowerArrow());
    }


}
