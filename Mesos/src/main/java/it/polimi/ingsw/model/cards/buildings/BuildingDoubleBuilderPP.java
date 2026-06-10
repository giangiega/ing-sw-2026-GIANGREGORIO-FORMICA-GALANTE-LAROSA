package it.polimi.ingsw.model.cards.buildings;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.tribe.characters.Builder;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;

/**
 * @author Giuse
 */
public class BuildingDoubleBuilderPP extends BuildingEffect{

    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * This method gives the player an amount of prestige points based on the builders' bonuses,
     * doubling the actual reward
     */
    @Override
    public void applyEndGame(Player p, Board b){
        int sum = 0;
        for(CharacterCard c : p.getCharacterByType(CharacterEnum.BUILDER)){
            sum = sum + ((Builder) c).getEndGamePP();
        }
        p.gainPP(sum);
    }

    /**
     * This method prints out the building information
     */
    @Override
    public String toString(){
        return "BuildingDoubleBuilderPP ";
    }
}