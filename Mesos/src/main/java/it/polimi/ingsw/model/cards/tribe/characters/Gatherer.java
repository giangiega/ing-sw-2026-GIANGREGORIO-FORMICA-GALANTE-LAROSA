package it.polimi.ingsw.model.cards.tribe.characters;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;

/**
 * @author Daniele
 */
public class Gatherer extends CharacterCard{

    public Gatherer(EraEnum era, int numPlayers) {
        super(era, numPlayers);
    }

    /**
     * @param player player whose tribe must be updated with new card
     * @param board
     * This method will add the card to the tribe and trigger the possible building effect
     */
    @Override
    public void AddToPlayerTribe(Player player, Board board){
        player.getCharacterByType(CharacterEnum.GATHERER).add(this);
    }

    @Override
    public String toString() {
        return "Gatherer | Era: " + getEra();
    }
}
