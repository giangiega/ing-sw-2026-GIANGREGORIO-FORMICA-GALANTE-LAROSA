package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.OfferTile;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TurnOrderTile;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.util.ArrayList;
import java.util.List;

public class UpdateBoardEvent implements ServerEvent {
    private final List<OfferTile> offerTrack;
    private final TurnOrderTile turnOrderTile;

    public UpdateBoardEvent(List<OfferTile> offerTrack, TurnOrderTile turnOrderTile) {
        this.offerTrack = offerTrack;
        this.turnOrderTile = turnOrderTile;
    }

    private List<String> getTurnOrderNames(){
        List<String> turnOrderNames = new ArrayList<>();
        for(Player p : turnOrderTile.getOrder()){
            turnOrderNames.add(p.getName());
        }
        return turnOrderNames;
    }
    @Override
    public void updateView(ViewInterface view){
        view.updateOfferTrack(offerTrack);
        view.updateTurnOrder(getTurnOrderNames());
    }
}
