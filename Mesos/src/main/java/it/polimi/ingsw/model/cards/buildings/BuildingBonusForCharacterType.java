package it.polimi.ingsw.model.cards.buildings;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;

/**
 * @author Giuse
 */
public class BuildingBonusForCharacterType extends BuildingEffect{
    private int PP;
    private CharacterEnum character;

    /**
     * this is the constructor
     * @param PP : prestige points
     * @param character : type of character
     */
    public BuildingBonusForCharacterType(int PP, CharacterEnum character) {
        this.PP = PP;
        this.character = character;
    }

    /**
     * @return the prestige points multiplier
     */
    public int getPP(){
        return this.PP;
    }

    /**
     * @return the character type of the card needed to acquire the pp
     */
    public CharacterEnum getCharacter(){
        return this.character;
    }
    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * this method calculates the amount of prestige points awarded by the building based on
     * the number of character that have the same type indicated in the card
     */
    @Override
    public void applyEndGame(Player p, Board b){
        p.gainPP(PP * p.getCharacterByType(character).size());
    }
    /**
     * This method prints out the building information
     */
    @Override
    public String toString(){
        return "BuildingBonusForCharacterType: " +
                "for each character " +  this.character + " it gives " + this.PP + " prestige points ";
    }
}
