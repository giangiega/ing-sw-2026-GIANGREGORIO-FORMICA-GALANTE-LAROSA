package it.polimi.ingsw.model.boardAndTiles;

import it.polimi.ingsw.exceptions.InvalidPlayerActionException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * OfferTile class
 * @author Ale
 */
public class OfferTile implements Serializable {
    private final char letter;
    private final int upperArrow;
    private final int lowerArrow;
    private boolean isFree = true;
    private Player occupant;

    /**
     * constructor
     * @param letter: the letter on the card, is used in GameConfig classes
     * @param upperArrow: count of upperArrow on the card
     * @param lowerArrow: count of lowerArrow on the card
     */
    public OfferTile(char letter, int upperArrow, int lowerArrow) {
        this.letter = letter;
        this.upperArrow = upperArrow;
        this.lowerArrow = lowerArrow;
    }

    public char getLetter(){
        return letter;
    }

    public int getCountUpperArrow(){
        return upperArrow;
    }

    public int getCountLowerArrow(){
        return lowerArrow;
    }

    public Player getOccupant() {
        return occupant;
    }

    public boolean getFreeOfferTile(){
        return isFree;
    }

    public void setOccupant(Player occupant) {
        this.occupant = occupant;
        this.isFree = false;
        if (occupant == null)
            this.isFree = true;
    }

    /**
     * @param player player whose tribe or buildingDeck must be  modified
     * @param board current board
     * @param indexUpperChosenCards: these are indexes of the upperRow's CharacterCards chosen by the player
     * @param indexLowerChosenCards: these are indexes of the lowerRow's CharacterCards chosen by the player
     * @param indexUpperChosenBuildings: these are indexes of the upperRow's BuildingsCards chosen by the player
     * @param indexLowerChosenBuildings: these are indexes of the lowerRow's BuildingsCards chosen by the player
     * This method remove from board the cards chosen by the player on the OfferTile, and add these
     * in the player's tribe or player's buildings
     */
    public void playerMove(Player player, Board board, List<Integer> indexUpperChosenCards, List<Integer>
            indexLowerChosenCards, List<Integer> indexUpperChosenBuildings,
                           List<Integer> indexLowerChosenBuildings) throws InvalidPlayerActionException {
        int totalFoodToPay = 0;

        for (Integer i : indexUpperChosenBuildings) {
            BuildingCard card = board.getBuildingUpperRow().get(i);
            totalFoodToPay += card.getCost(player);
        }
        for (Integer i : indexLowerChosenBuildings) {
            BuildingCard card = board.getBuildingLowerRow().get(i);
            totalFoodToPay += card.getCost(player);
        }
        if (totalFoodToPay > player.getFood())
            throw new InvalidPlayerActionException("Player hasn't enough food to take this building");


        for (Integer i : indexUpperChosenCards) {
            if (!board.getUpperRow().get(i).isPickable())
                throw new InvalidPlayerActionException("Cannot pick an EventCard");
        }
        for (Integer i : indexLowerChosenCards) {
            if (!board.getLowerRow().get(i).isPickable())
                throw new InvalidPlayerActionException("Cannot pick an EventCard");
        }

        if (!indexUpperChosenBuildings.isEmpty()) {
            indexUpperChosenBuildings.sort(Collections.reverseOrder());
            for (Integer i : indexUpperChosenBuildings) {
                BuildingCard chosenCard = board.getBuildingUpperRow().get(i);
                int cost = chosenCard.getCost(player);
                board.removeFromBuildingUpperRow(chosenCard);
                player.addBuildingCard(chosenCard);
                player.payFood(cost);
            }
        }
        if (!indexLowerChosenBuildings.isEmpty()) {
            indexLowerChosenBuildings.sort(Collections.reverseOrder());
            for (Integer i : indexLowerChosenBuildings) {
                BuildingCard chosenCard = board.getBuildingLowerRow().get(i);
                int cost = chosenCard.getCost(player);
                board.removeFromBuildingLowerRow(chosenCard);
                player.addBuildingCard(chosenCard);
                player.payFood(cost);
            }
        }
        if (!indexUpperChosenCards.isEmpty()) {
            indexUpperChosenCards.sort(Collections.reverseOrder());
            for (Integer i : indexUpperChosenCards) {
                CharacterCard chosenCard = (CharacterCard) board.getUpperRow().get(i);
                board.removeFromUpperRow(chosenCard);
                player.addCharacterCard(chosenCard, board);
            }
        }
        if (!indexLowerChosenCards.isEmpty()) {
            indexLowerChosenCards.sort(Collections.reverseOrder());
            for (Integer i : indexLowerChosenCards) {
                CharacterCard chosenCard = (CharacterCard) board.getLowerRow().get(i);
                board.removeFromLowerRow(chosenCard);
                player.addCharacterCard(chosenCard, board);
            }
        }
    }
}
