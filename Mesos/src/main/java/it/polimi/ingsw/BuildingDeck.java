package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * building deck of a specific era, decks must be shuffled in buildBuildingsDeck (CardFactory class)
 * @author Ale
 */
public class BuildingDeck {
    private EraEnum era;
    private List<BuildingCard> cards;

    /**
     * constructor
     * @param era
     */
    public BuildingDeck(EraEnum era){
        this.cards = new ArrayList<>();
        this.era = era;
    }

    /**
     * At the start of a new era all deck's cards will be placed face up
     * @throws EmptyDeckException
     * @return first deck card
     */
    public BuildingCard getFirstCard() throws EmptyDeckException{
        if(cards.isEmpty()) throw new EmptyDeckException("deck is empty");
        else return cards.get(0);
    }

    public boolean isEmpty(){
        return cards.isEmpty();
    }

    public int size(){
        return cards.size();
    }

    public EraEnum getEra(){
        return this.era;
    }
}
