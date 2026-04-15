package it.polimi.ingsw;
/**
 * @author Giuse
 */
public class BuildingCard extends Card {
    private int baseFoodCost;
    private int basePrestigePoints;
    private BuildingEffect effect;

    /**
     * constructor of this class
     * @param era :era of this card
     * @param baseFoodCost : food cost of this card
     * @param basePrestigePoints :prestige points awarded by this card
     * @param effect : effect of this card
     */
    public BuildingCard(EraEnum era, int baseFoodCost, int basePrestigePoints, BuildingEffect effect) {
        super(era);

        this.baseFoodCost = baseFoodCost;
        this.basePrestigePoints = basePrestigePoints;
        this.effect = effect;
    }

    /**
     * @return the base food cost to purchase the building
     */
    public int getBaseFC() {
        return baseFoodCost;
    }

    /**
     * @return the base amount of prestiege points awarded by the building at the end of the game
     */
    public int getBasePP() {
        return basePrestigePoints;
    }
    /**
     * @return the effect of the building
     */
    public BuildingEffect getEffect(){
        return effect;
    }
    /**
     * @param p : player who has this building card
     * @return the real amount of food needed to actually buy the building: each hunter lowers its cost
     *         The cost cannot be lower than 0
     */
    public int getCost(Player p){
        int real_cost;

        real_cost = baseFoodCost - p.getTotalFoodDiscountBuilder();
        if(real_cost >= 0){
            return real_cost;
        }else{
            return 0;
        }
    }
    /**
     *This method applies the building effect when it's just been acquired by the player
     * @param p : player who has this building card
     * @param b
     */
    public void applyEffect(Player p, Board b){
        effect.applyOnCardAdded(p, b);
    }
}
