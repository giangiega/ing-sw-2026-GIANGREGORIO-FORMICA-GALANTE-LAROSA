package it.polimi.ingsw.model.cards.tribe.characters;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;

/**
 * @author Daniele
 */
public class Shaman extends CharacterCard{
    private final int StarCount;

    public Shaman(EraEnum era, int numPlayers, int StarCount){
        super(era, numPlayers);
        this.StarCount = StarCount;
    }

    /**
     * @param player player whose tribe must be updated
     * @param board
     * This method will add the card to the tribe, update the total starCount of the
     * specific player and trigger the possible  building effect
     */
    @Override
    public void AddToPlayerTribe(Player player, Board board){
        player.getCharacterByType(CharacterEnum.SHAMAN).add(this);
        player.updateTotalStarCount(this.StarCount);
    }

    public int getStarCount()
    {
        return  StarCount ;
    }

    @Override
    public String toString(){
        return "Shaman | Stars: " + StarCount + " | Era: " + getEra();
    }
}
