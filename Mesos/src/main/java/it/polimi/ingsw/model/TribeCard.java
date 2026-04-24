package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.EraEnum;

/**
 * @author Daniele
 */
public abstract class TribeCard extends Card {
    public TribeCard(EraEnum era) {
        super(era);
    }

    public boolean isPickable(){
        return true;
    }

    public boolean isEventCard() {
        return false;
    }

    public boolean isSustenance() {
        return false;
    }
}
