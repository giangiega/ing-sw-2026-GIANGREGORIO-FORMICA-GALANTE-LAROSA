package it.polimi.ingsw;
/**
 * @author Daniele
 */
import java.util.List;

public class EventHunt extends EventCard {
    private int ppPerHunter;

    public EventHunt(EraEnum era) {
        super(era);
    }

    @Override
    public void resolve(List<Player> players, Board board) {

    }
    public int getPpPerHunter() {
        return ppPerHunter;
    }
}
