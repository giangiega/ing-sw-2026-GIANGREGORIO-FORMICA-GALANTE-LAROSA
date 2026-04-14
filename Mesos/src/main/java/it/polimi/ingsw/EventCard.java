package it.polimi.ingsw;
/**
 * @author Daniele
 */

import java.util.List;

public abstract class EventCard extends TribeCard {
    private final boolean isFinalEvent;
    public EventCard(EraEnum era, boolean isFinalEvent) {
        super(era);
        this.isFinalEvent = isFinalEvent;
    }

    public abstract void resolve(List<Player> players, Board board);

    public boolean isFinalEvent(){
        return isFinalEvent;
    }

    @Override
    public boolean isPickable(){
        return false;
    }
}
