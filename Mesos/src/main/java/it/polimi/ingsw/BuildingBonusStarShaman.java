package it.polimi.ingsw;
/**
 * @author Giuse
 */

public class BuildingBonusStarShaman extends BuildingEffect{
    private static final int BONUS_EXTRA_STARS = 3;
    /**
     *
     * @return extraStars : number of extra stars
     */
    public int getExtraStars() {
        return BONUS_EXTRA_STARS;
    }

    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is empty: all the logic is contained in EventShamanRitual
     */
    @Override
    public void applyEventShaman(Player p, Board b){}
}

