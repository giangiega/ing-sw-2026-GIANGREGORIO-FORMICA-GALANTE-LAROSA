package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.model.OfferTile;
import it.polimi.ingsw.model.TurnOrderTile;
import it.polimi.ingsw.userInterface.ViewInterface;


import java.util.ArrayList;
import java.util.List;

public class MoveTotemEvent implements ServerEvent {
    private final List<OfferTile> offerTrack;
    private final TurnOrderTile turnOrderTile;

    public MoveTotemEvent(List<OfferTile> offerTrack, TurnOrderTile turnOrderTile) {
        this.offerTrack = offerTrack;
        this.turnOrderTile = turnOrderTile;
    }

    private List<Character> getFreeSlots(){
        List<Character> freeSlots = new ArrayList<>();
            for (OfferTile tile : offerTrack){
                if(tile.getFreeOfferTile()){
                    freeSlots.add(tile.getLetter());
                }
            }
            return freeSlots;
    }
    @Override
    public void updateView(ViewInterface view){
       view.updateOfferTrack(offerTrack);
       view.placeTotem(getFreeSlots());

    }
}
