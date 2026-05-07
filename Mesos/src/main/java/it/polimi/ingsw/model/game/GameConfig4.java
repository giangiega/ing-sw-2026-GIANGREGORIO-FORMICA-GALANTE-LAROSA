package it.polimi.ingsw.model.game;

import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;

import java.util.List;
import java.util.Map;

public class GameConfig4 extends GameConfig {

    @Override
    public int getUpperRowSize() {
        return 8;
    }

    @Override
    public int getLowerRowSize() {
        return 5;
    }

    @Override
    public Map<EraEnum,Integer> getBuildingCardsPerEra() {
        return Map.of(EraEnum.I,2, EraEnum.II,3, EraEnum.III,4);
    }

    @Override
    public List<OfferTile> getOfferTiles() {
        return  List.of(
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
        return 4;
    }

    //the last player lose 1 food or 2 PP, already managed in unplaceTotem(), with isLastSlot()
    @Override
    public int[] getFoodBonuses() {
        return new int[]{2 , 1 , 0 , 0};
    }
}