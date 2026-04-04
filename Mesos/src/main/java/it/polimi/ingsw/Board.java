package it.polimi.ingsw;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
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
    private List<BuildingCard> buildingUpperRow;
    private List<BuildingCard> buildingLowerRow;
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
        this.buildingUpperRow = new ArrayList<>();
        this.buildingLowerRow = new ArrayList<>();

        this.currentEra = EraEnum.I;
        this.transitions = Map.of(
                EraEnum.II, new TransitionEraII(),
                EraEnum.III, new TransitionEraIII()
        );

        initializeRows();
    }
    // NOT IN UML
    private void initializeRows() {
        for (int i = 0; i < config.getUpperRowSize(); i++) {
            upperRow.add(tribeDeck.getFirstCard());
        }
        for (int i = 0; i < config.getLowerRowSize(); i++) {
            lowerRow.add(tribeDeck.getFirstCard());
        }
        while(!buildingDeckEra1.isEmpty()) {
            buildingUpperRow.add(buildingDeckEra1.getFirstCard());
        }
    }

    public List<TribeCard> getUpperRow() {
        return Collections.unmodifiableList(upperRow);
    }
    public List<TribeCard> getLowerRow() {
        return Collections.unmodifiableList(lowerRow);
    }
    public List<BuildingCard> getBuildingUpperRow() {
        return Collections.unmodifiableList(buildingUpperRow);
    }
    public List<BuildingCard> getBuildingLowerRow() {
        return Collections.unmodifiableList(buildingLowerRow);
    }
    public List<OfferTile> getOfferTrack() {
        return Collections.unmodifiableList(offerTrack);
    }
    // Game needs this method
    public TurnOrderTile getTurnOrderTile() {
        return turnOrderTile;
    }
    public EraEnum getCurrentEra() {
        return currentEra;
    }

    public void removeFromUpperRow(TribeCard card) {
        upperRow.remove(card);
    }
    public void removeFromLowerRow(TribeCard card) {
        lowerRow.remove(card);
    }

    // rowsEndTurn needs this method to check every card era.
    public boolean checkEraSwitch(EraEnum newEra) {
      if (newEra != currentEra) {
          transitions.get(newEra).applyTransition(this);
          currentEra = newEra;
          return true;
      } else return false;
    }

    // Game is the event resolver, so it needs another method endRound() , Board doesn't care about
    public void rowsEndRound() {
        // 1 --> clear lowerRow
        lowerRow.clear();
        // 2 --> move upperRow to lowerRow
        lowerRow.addAll(upperRow);
        upperRow.clear();
        // 3 --> fill upperRow with new cards
        for (int i = 0; i < config.getUpperRowSize(); i++) {
            if (!tribeDeck.isEmpty()) {
                checkEraSwitch(tribeDeck.getFirstCard().getEra());
                upperRow.add(tribeDeck.getFirstCard());
            }
        }
    }

    // define helper methods for letting TransitionEra know infos about the board
}
