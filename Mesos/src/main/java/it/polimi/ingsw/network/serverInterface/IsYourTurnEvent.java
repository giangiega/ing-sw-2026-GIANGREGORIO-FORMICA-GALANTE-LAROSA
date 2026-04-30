package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.OfferTile;
import it.polimi.ingsw.model.TribeCard;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.util.List;

public class IsYourTurnEvent implements ServerEvent {
    private final OfferTile offerTile;

    public IsYourTurnEvent(OfferTile offerTile) {
        this.offerTile = offerTile;
    }

    @Override
    public void updateView(ViewInterface view){
        view.selectCard(offerTile.getCountUpperArrow(), offerTile.getCountLowerArrow());
    }



}
