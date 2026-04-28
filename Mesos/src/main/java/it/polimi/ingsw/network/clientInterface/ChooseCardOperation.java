package it.polimi.ingsw.network.clientInterface;

import it.polimi.ingsw.network.ClientManagerSocket;
import it.polimi.ingsw.network.Server;

import java.util.List;

public class ChooseCardOperation implements ClientOperation {
    private final List<Integer> upperCards;
    private final List<Integer>  lowerCards;
    private final List<Integer> upperBuildings;
    private final List<Integer> lowerBuildings;

    public ChooseCardOperation(List<Integer> upperCards, List<Integer> lowerCards, List<Integer> upperBuildings, List<Integer> lowerBuildings) {
        this.upperCards = upperCards;
        this.lowerCards = lowerCards;
        this.upperBuildings = upperBuildings;
        this.lowerBuildings = lowerBuildings;
    }


    @Override
    public void executeOp(Server server, ClientManagerSocket cm) {
        server.getGameController().resolveAction(cm.getPlayerName(),upperCards,lowerCards,upperBuildings,lowerBuildings);
    }
}
