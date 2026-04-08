package it.polimi.ingsw;
/**
 * @author Daniele
 */

public abstract class CharacterCard extends TribeCard {
    public CharacterCard(EraEnum era){
        super(era);
    }

   public abstract void AddToPlayerTribe(Player player, Board board);

}


