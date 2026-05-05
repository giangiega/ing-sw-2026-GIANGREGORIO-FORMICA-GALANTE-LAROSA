package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.CharacterEnum;

/**
 * @author Giuse
 */
public class BuildingDiscountFood extends BuildingEffect {
    private static final int DISCOUNT_PER_CHARACTER = 1;
    private CharacterEnum characterType;

    public BuildingDiscountFood(CharacterEnum characterType) {
        this.characterType = characterType;
    }
    /**
     * @return the type of the character needed to receive a discount
     */
    public CharacterEnum getCharacterType() {
        return characterType;
    }
    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * this method calculates the discount and gives back that amount of food
     */
    @Override
    public void applyEventSustenance(Player p, Board b){
        p.gainFood((p.getCharacterByType(characterType).size() *  DISCOUNT_PER_CHARACTER));
    }
    /**
     * This method prints out the building information
     */
    @Override
    public String toString(){
        return "BuildingDiscountFood: " +
                "for each character " +  this.characterType + " it gives 1 food discount ";
    }
}
