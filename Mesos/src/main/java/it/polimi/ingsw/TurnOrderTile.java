package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

/**
 * TurnOrderTile class
 * @author Ale
 */
public class TurnOrderTile {
    private final int numPlayers;
    private final int[] foodBonus;
    private final List<Player> slots;

    /**
     * constructor
     * @param
     * @param : list of all players
     */
    public TurnOrderTile(int numPlayers, int[] foodBonus) {
        this.numPlayers = numPlayers;
        this.foodBonus = foodBonus;
        this.slots = new ArrayList<>();
    }

    /**
     * add the player to the slots of the TurnOrderTile
     * @param player
     */
    public void totemIn(Player player){
        slots.add(player);
    }

    /**
     * when a player moves his totem to the offerTrack, his totem will be removed from slots of TurnOrderTile
     * @param player
     */
    public void totemOut(Player player){
        slots.removeIf(p -> p.getName().equals(player.getName()));
    }
    public List<Player> getOrder(){
        return List.copyOf(slots);
    }

    //constructor has foodBonus from a GameConfig method
   public int getFoodBonusForSlot(int pos){
        return foodBonus[pos];
    }

    public boolean isLastSlot(int pos) {
        return pos == numPlayers - 1;
    }

}
