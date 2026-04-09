package it.polimi.ingsw;
/**
 * @author Daniele
 */
public class Gatherer extends CharacterCard{

    public Gatherer(EraEnum era, int numPlayers) {
        super(era, numPlayers);
    }

    /**
     * this method will add the card to the tribe and trigger the possible building effect
     * @param player
     * @param board
     */
    @Override
    public void AddToPlayerTribe(Player player, Board board){
        player.getCharacterByType(CharacterEnum.GATHERER).add(this);

    }
}
