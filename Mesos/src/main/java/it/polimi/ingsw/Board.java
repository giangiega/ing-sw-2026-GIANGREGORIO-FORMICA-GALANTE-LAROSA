package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Board {
    private final GameConfig config;
    private final Deck tribeDeck;
    private final BuildingDeck buildingDeckEra1;
    private final BuildingDeck buildingDeckEra2;
    private final BuildingDeck buildingDeckEra3;
    private final TurnOrderTile turnOrderTile;
    private List<OfferTile> offerTrack;
    private List<TribeCard> upperRow;
    private List<TribeCard> lowerRow;
    private List<BuildingCard> BuildingUpperRow;
    private List<BuildingCard> BuildingLowerRow;
    private EraEnum currentEra;
    private Map<EraEnum,EraTransition> transitions;

    public Board(GameConfig config, Deck tribeDeck,
                 BuildingDeck buildingDeckEra1, BuildingDeck buildingDeckEra2,
                 BuildingDeck buildingDeckEra3) {
        this.config = config;
        this.tribeDeck = tribeDeck;
        this.buildingDeckEra1 = buildingDeckEra1;
        this.buildingDeckEra2 = buildingDeckEra2;
        this.buildingDeckEra3 = buildingDeckEra3;

        this.turnOrderTile = new TurnOrderTile();
        this.offerTrack = config.getOfferTiles();
        this.upperRow = new ArrayList<>();
        this.lowerRow = new ArrayList<>();
        this.BuildingUpperRow = new ArrayList<>();
        this.BuildingLowerRow = new ArrayList<>();

        this.currentEra = EraEnum.I;
        this.transitions = Map.of(
                EraEnum.II, new TransitionEraII(),
                EraEnum.III, new TransitionEraIII()
        );

        initializeRows();
    }

    // NOT IN UML
    private void initializeRows() {

    }

    // define helper methods for letting TransitionEra know infos about the board

}
