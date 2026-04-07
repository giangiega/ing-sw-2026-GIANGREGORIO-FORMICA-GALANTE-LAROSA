package it.polimi.ingsw;
/**
 * @author Giuse
 */
public class BuildingBonusHunt extends BuildingEffect{
    private static final int BONUS_FOOD_PER_HUNTER = 1;
    private static final int BONUS_PP_PER_HUNTER = 1;
    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * this method gives the player food and prestige points during the event Hunt
     * this number is based on the number of hunters
     */
    @Override
    public void applyEventHunt(Player p, Board b){
        int huntersNumber = p.getCharacterByType(CharacterEnum.HUNTER).size();
        p.gainFood(huntersNumber * BONUS_FOOD_PER_HUNTER);
        p.gainPP(huntersNumber * BONUS_PP_PER_HUNTER);
    }
}