package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
public class BuildingFinal25PP extends BuildingEffect{
    //Constant definition for endgame prestige points awarded
    private static final int END_GAME_POINTS = 25;

    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * this method gives 25 prestige points to the player who owns this building at
     * the end of the game
     */
    @Override
    public void applyEndGame(Player p, Board b){
        p.gainPP(END_GAME_POINTS);
    }
}
