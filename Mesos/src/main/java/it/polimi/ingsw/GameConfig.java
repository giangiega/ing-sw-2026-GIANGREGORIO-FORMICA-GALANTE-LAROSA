package it.polimi.ingsw;

import java.util.List;
import java.util.Map;

public abstract class GameConfig {

    public static GameConfig create(int numPlayers) {
        return switch(numPlayers) {
            case 2 -> new GameConfig2();
            case 3 -> new GameConfig3();
            case 4 -> new GameConfig4();
            case 5 -> new GameConfig5();
            default -> throw new IllegalArgumentException("ERROR");
        };  // can it print error?
    }

    public int getInitialFood(int pos) {
        if(pos == 1) return 2;
        else if(pos == 2 || pos == 3) return 3;
        else if(pos < 1 || pos > 5) throw new IllegalArgumentException("ERROR");
        else return 4;
    }

    public abstract int getUpperRowSize();
    public abstract int getLowerRowSize();
    public abstract Map<EraEnum,Integer> getBuildingCardsPerEra();
    public abstract List<OfferTile> getOfferTile();
    public abstract int getNumPlayers();

}
