package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.GameConfig;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TurnOrderTile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TurnController {
    private final GameController gameController;
    private final int numPlayers;
    private final Set<String> totemPlacedCurrRound = new HashSet<>();
    private final List<Player> resolveOrder = new ArrayList<>();
    private int idx;

    public TurnController(GameController gameController, int numPlayers) {
        this.gameController = gameController;
        this.numPlayers = numPlayers;
    }

    public void startPlacementPhase(TurnOrderTile turnOrderTile) {
        totemPlacedCurrRound.clear();
        resolveOrder.clear();
        idx = 0;
        askNextTotemPlacement(turnOrderTile);
    }


    public void askNextTotemPlacement(TurnOrderTile turnOrderTile) {
        for (Player p : turnOrderTile.getSlots()) {
            if(p != null && !totemPlacedCurrRound.contains(p.getName())) {
                //gameController.sendMoveTotem(p.getName());
            }
        }
    }

}
