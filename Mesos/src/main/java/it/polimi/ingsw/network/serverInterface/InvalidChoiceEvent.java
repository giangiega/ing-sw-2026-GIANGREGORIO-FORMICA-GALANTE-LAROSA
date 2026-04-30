package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.userInterface.ViewInterface;

public class InvalidChoiceEvent implements ServerEvent {
    private final String message;

    public InvalidChoiceEvent(String message) {
        this.message = message;
    }

    @Override
    public void updateView(ViewInterface view) {
        view.invalidChoice(message);
    }
}
