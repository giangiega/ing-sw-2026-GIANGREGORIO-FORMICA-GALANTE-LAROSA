package it.polimi.ingsw;
/**
 * @author Giuse
 */
public class BuildingBonusDoubleShamanPP extends BuildingEffect{
    private int gainedPP;

    /**
     * @param PP : amount of PP won by winning the event "Shaman Ritual"
     * this method updates gainedPP
     */
    public void setGainedPP(int PP){
        this.gainedPP = PP;
    }
    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * this method awards the exact amount of prestige points gained during
     * the event "Shaman Ritual"
     */
    @Override
    public void applyEventShaman(Player p, Board b){
        p.gainPP(gainedPP);
    }
}