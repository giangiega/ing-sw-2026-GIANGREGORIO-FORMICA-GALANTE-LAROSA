package it.polimi.ingsw.model.boardAndTiles;

import it.polimi.ingsw.model.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * TurnOrderTile class
 * @author Ale
 */
public class TurnOrderTile implements Serializable {
    private final int numPlayers;
    private final int[] foodBonus;
    private final List<Player> slots;

    /**
     * Constructor of this class
     * @param numPlayers number of players
     * @param foodBonus: list of bonuses
     */
    public TurnOrderTile(int numPlayers, int[] foodBonus) {
        this.numPlayers = numPlayers;
        this.foodBonus = foodBonus;
        this.slots = new ArrayList<>();
    }

    /**
     * @param player player to move
     * This method adds the player to the slots of the TurnOrderTile
     */
    public void totemIn(Player player){
        slots.add(player);
    }

    /**
     * when a player moves his totem to the offerTrack, his totem will be removed from slots of TurnOrderTile
     * @param player player whose totem must be removed from TurnOrderTile
     */
    public void totemOut(Player player){
        slots.removeIf(p -> p.getName().equals(player.getName()));
    }
    public List<Player> getOrder(){
        return List.copyOf(slots);
    }

    /**
     * @author Giuse
     * @param disconnectedNames names of all currently disconnected players
     * This method moves all disconnected players to the END of the slots list, preserving
     * their relative order among themselves and keeping connected players first.
     * Called at the start of every placement phase so that players who missed
     * one or more rounds (and whose totems therefore never cycled through
     * totemOut → totemIn) don't end up at the front of the order simply because
     * they stayed in slots while everyone else rotated around them.
     */
    public void moveDisconnectedToEnd(List<String> disconnectedNames) {
        List<Player> disconnected = new ArrayList<>();
        slots.removeIf(p -> {
            if (disconnectedNames.contains(p.getName())) {
                disconnected.add(p);
                return true;
            }
            return false;
        });
        slots.addAll(disconnected);
    }

    //constructor has foodBonus from a GameConfig method
   public int getFoodBonusForSlot(int pos){
        return foodBonus[pos];
    }

    public boolean isLastSlot(int pos) {
        return pos == numPlayers - 1;
    }

    public List<Player> getSlots(){
        return List.copyOf(slots);
    }
    public int getNumPlayers(){
        return numPlayers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < slots.size(); i++) {
            Player p = slots.get(i);
            if (p != null)
                sb.append(String.format("  %d. [%s] %s%n",
                        i + 1, p.getTotemColor().name(), p.getName()));
            else
                sb.append(String.format("  %d. [ empty ]%n", i + 1));
        }
        return sb.toString();
    }

}
