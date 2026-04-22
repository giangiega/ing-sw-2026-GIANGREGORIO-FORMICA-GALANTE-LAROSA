package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.EraEnum;

import java.util.List;
import java.util.Map;

public class GameConfig2 extends GameConfig {

    @Override
    public int getUpperRowSize() {
        return 6;
    }

    @Override
    public int getLowerRowSize() {
        return 3;
    }

    @Override
    public Map<EraEnum,Integer> getBuildingCardsPerEra() {
        return Map.of(EraEnum.I,1, EraEnum.II,2, EraEnum.III,3);
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
        return 2;
    }

    //the last player lose 1 food or 2 PP, already managed in unplaceTotem(), with isLastSlot()
    @Override
    public int[] getFoodBonuses() {
        return new int[]{1 , 0};
    }
}
