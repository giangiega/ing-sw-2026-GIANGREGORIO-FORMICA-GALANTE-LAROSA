package it.polimi.ingsw;
/**
 * @author Giuse
 */
public class BuildingBonusDoubleShamanPP extends BuildingEffect{
    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * this method awards the exact amount of prestige points gained during
     * the event "Shaman Ritual"
     */
    @Override
    public void applyEventShamanWinner(Player p, Board b, int gainedPP, boolean win){
        if(win){p.gainPP(gainedPP);}
    }
}