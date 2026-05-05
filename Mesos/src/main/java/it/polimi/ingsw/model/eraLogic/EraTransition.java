package it.polimi.ingsw.model.eraLogic;

import it.polimi.ingsw.model.boardAndTiles.Board;

public abstract class EraTransition {

    /**
     * This method is called only by checkEraSwitch, that checks every card, and if needed, does the
     * transition to the next era through applyTransition() , chosen a runtime for era II or era III.
     */
    public abstract void applyTransition(Board board);
}
