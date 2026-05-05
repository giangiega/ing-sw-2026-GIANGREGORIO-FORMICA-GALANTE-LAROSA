package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.EraEnum;

/**
 * @author Daniele
 */

public abstract class Card {
    private EraEnum era;

    protected Card(EraEnum era) {
        this.era = era;
    }

    public EraEnum getEra() {
        return era;
    }
}
