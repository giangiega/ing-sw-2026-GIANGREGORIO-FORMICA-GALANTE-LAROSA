package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.OfferTile;
import it.polimi.ingsw.model.TurnOrderTile;
import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

import java.util.List;

public class UpdateBoardEvent implements ServerEvent {
    private final List<OfferTile> offerTrack;
    private final TurnOrderTile turnOrderTile;

    public UpdateBoardEvent(List<OfferTile> offerTrack, TurnOrderTile turnOrderTile) {
        this.offerTrack = offerTrack;
        this.turnOrderTile = turnOrderTile;
    }

    @Override
    public void executeOp(Server server, ClientManagerSocket cm) {

    }
}
