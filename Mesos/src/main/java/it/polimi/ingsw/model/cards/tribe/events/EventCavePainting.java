/**
 * @author Daniele
 */

package it.polimi.ingsw.model.cards.tribe.events;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;

import java.util.List;

public class EventCavePainting extends EventCard {

    private final int minArtist;
    private final int gainedPP;
    private final int lostPP;

    public EventCavePainting(EraEnum era, boolean isFinalEvent, int minArtist, int gainedPP, int lostPP) {
        super(era, isFinalEvent);
        this.minArtist = minArtist;
        this.gainedPP = gainedPP;
        this.lostPP = lostPP;
    }

    /**
     * @param players list of players
     * @param board current board
     * This method will scroll through the list of players, check if they have the specific building for this event
     * and then adds or subtracts the PP based on the artist's number of the player
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

    @Override
    public String toString() {
        return "Event - Cave Painting | If you have less then " + minArtist +
                " Artists, You lose :  -" + lostPP +
                " PP | If you have at least  " + minArtist +
                " Artists, you gain: +" + gainedPP + " PP each";
    }
}
