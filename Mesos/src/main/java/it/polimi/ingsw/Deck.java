package it.polimi.ingsw;

import java.util.List;

/**
 * tribe deck, addToBottom and shuffle methods must be implemented in CardFactory because are used to build the complete Deck
 * @author Ale
 */
public class Deck {
    private List<TribeCard> cards;

    public Deck(List<TribeCard> cards) {
        this.cards = cards;
    }

    /**
     *
     * @throws EmptyDeckException if deck is empty, handled in the caller
     * @return first TribeCard of the deck
     * */
    public TribeCard getFirstCard() throws EmptyDeckException{
        if(cards.isEmpty()) throw new EmptyDeckException("deck is empty");
        else{
            TribeCard card = cards.getFirst();
            cards.removeFirst();
            return card;
        }
    }

    /**
     *
     * @return true if the deck is Empty, false otherwise
     */
    public boolean isEmpty(){
        return cards.isEmpty();
    }

    /**
     *
     * @return deck size
     */
    public int size(){
        return cards.size();
    }
}
