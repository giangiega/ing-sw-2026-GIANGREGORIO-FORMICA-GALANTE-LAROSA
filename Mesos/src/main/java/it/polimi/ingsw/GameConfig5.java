package it.polimi.ingsw;

import java.util.List;
import java.util.Map;

public class GameConfig5 extends GameConfig {
    @Override
    public int getUpperRowSize() {
        return 9;
    }

    @Override
    public int getLowerRowSize() {
        return 6;
    }

    @Override
    public Map<EraEnum,Integer> getBuildingCardsPerEra() {
        return Map.of(EraEnum.I,2, EraEnum.II,3, EraEnum.III,5);
    }

    @Override
    public List<OfferTile> getOfferTiles() {
        return  List.of(
                new OfferTile('A' , 0 , 0),
                new OfferTile('B' , 0 , 1),
                new OfferTile('C' , 1 , 0),
                new OfferTile('D' , 0 , 2),
                new OfferTile('E' , 1 , 1),
                new OfferTile('F' , 2 , 0),
                new OfferTile('G' , 1 , 2)
        );
    }

    @Override
    public int getNumPlayers() {
        return 5;
    }

    //the last player lose 1 food or 2 PP, already managed in unplaceTotem(), with isLastSlot()
    @Override
    public int[] getFoodBonuses() {
        return new int[]{3 , 1 , 0 , 0 , 0};
    }
}

