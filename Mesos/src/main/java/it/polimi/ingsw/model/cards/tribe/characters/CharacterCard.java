package it.polimi.ingsw.model.cards.tribe.characters;

import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.tribe.TribeCard;

/**
 * @author Daniele
 */

public abstract class CharacterCard extends TribeCard {
    private final int numPlayers;

    /**
     * Constructor of this class
     * @param era : era of this card
     * @param numPlayers : this is the minimum number of players for the card
     */
    public CharacterCard(EraEnum era, int numPlayers){
        super(era);
        this.numPlayers = numPlayers;
    }

   public abstract void AddToPlayerTribe(Player player, Board board);

    public int getNumPlayers(){
        return numPlayers;
    }
}


