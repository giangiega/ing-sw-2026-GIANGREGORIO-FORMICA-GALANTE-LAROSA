package it.polimi.ingsw;
/**
 * @author Giuse
 */
public class BuildingBonusDoubleShamanPP extends BuildingEffect{
    private int gainedPP;
    /**
     * this is the constructor of the class
     * @param gainedPP : must be the exact amount of pp gained during the event "Shaman Ritual"
     */
    public  BuildingBonusDoubleShamanPP(int gainedPP){
        this.gainedPP=gainedPP;
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