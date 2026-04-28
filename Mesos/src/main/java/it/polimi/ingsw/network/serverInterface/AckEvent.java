package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.userInterface.ViewInterface;

public class AckEvent implements ServerEvent{

    @Override
    public void updateView(ViewInterface view){
        view.showLoginScreen();
    }
}
