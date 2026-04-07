package it.polimi.ingsw;
/**
 * @author Daniele
 */
import java.util.List;

public class EventCavePainting extends EventCard {

    private int minArtist;
    private int gainedPP;
    private int lostPP;

    public EventCavePainting(EraEnum era) {
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
    public int getMinArtist() {
        return minArtist;
    }
}
