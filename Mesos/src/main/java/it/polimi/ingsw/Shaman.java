package it.polimi.ingsw;
/**
 * @author Daniele
 */
public class Shaman extends CharacterCard{
    private final int StarCount;

    public Shaman(EraEnum era, int StarCount) {
        super(era);
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
}
