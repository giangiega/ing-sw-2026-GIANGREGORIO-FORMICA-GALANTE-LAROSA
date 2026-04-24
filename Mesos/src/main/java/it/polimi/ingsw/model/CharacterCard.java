package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.EraEnum;

/**
 * @author Daniele
 */

public abstract class CharacterCard extends TribeCard {
    private final int numPlayers;

    /**
     * constructor
     * @param era
     * @param numPlayers: this is the minimum number of players for the card
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


