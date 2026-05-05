package it.polimi.ingsw.model.cards.tribe.events;
/**
 * @author Daniele
 */
import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;

import java.util.List;

public class EventHunt extends EventCard {
    private final int ppPerHunter;

    public EventHunt(EraEnum era, boolean isFinalEvent, int ppPerHunter) {
        super(era, isFinalEvent);
        this.ppPerHunter = ppPerHunter;
    }

    /**
     * this method will scroll through the list of players and based on the possible building of a player , it will
     * change the quantity of food and PP gained by that specific player
     * @param players
     * @param board
     */
    @Override
    public void resolve(List<Player> players, Board board) {

        players.forEach(p -> {
            p.getBuildingCards().forEach(b -> b.getEffect().applyEventHunt(p, board));

            int hunterCount = p.getCharacterByType(CharacterEnum.HUNTER).size();
            if (hunterCount > 0) {
                p.gainFood(hunterCount);
                p.gainPP(hunterCount * ppPerHunter);
            }
        });
    }

    public int getPpPerHunter() {
        return ppPerHunter;
    }

    @Override
    public String toString(){
        return "Event - Hunt | Each Hunter gives: +1 Food, +" + getPpPerHunter() + "PP";
    }
}

