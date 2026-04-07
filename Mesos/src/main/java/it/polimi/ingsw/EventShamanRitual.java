package it.polimi.ingsw;
/**
 * @author Daniele
 */
import java.util.List;

public class EventShamanRitual extends EventCard {
    private int gainedPP;
    private int lostPP;

    public EventShamanRitual(EraEnum era) {
        super(era);
    }

    @Override
    public void resolve(List<Player> players, Board board) {

    }
    public int getGainedPP() {
        return gainedPP;
    }
    public int getLostPP() {
        return lostPP;
    }
}
