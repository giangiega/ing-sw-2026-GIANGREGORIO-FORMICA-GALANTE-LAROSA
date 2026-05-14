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

    //Disconnection e Reconnection handling
    private boolean inResolvingPhase = false;
    private TurnOrderTile savedTurnOrderTile;
    private List<OfferTile> savedOfferTrack;

    public TurnController(GameController gameController, int numPlayers) {
        this.gameController = gameController;
        this.numPlayers = numPlayers;
    }

    public void startPlacementPhase(TurnOrderTile turnOrderTile) {
        inResolvingPhase = false;
        savedTurnOrderTile = turnOrderTile;

        totemPlacedCurrRound.clear();
        resolveOrder.clear();
        idx = 0;
        askNextTotemPlacement(turnOrderTile);
    }

    /**
     * Registers a placement.
     * If all players have placed their totem, it starts the resolve phase.
     * If not, the next player place his totem.
     * Disconnected players are skipped
     */
    public void onTotemPlaced(String playerName, TurnOrderTile turnOrderTile, List<OfferTile> offerTrack) {
        totemPlacedCurrRound.add(playerName);

        int connectedCount = gameController.getConnectedPlayersCount();

        if (totemPlacedCurrRound.size() >= connectedCount) {
            startResolvePhase(offerTrack);
        } else {
            askNextTotemPlacement(turnOrderTile);
        }
    }

    /**
     * Finds the next player who needs to place his totem.
     * Sends a MoveTotemEvent if the totem is not already moved in the current round.
     * Adding check to see if the player is still connected
     */
    public void askNextTotemPlacement(TurnOrderTile turnOrderTile) {
        savedTurnOrderTile = turnOrderTile;
        for (Player p : turnOrderTile.getSlots()) {
            if(p != null && !totemPlacedCurrRound.contains(p.getName())
                    && !gameController.isDisconnectedPlayer(p.getName()) ) {
                gameController.sendMoveTotem(p.getName());
                return;
            }
        }
        // At this point, all connected players have placed; So this can happen if the last placer
        // disconnected right after placing. Advance to resolve phase with the
        // most recent offer track we have; if we have none yet, end the round.
        if (savedOfferTrack != null) {
            startResolvePhase(savedOfferTrack);
        } else {
            gameController.endRound();
        }
    }

    /**
     * Determines the order in which players will act based on the position
     * of their totems on the OfferTrack.
     * Disconnected players are excluded
     */
    private void startResolvePhase(List<OfferTile> offerTrack) {
        inResolvingPhase = true;
        savedOfferTrack = offerTrack;

        resolveOrder.clear();
        idx = 0;
        for (OfferTile tile : offerTrack) {
            if (!tile.getFreeOfferTile() && tile.getOccupant() != null
                    && !gameController.isDisconnectedPlayer(tile.getOccupant().getName())) {
                resolveOrder.add(tile.getOccupant().getName());
            }
        }
        if (resolveOrder.isEmpty()) {
            gameController.endRound();
        } else {
            askNextAction();
        }
    }

    /**
     * Checks the index of players and calls che next action.
     */
    public void onActionResolved() {
        idx++;
        advanceResolvePhase();
    }
    /**
     * @author Giuse
     * @param playerName : player who skipped his turn
     * This method skips the player currently at idx in the resolve order because
     * they disconnected while it was their turn.  If the player is not the current
     * one this is a no-op, so callers don't need to guard against double-calls.
     */
    public void skipCurrentPlayer(String playerName) {
        if (idx < resolveOrder.size() && resolveOrder.get(idx).equals(playerName)) {
            advanceResolvePhase();
        }
    }
    /**
     * @author Giuse
     * Advances idx past any consecutive disconnected players and either
     * asks the next connected player to act or ends the round.
     */
    private void advanceResolvePhase() {
        // Skip any players that disconnected while waiting their turn
        while (idx < resolveOrder.size()
                && gameController.isDisconnectedPlayer(resolveOrder.get(idx))) {
            idx++;
        }
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

    /**
     * @author Giuse
     * @param playerName
     * This method is called by the GameController when a player drops
     * If it happens during the placement phase, if alla remaining connected
     * players have already placed, it forces the game to advance to the resolve phase
     * If it happens during the resolve phase, if it was the player's turn,
     * skip him
     */
    public void onPlayerDisconnected(String playerName) {
        if (!inResolvingPhase) {
            // Placement phase: check if all connected players have placed already
            int connectedCount = gameController.getConnectedPlayersCount();
            if (connectedCount > 0 && totemPlacedCurrRound.size() >= connectedCount) {
                startResolvePhase(savedOfferTrack != null ? savedOfferTrack : new ArrayList<>());
            }
            // else: the next askNextTotemPlacement call will skip the disconnected player
        } else {
            // Resolve phase: skip if it was their turn
            skipCurrentPlayer(playerName);
        }
    }

    /**
     * @author Giuse
     * Resends the appropriate information to the player currently expected to act.
     * Called by GameController after the game resumes from suspension.
     */
    public void resumeAfterSuspension() {
        if (!inResolvingPhase) {
            if (savedTurnOrderTile != null) {
                askNextTotemPlacement(savedTurnOrderTile);
            }
        } else {
            // Skip disconnected players and ask the next connected one
            advanceResolvePhase();
        }
    }

}
