package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Player class with usefully attribute to handle events and buildings effect
 * @author Ale
 */
public class Player {
    private final String name;
    private final ColorEnum totemColor;
    private final Map<CharacterEnum, List<CharacterCard>> tribe;
    private final List<BuildingCard> buildings;
    private int food;
    private int prestigePoints;
    private int countShamanStar;
    private int effectiveStars;
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
        card.AddToPlayerTribe(this, board);
        if(this.checkCompletedSet())
            updateCompletedSetsCount();
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

    public String getName(){
        return name;
    }

    public ColorEnum getTotemColor(){
        return totemColor;
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
        effectiveStars += numStar;
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
    /**
     * @author Giuse
     * @param extraStars : number of extra stars given to the player
     * this method gives the player 3 extra stars to the player
     */
    public void setEffectiveStars(int extraStars){
        effectiveStars = effectiveStars + extraStars;
    }
    /**
     * @author Giuse
     * @return effectiveStars : number of stars of the player considering the eventual bonuses
     */
    public int getEffectiveStars(){
        return effectiveStars;
    }

    /**
     * @author Daniele
     * @return the total amount of the characters in the tribe
     */
    public int getTotalCharactersCount(){
        int totalCharactersCount = 0;

        for(List<CharacterCard> cards: tribe.values()){
            totalCharactersCount += cards.size();
        }

        return totalCharactersCount;
    }

    public Map<CharacterEnum, List<CharacterCard>> getTribe(){
        return Collections.unmodifiableMap(tribe);
    }

    /**
     *  check if a set is completed or not
     */
    public boolean checkCompletedSet(){
        int min = IntStream.of(tribe.get(CharacterEnum.ARTIST).size(),
                tribe.get(CharacterEnum.SHAMAN).size(),
                tribe.get(CharacterEnum.HUNTER).size(),
                tribe.get(CharacterEnum.GATHERER).size(),
                tribe.get(CharacterEnum.INVENTOR).size(),
                tribe.get(CharacterEnum.BUILDER).size()
        ).min().getAsInt();
        if(min > completedSetsCount)
            return true;

        return false;
    }
}

