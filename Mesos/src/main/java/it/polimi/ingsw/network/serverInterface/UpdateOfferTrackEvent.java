package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.util.List;

public class UpdateOfferTrackEvent implements ServerEvent {
    private final List<OfferTile> offerTrack;
    private final TurnOrderTile turnOrderTile;

    public UpdateOfferTrackEvent(List<OfferTile> offerTrack, TurnOrderTile turnOrderTile) {
        this.offerTrack = offerTrack;
        this.turnOrderTile = turnOrderTile;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.updateOfferTrack(offerTrack);
        view.updateTurnOrder(turnOrderTile);
    }

}
