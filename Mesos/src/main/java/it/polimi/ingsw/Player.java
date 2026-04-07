package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Player class with usefully attribute to handle events and buildings effect
 * @author Ale
 */
public class Player {
    private final String name;
    private final ColorEnum totemColor;
    private Map<CharacterEnum, List<CharacterCard>> tribe;
    private List<BuildingCard> buildings;
    private int food;
    private int prestigePoints;
    private int countShamanStar;
    private int totalFoodDiscountBuilder;
    private int distinctInventorsIcon;
    private int coupleSameInventors;
    private int completedSetsCount;
    private int endGameBuilderPP;
    private int totalBuildingsPP;

    /**
     * constructor
     * @param name
     * @param totemColor
     */
    public Player(String name, ColorEnum totemColor){
        this.name = name;
        this.totemColor = totemColor;
        this.tribe = new HashMap<>();
        this.tribe.put(CharacterEnum.HUNTER, new ArrayList<>());
        this.tribe.put(CharacterEnum.GATHERER, new ArrayList<>());
        this.tribe.put(CharacterEnum.SHAMAN, new ArrayList<>());
        this.tribe.put(CharacterEnum.BUILDER, new ArrayList<>());
        this.tribe.put(CharacterEnum.ARTIST, new ArrayList<>());
        this.tribe.put(CharacterEnum.INVENTOR, new ArrayList<>());
        this.buildings = new ArrayList<>();
    }

    public void addCharacterCard(CharacterCard card, Board board){
        //card.addToPlayerTribe(this);
        for(BuildingCard b: buildings){
            b.getEffect().applyOnCardAdded(this, board);
        }
    }

    /**
     * this method will add the building that a player draw from board
     * @param card
     */
    public void addBuildingCard(BuildingCard card){
        buildings.add(card);
        totalBuildingsPP += card.getBasePP();
    }

    public void gainFood(int num){
        food += num;
    }

    public void payFood(int num){
        food -= num;
    }

    public void gainPP(int num){
        prestigePoints += num;
    }

    public void losePP(int num){
        prestigePoints -= num;
    }

    public int getFood(){
        return food;
    }

    public int getPP(){
        return prestigePoints;
    }

    /**
     *
     * @param type: CharacterEnum to get a specific list of character
     * @return: list of a specific type of character
     */
    public List<CharacterCard> getCharacterByType(CharacterEnum type){
        return tribe.get(type);
    }

    public List<BuildingCard> getBuildingCards(){
        return buildings;
    }

    public int getTotalStarCount(){
        return countShamanStar;
    }

    public int getTotalFoodDiscountBuilder(){
        return totalFoodDiscountBuilder;
    }

    public int getDistinctInventorsIcon(){
        return distinctInventorsIcon;
    }

    public int getCoupleSameInventors(){
        return coupleSameInventors;
    }

    public int getCompletedSetsCount(){
        return completedSetsCount;
    }

    public int getEndGameBuildersPP(){
        return endGameBuilderPP;
    }

    public int getTotalBuildingsPP(){
        return totalBuildingsPP;
    }

    /**
     * when a player draw a shaman countShamanStar will be update using the number of star on the specific card
     * @param numStar
     */
    public void updateTotalStarCount(int numStar){
        countShamanStar += numStar;
    }

    /**
     * when a player draw a builder totalFoodDiscountBuilder will be update using the discount on the specific card
     * @param discountBuilder
     */
    public void updateTotalFoodDiscountBuilder(int discountBuilder){
        totalFoodDiscountBuilder += discountBuilder;
    }

    public void updateDistinctInventorsIcon(){
        distinctInventorsIcon++;
    }

    public void updateCoupleSameInventors(){
        coupleSameInventors++;
    }

    public void updateCompletedSetsCount(){
        completedSetsCount++;
    }

    public void updateEndGameBuilderPP(int PP){
        endGameBuilderPP += PP;
    }

    public void updateTotalBuildingsPP(int PP){
        totalBuildingsPP += PP;
    }
}
