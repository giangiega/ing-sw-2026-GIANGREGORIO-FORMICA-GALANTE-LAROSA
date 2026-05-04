package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.EraEnum;

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
     * this method will add the card to the tribe , update the total starcount of the specific player and trigger the
     * possible  building effect
     * @param player
     * @param board
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
