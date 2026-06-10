/**
 * @author Daniele
 */

package it.polimi.ingsw.model.cards.tribe;

import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.cards.Card;


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
