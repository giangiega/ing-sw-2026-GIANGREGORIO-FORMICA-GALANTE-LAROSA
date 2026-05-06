package it.polimi.ingsw.model.boardAndTiles;

import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.buildings.BuildingDeck;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.model.decks.Deck;
import it.polimi.ingsw.model.eraLogic.EraTransition;
import it.polimi.ingsw.model.eraLogic.TransitionEraII;
import it.polimi.ingsw.model.eraLogic.TransitionEraIII;
import it.polimi.ingsw.model.game.GameConfig;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Ric
 * Board is the core class besides Game.
 * It manages the initialization of the rows and their updates.
 * It also manages the era transition.
 */
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
    private Map<EraEnum, EraTransition> transitions;

    public Board(GameConfig config, Deck tribeDeck,
                 BuildingDeck buildingDeckEra1, BuildingDeck buildingDeckEra2,
                 BuildingDeck buildingDeckEra3) {
        this.config = config;
        this.tribeDeck = tribeDeck;
        this.buildingDeckEra1 = buildingDeckEra1;
        this.buildingDeckEra2 = buildingDeckEra2;
        this.buildingDeckEra3 = buildingDeckEra3;

        this.turnOrderTile = new TurnOrderTile(
                config.getNumPlayers(),
                config.getFoodBonuses()
        );
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
        for (int counter = 0; counter < config.getLowerRowSize(); ) {
            TribeCard c = tribeDeck.getFirstCard();
            if (c.isEventCard()) {
                upperRow.add(c);
            }
            else {
                lowerRow.add(c);
                counter++;
            }
        }
        int remaining = config.getUpperRowSize() - upperRow.size();
        for (int i = 0; i < remaining; i++)
            upperRow.add(tribeDeck.getFirstCard());

        while (!buildingDeckEra1.isEmpty())
            buildingUpperRow.add(buildingDeckEra1.getFirstCard());
    }

    public BuildingDeck getBuildingDeckEra1() {
        return buildingDeckEra1;
    }
    public BuildingDeck getBuildingDeckEra2() {
        return buildingDeckEra2;
    }
    public BuildingDeck getBuildingDeckEra3() {
        return buildingDeckEra3;
    }
    // returns an unmodifiable structure, so who calls the method can only read and not modify.
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

    public void removeFromBuildingUpperRow(BuildingCard card) {
        buildingUpperRow.remove(card);
    }

    public void removeFromBuildingLowerRow(BuildingCard card) {
        buildingLowerRow.remove(card);
    }

    /**
     * called by rowsEndRound, every time I draw a card for the rows.
     * If the card's era attribute is a new era, applyTransition is called.
     * applyTransition() uses the last 3 helper methods defined on Board.
     * @param newEra
     */
    public void checkEraSwitch(EraEnum newEra) {
      if (newEra != currentEra) {
          transitions.get(newEra).applyTransition(this);
          currentEra = newEra;
      }
    }

    /**
     * called by Game inside method endRound()
     * 1 --> clear lowerRow
     * 2 --> move upperRow to lowerRow
     * 3 --> fill upperRow with new cards
     */
    public void rowsEndRound() {
        lowerRow.clear();

        lowerRow.addAll(upperRow);
        upperRow.clear();

        for (int i = 0; i < config.getUpperRowSize(); i++) {
            if (!tribeDeck.isEmpty()) {
                TribeCard card = tribeDeck.getFirstCard();
                checkEraSwitch(card.getEra());
                upperRow.add(card);
            }
        }
    }


    /**
     * I have to define helper methods for letting TransitionEra know infos about the board
     * applyTransition has board as a parameter. This 3 methods are called by TransitionEra
     * this first method is only used by TransitionEraIII
     */
    public void discardLowerRowBuildings() {
        buildingLowerRow.clear();
    }

    public void moveBuildingsToLowerRow() {
        buildingLowerRow.addAll(buildingUpperRow);
        buildingUpperRow.clear();
    }

    public void fillBuildingUpperRow(BuildingDeck deck) {
        while(!deck.isEmpty()) {
            buildingUpperRow.add(deck.getFirstCard());
        }
    }
}
