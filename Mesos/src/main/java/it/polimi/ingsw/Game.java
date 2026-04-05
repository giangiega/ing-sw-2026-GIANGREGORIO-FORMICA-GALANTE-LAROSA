package it.polimi.ingsw;

import java.util.Collections;
import java.util.List;

public class Game {
    public List<Player> players;
    public GameConfig config;
    public Board board;
    public int currentRound;

    public Game (List<Player> players, Board board, GameConfig config) {
        this.players = players;
        this.board = board;
        this.config = config;
        this.currentRound = 1;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }
    public Board getBoard() {
        return board;
    }
    public GameConfig getConfig() {
        return config;
    }
    public int getCurrentRound() {
        return currentRound;
    }

    //resolves events in the lowerRow and then calls board.rowsEndRound()
    public void endRound() {

    }
}
