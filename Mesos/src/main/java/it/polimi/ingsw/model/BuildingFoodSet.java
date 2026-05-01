package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
public class BuildingFoodSet extends BuildingEffect {
    private static final int DISCOUNT_PER_SET = 5;
    private int initialSets = 0;

    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method gives the player 5 foods for completed set.
     */
    @Override
    public void applyOnCardAdded(Player p, Board b) {
        int actualSets = p.getCompletedSetsCount();
        p.gainFood((actualSets - initialSets) * DISCOUNT_PER_SET);
        initialSets = actualSets;
    }
    /**
     * This method prints out the building information
     */
    @Override
    public String toString(){
        return "BuildingFoodSet ";
    }
}
