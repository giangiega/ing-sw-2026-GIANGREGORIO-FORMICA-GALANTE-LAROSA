package it.polimi.ingsw;

public class Builder extends CharacterCard {
    private int wingCount;
    private int endGamePP;

    public Builder(EraEnum era, int wingCount, int endGamePP) {
        super(era);
        this.wingCount = wingCount;
        this.endGamePP = endGamePP;
    }

    public int getWingCount() {
        return wingCount;
    }
    public int getEndGamePP() {
        return endGamePP;
    }
}
