package it.polimi.ingsw.model.cards.buildings;

import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;

/**
 * @author Giuse
 */
public class BuildingBonusTotem extends BuildingEffect{
    private static final int BONUS_FOOD_TOTEM = 1;


    /**
     *@param p : player who has this building card
     *@param b  state of the board
     * This method awards 1 extra food when the player's totem lands on a
     * TurnOrderTile slot that has a food bonus. Never triggers on the last slot.
     */
    @Override
    public void applyTotemFoodBonus(Player p, Board b){
        p.gainFood(BONUS_FOOD_TOTEM);
    }

    /**
     * This method prints out the building information
     */
    @Override
    public String toString(){
        return "BuildingBonusTotem ";
    }
}
