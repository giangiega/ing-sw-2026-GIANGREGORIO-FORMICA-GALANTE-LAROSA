package it.polimi.ingsw;
/**
 * @author Giuse
 */
public class BuildingFoodSet extends BuildingEffect {
    private static final int DISCOUNT_PER_SET = 5;
    private int initialSets = -1;

    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method gives the player 5 foods for completed set.
     * The first time applayOnCardAdded is called, initialsSets is -1, to ensure no food is given
     * For the following calls, the right amount of food is calculated
     */
    @Override
    public void applyOnCardAdded(Player p, Board b) {
        int actualSets = p.getCompletedSetsCount();

        if (initialSets == -1) {
            initialSets = actualSets;
            return;
        }else {
            p.gainFood((actualSets - initialSets) * DISCOUNT_PER_SET);
            initialSets = actualSets;
        }
    }
}
