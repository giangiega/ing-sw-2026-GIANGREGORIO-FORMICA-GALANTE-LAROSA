package it.polimi.ingsw;
/**
 * @author Daniele
 */

public class Inventor extends CharacterCard {
    private final IconEnum iconType;

    public Inventor(EraEnum era, int numPlayers, IconEnum iconType) {
        super(era, numPlayers);
        this.iconType = iconType;
    }
    public IconEnum getIconType()
    {
        return iconType;
    }

    /**
     * when a player draw an Inventor this method will check if it is the first of that specific type and use
     * updateDistinctInventorsIcon() to update the count , then it will add the card to the tribe and trigger the
     * possible building effect
     * @param player
     * @param board
     */
    @Override
    public void AddToPlayerTribe(Player player, Board board){
        boolean isFirst;

        isFirst = player.getCharacterByType(CharacterEnum.INVENTOR).stream().map(c-> (Inventor) c).noneMatch(inv -> inv.getIconType() == this.iconType);

        if(isFirst){
            player.updateDistinctInventorsIcon();
        }
        player.getCharacterByType(CharacterEnum.INVENTOR).add(this);

    }


}
