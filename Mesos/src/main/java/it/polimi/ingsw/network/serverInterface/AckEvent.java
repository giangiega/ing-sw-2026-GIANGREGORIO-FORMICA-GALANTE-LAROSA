package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.userInterface.ViewInterface;

public class AckEvent implements ServerEvent{
    private final boolean isFirst;

    public AckEvent(boolean isFirst) {
        this.isFirst = isFirst;
    }

    @Override
    public void updateView(ViewInterface view){
        if(isFirst)
            view.askNumPlayers();
        view.askLogin();
    }
}
