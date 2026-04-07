package it.polimi.ingsw;
/**
 * @author Giuse
 */
public abstract class BuildingEffect {
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is called when a player acquires a building card
     */
    public void applyOnCardAdded(Player p, Board b){}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is called during the following event: "Sustenance"
     */
    public void applyEventSustenance(Player p, Board b){}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is called during the following event: "Hunt"
     */
    public void applyEventHunt(Player p, Board b){}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is called during the following event: "Shaman Ritual"
     */
    public void applyEventShaman(Player p, Board b){}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is called during the following event: "Cave Painting"
     */
    public void applyEventCavePainting(Player p, Board b){}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is called at the end of a turn
     */
    public void applyEndTurn(Player p, Board b){}
    /**
     * @param p : player who has this building card
     * @param b : state of the board
     * This method is called at the end of the game
     */
    public void applyEndGame(Player p, Board b){}
}
