package it.polimi.ingsw.model.cards.buildings;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;

/**
 * @author Giuse
 */
public class BuildingBonusArtist extends BuildingEffect{
    private static final int BONUS_FOOD_PER_ARTIST = 1;

    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * This method awards the player a certain amount of food during the event Cave Painting
     * this amount is based on the number of painters
     */
    @Override
    public void applyEventCavePainting(Player p, Board b){
        p.gainFood((p.getCharacterByType(CharacterEnum.ARTIST)).size() * BONUS_FOOD_PER_ARTIST);
    }

    /**
     * This method prints out the building information
     */
    @Override
    public String toString(){
        return "BuildingBonusArtist ";
    }
}
