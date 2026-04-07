package it.polimi.ingsw;
/**
 * @author Daniele
 */

import java.util.List;

public abstract class EventCard extends TribeCard {

    public EventCard(EraEnum era) {
        super(era);
    }

    public abstract void resolve(List<Player> players, Board board);
}
