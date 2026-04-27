package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.GameConfig;
import it.polimi.ingsw.model.OfferTile;
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
    private final List<String> resolveOrder = new ArrayList<>();
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

    public void onTotemPlaced(String playerName, TurnOrderTile turnOrderTile, List<OfferTile> offerTrack) {
        totemPlacedCurrRound.add(playerName);
        if (totemPlacedCurrRound.size() == numPlayers) {
            startResolvePhase(offerTrack);
        } else {
            askNextTotemPlacement(turnOrderTile);
        }
    }

    public void askNextTotemPlacement(TurnOrderTile turnOrderTile) {
        for (Player p : turnOrderTile.getSlots()) {
            if(p != null && !totemPlacedCurrRound.contains(p.getName())) {
                gameController.sendMoveTotem(p.getName());
            }
        }
    }

    private void startResolvePhase(List<OfferTile> offerTrack) {
        resolveOrder.clear();
        idx = 0;
        for (OfferTile tile : offerTrack) {
            if (!tile.getFreeOfferTile() && tile.getOccupant() != null) {
                resolveOrder.add(tile.getOccupant().getName());
            }
        }
        askNextAction();
    }

    private void askNextAction() {
        String nextPlayerName = resolveOrder.get(idx);
        gameController.sendIsYourTurn(nextPlayerName);
    }

}
