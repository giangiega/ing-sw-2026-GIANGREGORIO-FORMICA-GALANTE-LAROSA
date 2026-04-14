package it.polimi.ingsw;
/**
 * @author Giuse
 */
public class BuildingSaveShamanPP extends BuildingEffect {
    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * this method gives back the exact amount of prestige points lost during
     * the event "Shaman Ritual"
     */
    @Override
    public void applyEventShamanWinner(Player p, Board b,  int lostPP, boolean win){
        if(!win){p.gainPP(lostPP);}
    }
}
