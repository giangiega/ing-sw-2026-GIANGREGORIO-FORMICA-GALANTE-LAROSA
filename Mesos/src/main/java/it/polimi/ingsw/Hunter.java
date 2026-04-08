package it.polimi.ingsw;
/**
 * @author Daniele
 */
public class Hunter extends CharacterCard{

    private final boolean hunt;

    public Hunter(EraEnum era, boolean hunt) {
        super(era);
        this.hunt = hunt;

    }

    /**
     * this method will add the card to the tribe, check if the hunter has the icon and eventually give the
     * food to player based on the numHunters and then trigger the possible building effect
     * @param player
     * @param board
     */
    @Override
    public void AddToPlayerTribe(Player player, Board board){
    player.getCharacterByType(CharacterEnum.HUNTER).add(this);

    if(this.hunt){
        int numHunters;

        numHunters = player.getCharacterByType(CharacterEnum.HUNTER).size();
        player.gainFood(numHunters);
    }
    }
    public boolean getHunt(){
        return hunt;
    }
}
