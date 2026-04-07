package it.polimi.ingsw;

import java.util.Collections;
import java.util.List;

/**
 * OfferTile class
 * @author Ale
 */
public class OfferTile {
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

    public void setOccupant(Player occupant) {
        this.occupant = occupant;
        this.isFree = false;
    }

    /**
     * this method remove from board the cards chosen by the player on the OfferTile, and add these in the player's tribe or player's buildings
     * @param player
     * @param board
     * @param indexUpperChosenCards: these are indexes of the upperRow's CharacterCards chosen by the player
     * @param indexLowerChosenCards: these are indexes of the lowerRow's CharacterCards chosen by the player
     * @param indexUpperChosenBuildings: these are indexes of the upperRow's BuildingsCards chosen by the player
     * @param indexLowerChosenBuildings: these are indexes of the lowerRow's BuildingsCards chosen by the player
     */
    public void playerMove(Player player, Board board, List<Integer> indexUpperChosenCards, List<Integer> indexLowerChosenCards, List<Integer> indexUpperChosenBuildings, List<Integer> indexLowerChosenBuildings) throws InvalidPlayerActionException{
        if(!indexUpperChosenCards.isEmpty()) {
            //I have to sort the list in descending order because when I remove an element the others shift to the left, in this way the other indices remain valid
            indexUpperChosenCards.sort(Collections.reverseOrder());
            for (Integer i : indexUpperChosenCards) {
                if (board.getUpperRow().get(i) instanceof EventCard)
                    throw new InvalidPlayerActionException("Player can't choose an EventCard");
                else if (board.getUpperRow().get(i) instanceof CharacterCard) {
                    CharacterCard chosenCard = (CharacterCard) board.getUpperRow().get(i);
                    board.removeFromUpperRow((TribeCard) chosenCard);
                    player.addCharacterCard(chosenCard, board);
                }
            }
        }
        if(!indexLowerChosenCards.isEmpty()) {
            //I have to sort the list in descending order because when I remove an element the others shift to the left, in this way the other indices remain valid
            indexLowerChosenCards.sort(Collections.reverseOrder());
            for (Integer i : indexLowerChosenCards) {
                if (board.getLowerRow().get(i) instanceof EventCard)
                    throw new InvalidPlayerActionException("Player can't choose an EventCard");
                else if (board.getLowerRow().get(i) instanceof CharacterCard) {
                    CharacterCard chosenCard = (CharacterCard) board.getLowerRow().get(i);
                    board.removeFromLowerRow((TribeCard) chosenCard);
                    player.addCharacterCard(chosenCard, board);
                }
            }
        }
        if(!indexUpperChosenBuildings.isEmpty()) {
            //I have to sort the list in descending order because when I remove an element the others shift to the left, in this way the other indices remain valid
            indexUpperChosenBuildings.sort(Collections.reverseOrder());
            for (Integer i : indexUpperChosenBuildings) {
                BuildingCard chosenCard = board.getBuildingUpperRow().get(i);
                board.removeFromBuildingUpperRow(chosenCard);
                player.addBuildingCard(chosenCard);
            }
        }
        if(!indexLowerChosenBuildings.isEmpty()) {
            //I have to sort the list in descending order because when I remove an element the others shift to the left, in this way the other indices remain valid
            indexLowerChosenBuildings.sort(Collections.reverseOrder());
            for (Integer i : indexLowerChosenBuildings) {
                BuildingCard chosenCard = board.getBuildingLowerRow().get(i);
                board.removeFromBuildingLowerRow(chosenCard);
                player.addBuildingCard(chosenCard);
            }
        }
    }

    public boolean getFreeOfferTile(){
        return isFree;
    }

    public void setFreeOfferTile(boolean b){
        this.isFree = b;
    }
}
