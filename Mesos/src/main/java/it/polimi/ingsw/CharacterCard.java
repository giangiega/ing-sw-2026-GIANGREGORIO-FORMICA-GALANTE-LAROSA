package it.polimi.ingsw;
/**
 * @author Daniele
 */

public abstract class CharacterCard extends TribeCard {
    public CharacterCard(EraEnum era){
        super(era);
    }

   public abstract void AddToPlayerTribe(Player player, Board board);

    protected void triggerBuildingEffect(Player player, Board board){
        for(BuildingCard b : player.getBuildingCards()){
            b.getEffect().applyOnCardAdded(player, board);
        }
    }
}


