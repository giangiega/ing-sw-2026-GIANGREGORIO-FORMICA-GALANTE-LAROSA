package it.polimi.ingsw;

import java.util.List;
import java.util.Map;

public class GameConfig3 extends GameConfig {

    @Override
    public int getUpperRowSize() {
        return 7;
    }

    @Override
    public int getLowerRowSize() {
        return 4;
    }

    @Override
    public Map<EraEnum,Integer> getBuildingCardsPerEra() {
        return Map.of(EraEnum.I,2, EraEnum.II,2, EraEnum.III,4);
    }

    @Override
    public List<OfferTile> getOfferTiles() {
        return  List.of(
                new OfferTile('B' , 0 , 1),
                new OfferTile('C' , 1 , 0),
                new OfferTile('D' , 0 , 2),
                new OfferTile('E' , 1 , 1),
                new OfferTile('F' , 2 , 0)
        );
    }

    @Override
    public int getNumPlayers() {
        return 3;
    }
}

