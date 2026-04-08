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
        this.minArtist = minArtist;
        this.gainedPP = gainedPP;
        this.lostPP = lostPP;
    }

    /**
     * this method will scroll through the list of players , check if they have the specific building for this event
     * and then adds or subtracts the PP based on the artist's number of the player
     * @param players
     * @param board
     */

    @Override
    public void resolve(List<Player> players, Board board) {
        players.forEach(p -> {
            p.getBuildingCards().forEach(b -> b.getEffect().applyEventCavePainting(p, board));

            int numArtist  = p.getCharacterByType(CharacterEnum.ARTIST).size();
            if (numArtist < minArtist) {
                p.losePP(lostPP);
            }else{
                p.gainPP(numArtist * gainedPP);
            }
        });

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
