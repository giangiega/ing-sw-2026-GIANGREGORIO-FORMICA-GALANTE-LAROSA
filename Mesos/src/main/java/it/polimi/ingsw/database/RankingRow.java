package it.polimi.ingsw.database;

import java.io.Serializable;

public class RankingRow implements Serializable{
    private final int position;
    private final String nickname;
    private final int totalWins;
    private final int totalScore;
    private final int numPlayers;

    public RankingRow(int position, String nickname, int totalWin, int totalScore,
                        int numPlayers) {
        this.position = position;
        this.nickname = nickname;
        this.totalWins = totalWin;
        this.totalScore = totalScore;
        this.numPlayers = numPlayers;
    }

    public int getPosition(){
        return this.position;
    }

    public String getNickname(){
        return this.nickname;
    }

    public int getTotalWin(){
        return this.totalWins;
    }

    public int getScore(){
        return this.totalScore;
    }

    public int getNumPlayers(){
        return this.numPlayers;
    }

    @Override
    public String toString() {
        return String.format("#%-3d %-15s %4d wins %4d PP  (%s)",
                position, nickname, totalWins, totalScore);
    }
}
