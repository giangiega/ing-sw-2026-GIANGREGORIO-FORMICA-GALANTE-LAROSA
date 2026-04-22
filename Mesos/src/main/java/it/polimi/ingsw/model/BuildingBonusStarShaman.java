package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
public class BuildingBonusStarShaman extends BuildingEffect{
    private int firstAdd = -1;
    private static final int BONUS_EXTRA_STARS = 3;
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method gives 3 extra shaman stars
     */
    @Override
    public void applyEventShamanBonusStars(Player p, Board b){
        if(firstAdd == -1){//It should give extra stars only the first time
            p.setEffectiveStars(BONUS_EXTRA_STARS);
            firstAdd++;
        }
    }
}

