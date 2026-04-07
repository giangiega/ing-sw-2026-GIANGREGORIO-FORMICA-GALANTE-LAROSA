package it.polimi.ingsw;
/**
 * @author Daniele
 */
import java.util.List;

public class EventSustenance extends EventCard {
    private int ppPerUnfedCharacter;

    public EventSustenance(EraEnum era) {
        super(era);
    }


    @Override
    public void resolve(List<Player> players, Board board) {

    }
    public int getPpPerUnfedCharacter() {
        return ppPerUnfedCharacter;
    }
}
