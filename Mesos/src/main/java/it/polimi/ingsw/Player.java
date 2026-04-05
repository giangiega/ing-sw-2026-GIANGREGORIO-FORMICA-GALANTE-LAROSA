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
    private String name;
    private ColorEnum totemColor;
    private Map<CharacterEnum, List<CharacterCard>> tribe;
    private List<BuildingCard> buildings;
    private int food;
    private int prestigePoints;
    private int countShamanStar;
    private int gatherersCount;
    private int totalFoodDiscountBuilder;
    private int huntersCount;
    private int artistCount;
    private int distinctInventorsIcon;
    private int completedSetsCount;

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

    /**
     * this method will add the character that a player draw from board
     * @param card
     */
    public void addCharacterCard(CharacterCard card){
        /* with sublists model for tribe we should eliminate this method and handle adding of a character in
        *  each character class with an overrided method:
        card.addToPlayerTribe(player) */
    }

    /**
     * this method will add the building that a player draw from board
     * @param card
     */
    public void addBuildingCard(BuildingCard card){
        buildings.add(card);
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

    public int getGatherersCount(){
        return gatherersCount;
    }

    public int getTotalFoodDiscountBuilder(){
        return totalFoodDiscountBuilder;
    }

    public int getHuntersCount(){
        return huntersCount;
    }

    public int getArtistCount(){
        return artistCount;
    }

    public int getDistinctInventorsIcon(){
        return distinctInventorsIcon;
    }

    public int getCompletedSetsCount(){
        return completedSetsCount;
    }

    /**
     * when a player draw a shaman countShamanStar will be update using the number of star on the specific card
     * @param numStar
     */
    public void updateTotalStarCount(int numStar){
        countShamanStar += numStar;
    }

    public void updateGatherersCount(){
        gatherersCount++;
    }

    /**
     * when a player draw a builder totalFoodDiscountBuilder will be update using the discount on the specific card
     * @param discountBuilder
     */
    public void updateTotalFoodDiscountBuilder(int discountBuilder){
        totalFoodDiscountBuilder += discountBuilder;
    }

    public void updateHuntersCount(){
        huntersCount++;
    }

    public void updateArtistCount(){
        artistCount++;
    }

    public void updateDistinctInventorsIcon(){
        distinctInventorsIcon++;
    }

    public void updateCompletedSetsCount(){
        completedSetsCount++;
    }
}
