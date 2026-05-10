package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.EraEnum;

/**
 * @author Daniele
 */

public abstract class Card {
    private EraEnum era;
    private String image;

    protected Card(EraEnum era) {
        this.era = era;
    }

    public EraEnum getEra() {
        return era;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }
}
