package it.polimi.ingsw.model.decks;

import it.polimi.ingsw.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.cards.tribe.TribeCard;

import java.util.List;

/**
 * tribe deck
 * @author Ale
 */
public class Deck {
    private List<TribeCard> cards;

    public Deck(List<TribeCard> cards) {
        this.cards = cards;
    }

    /**
     * @throws EmptyDeckException if deck is empty, handled in the caller
     * @return first TribeCard of the deck
     * */
    public TribeCard getFirstCard() throws EmptyDeckException{
        if(cards.isEmpty()) throw new EmptyDeckException("deck is empty");
        else{
            return cards.removeFirst();
        }
    }

    /**
     * @return true if the deck is Empty, false otherwise
     */
    public boolean isEmpty(){
        return cards.isEmpty();
    }

    /**
     * @return deck size
     */
    public int size(){
        return cards.size();
    }
}
