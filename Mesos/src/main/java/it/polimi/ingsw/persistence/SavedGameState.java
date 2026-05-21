package it.polimi.ingsw.persistence;

import it.polimi.ingsw.model.game.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Function persistence.
 * This class has everything that must be saved about the current game.
 */
public class SavedGameState {
    private final Game game;
    private final TurnControllerSnapshot turnSnapshot;
    private final List<String> disconnectedPlayers;

    public SavedGameState(Game game, TurnControllerSnapshot turnSnapshot,
                          List<String> disconnectedPlayers) {
        this.game = game;
        this.turnSnapshot = turnSnapshot;
        this.disconnectedPlayers = new ArrayList<>(disconnectedPlayers);
    }

    public Game getGame() {
        return game;
    }
    public TurnControllerSnapshot getTurnSnapshot() {
        return turnSnapshot;
    }
    public List<String> getDisconnectedPlayers() {
        return disconnectedPlayers;
    }
}
