package it.polimi.ingsw.network.serverInterface;

import it.polimi.ingsw.network.ViewInterface;

public class AckEvent implements ServerEvent{

    @Override
    public void updateView(ViewInterface view){
        view.showLoginScreen();
    }
}
