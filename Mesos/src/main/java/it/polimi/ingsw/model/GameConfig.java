package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.EraEnum;

import java.util.List;
import java.util.Map;

/**
 * @ author Ric
 * abstract class with override methods for each game configuration, based on the number of players
 */
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

    /**
     * @ param pos
     * return the initial amount of food for each player based on the position in the turn order
     */
    public int getInitialFood(int pos) {
        if(pos == 1) return 2;
        else if(pos == 2 || pos == 3) return 3;
        else if(pos < 1 || pos > 5) throw new IllegalArgumentException("ERROR");
        else return 4;
    }

    public abstract int getUpperRowSize();
    public abstract int getLowerRowSize();
    public abstract Map<EraEnum,Integer> getBuildingCardsPerEra();
    public abstract List<OfferTile> getOfferTiles();
    public abstract int getNumPlayers();
    public abstract int[] getFoodBonuses();

}
