package it.polimi.ingsw;
/**
 * @author Giuse
 */
public class BuildingSaveShamanPP extends BuildingEffect {
    private int lostPP;
    /**
     * this is the constructor of the class
     * @param lostPP : must be the exact amount of pp lost during the event "Shaman Ritual"
     */
    public BuildingSaveShamanPP(int lostPP) {
        this.lostPP = lostPP;
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
