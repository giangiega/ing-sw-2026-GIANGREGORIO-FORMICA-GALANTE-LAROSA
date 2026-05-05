package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;

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

    /**
     * Registers a placement.
     * If all players have placed their totem, it starts the resolve phase.
     * If not, the next player place his totem.
     */
    public void onTotemPlaced(String playerName, TurnOrderTile turnOrderTile, List<OfferTile> offerTrack) {
        totemPlacedCurrRound.add(playerName);
        if (totemPlacedCurrRound.size() == numPlayers) {
            startResolvePhase(offerTrack);
        } else {
            askNextTotemPlacement(turnOrderTile);
        }
    }

    /**
     * Finds the next player who needs to place his totem.
     * Sends a MoveTotemEvent if the totem is not already moved in the current round.
     */
    public void askNextTotemPlacement(TurnOrderTile turnOrderTile) {
        for (Player p : turnOrderTile.getSlots()) {
            if(p != null && !totemPlacedCurrRound.contains(p.getName())) {
                gameController.sendMoveTotem(p.getName());
                return;
            }
        }
    }

    /**
     * Determines the order in which players will act based on the position
     * of their totems on the OfferTrack.
     */
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

    /**
     * Checks the index of players and calls che next action.
     */
    public void onActionResolved() {
        idx++;
        if (idx >= resolveOrder.size()) {
            gameController.endRound();
        } else {
            askNextAction();
        }
    }

    private void askNextAction() {
        String nextPlayerName = resolveOrder.get(idx);
        gameController.sendIsYourTurn(nextPlayerName);
    }

    public String getCurrentResolvingPlayer() {
        if (idx < resolveOrder.size())
            return resolveOrder.get(idx);
        return null;
    }

}
