package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.CharacterEnum;

/**
 * @author Giuse
 */
public class BuildingDoubleBuilderPP extends BuildingEffect{
    /**
     *@param p : player who has this building card
     *@param b : state of the board
     * this method gives the player an amount of prestige points based on the builders' bonuses,
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
}