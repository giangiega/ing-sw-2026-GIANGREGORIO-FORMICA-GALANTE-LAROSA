package it.polimi.ingsw.model;
/**
 * @author Daniele
 */

import it.polimi.ingsw.enums.EraEnum;

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

    @Override
    public boolean isEventCard() {
        return true;
    }

}
