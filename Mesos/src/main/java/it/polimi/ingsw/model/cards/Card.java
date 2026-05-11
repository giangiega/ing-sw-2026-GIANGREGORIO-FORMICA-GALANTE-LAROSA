package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.enums.EraEnum;

import java.io.Serializable;

/**
 * @author Daniele
 */

public abstract class Card implements Serializable {
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
