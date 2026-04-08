package it.polimi.ingsw;
/**
 * @author Giuse
 */
public class BuildingSaveShamanPP extends BuildingEffect {
    private int lostPP;

    /**
     * @param PP : amount of PP lost by losing the event "Shaman Ritual"
     * this method updates lostPP
     */
    public void setLostPP(int PP){
        this.lostPP = PP;
    }
    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * this method gives back the exact amount of prestige points lost during
     * the event "Shaman Ritual"
     */
    @Override
    public void applyEventShaman(Player p, Board b){
        p.gainPP(lostPP);
    }
}
