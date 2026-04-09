package it.polimi.ingsw;
/**
 * @author Daniele
 */
public class Artist extends CharacterCard{

    public Artist(EraEnum era, int numPlayers) {
        super(era, numPlayers);
    }

    /**
     * this method will add the card to the tribe and trigger the possible building effect
     * @param player
     * @param board
     */
    @Override
    public void AddToPlayerTribe(Player player, Board board){
        player.getCharacterByType(CharacterEnum.ARTIST).add(this);


    }
}
