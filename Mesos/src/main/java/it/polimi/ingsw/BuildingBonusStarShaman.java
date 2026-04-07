package it.polimi.ingsw;
/**
 * @author Giuse
 */

public class BuildingBonusStarShaman extends BuildingEffect{
    private int extraStars;
    /**
     * this is the constructor of the class
     * @param extraStars : numbers of extra stars to count during the event "Shaman Ritual"
     */
    public BuildingBonusStarShaman(int extraStars) {
        this.extraStars = extraStars;
    }

    /**
     *
     * @return extraStars : number of stars
     */
    public int getExtraStars() {
        return extraStars;
    }

    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is empty: all the logic is contained in EventShamanRitual
     */
    @Override
    public void applyEventShaman(Player p, Board b){}
}

