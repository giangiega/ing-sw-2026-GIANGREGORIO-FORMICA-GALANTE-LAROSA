package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.userInterface.ViewInterface;

public class UpdateRoundEvent implements ServerEvent {
    private final int currentRound;

    public UpdateRoundEvent(int currRound) {
        this.currentRound = currRound;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.updateRound(currentRound);
    }
}
