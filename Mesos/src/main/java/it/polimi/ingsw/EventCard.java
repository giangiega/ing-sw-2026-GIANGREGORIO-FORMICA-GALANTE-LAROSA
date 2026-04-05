package it.polimi.ingsw;

import java.util.List;

public abstract class EventCard {
    public abstract void resolve(List<Player> players, Board board);
}
