package it.polimi.ingsw.model.cards.buildings;

import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;

/**
 * @author Giuse
 */
public class BuildingPPForSet extends BuildingEffect{
    private static final int BONUS_PER_FINAL_SET = 6;
    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * This method awards the player a certain amount of prestige point:
     * 6 for each set completed
     */
    @Override
    public void applyEndGame(Player p, Board b){
        p.gainPP(p.getCompletedSetsCount() * BONUS_PER_FINAL_SET);
    }

    /**
     * This method prints out the building information
     */
    @Override
    public String toString(){
        return "BuildingPPForSet ";
    }
}

