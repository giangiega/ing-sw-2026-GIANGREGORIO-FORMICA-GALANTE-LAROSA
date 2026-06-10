package it.polimi.ingsw.model.cards.buildings;

import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;

/**
 * @author Giuse
 */
public class BuildingCard extends Card {
    private int baseFoodCost;
    private int basePrestigePoints;
    private BuildingEffect effect;

    /**
     * Constructor of this class
     * @param era : era of this card
     * @param baseFoodCost : food cost of this card
     * @param basePrestigePoints : prestige points awarded by this card
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
     * @return the base amount of prestige points awarded by the building at the end of the game
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
     * This method prints out the building information
     */
    @Override
    public String toString(){
        return  "BuildingCard with effect: " + effect.toString() + "and -->" +
                "\n\t\tEra : " + getEra() +
                "\n\t\tFood cost : " + baseFoodCost +
                "\n\t\tPrestige point earned : " + basePrestigePoints;
    }

    // only used for PLAYER STATUS update
    public String updateCard() {
        return effect.toString() + "| food: " + baseFoodCost + " | PP: " + basePrestigePoints +
                " | era: " + getEra() ;
    }
}
