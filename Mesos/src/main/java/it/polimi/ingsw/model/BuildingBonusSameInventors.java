package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
public class BuildingBonusSameInventors extends BuildingEffect{
    private static final int SAME_INVENTORS_BONUS = 3;
    private int initialInventorsCouples = 0;

    @Override
    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * this method gives 3 food for each couple made of the same inventors
     * it doesn't give you anything right after buying the building
     */
    public void applyOnCardAdded(Player p, Board b) {
        int realCouples = p.getCoupleSameInventors();
        p.gainFood((realCouples - initialInventorsCouples) * SAME_INVENTORS_BONUS);
        initialInventorsCouples = realCouples;
    }
    /**
     * This method prints out the building information
     */
    @Override
    public String toString(){
        return "BuildingBonusSameInventors ";
    }
}