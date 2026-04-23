package it.polimi.ingsw.model;
/**
 * @author Daniele
 */
import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.EraEnum;

import java.util.List;

public class EventSustenance extends EventCard {
    private final int ppPerUnfedCharacter;

    public EventSustenance(EraEnum era, boolean isFinalEvent, int ppPerUnfedCharacter) {
        super(era, isFinalEvent);
        this.ppPerUnfedCharacter = ppPerUnfedCharacter;

    }

    @Override
    public boolean isSustenance() {
        return true;
    }

    /**
     * this method will scroll through the list of players and calculate , based on the possible building's effect
     * and the discount of the gatherers, the total amount of food to pay and the eventual loss of PP
     *
     * @param players
     * @param board
     */
    @Override
    public void resolve(List<Player> players, Board board) {
        players.forEach(p -> {
            p.getBuildingCards().forEach(b -> b.getEffect().applyEventSustenance(p, board));

            int totalCharacters = p.getTotalCharactersCount();
            int numGatherers = p.getCharacterByType(CharacterEnum.GATHERER).size();
            int totalFoodToPay = Math.max(0, totalCharacters - (numGatherers * 3));

            if(p.getFood() >= totalFoodToPay){
                p.payFood(totalFoodToPay);
            }else{
                int remainingCharacters = totalFoodToPay -  p.getFood();
                p.payFood(p.getFood());
                p.losePP(remainingCharacters * ppPerUnfedCharacter);
            }



        });

    }
    public int getPpPerUnfedCharacter() {
        return ppPerUnfedCharacter;
    }
}
