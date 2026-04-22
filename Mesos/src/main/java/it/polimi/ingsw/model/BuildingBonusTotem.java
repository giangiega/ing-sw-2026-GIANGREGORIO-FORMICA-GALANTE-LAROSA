package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
public class BuildingBonusTotem extends BuildingEffect{
    private static final int BONUS_FOOD_TOTEM = 1;
    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * this method awards 1 extra food if the player totem is on the right spots
     */
    @Override
    public void applyEndTurn(Player p, Board b, int chosenIndex, boolean chosenIsBuilding ){
        p.gainFood(BONUS_FOOD_TOTEM);
    }
}
