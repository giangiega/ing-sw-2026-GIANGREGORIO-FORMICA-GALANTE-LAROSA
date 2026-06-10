package it.polimi.ingsw.model.cards.buildings;

import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;

/**
 * @author Giuse
 */
public class BuildingBonusDoubleShamanPP extends BuildingEffect{

    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * This method awards the exact amount of prestige points gained during
     * the event "Shaman Ritual"
     */
    @Override
    public void applyEventShamanWinner(Player p, Board b, int gainedPP, boolean win){
        if(win){p.gainPP(gainedPP);}
    }

    /**
     * This method prints out the building information
     */
    @Override
    public String toString(){
        return "BuildingBonusDoubleShamanPP ";
    }
}