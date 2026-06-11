package it.polimi.ingsw.model;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.buildings.BuildingDeck;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.decks.Deck;
import it.polimi.ingsw.model.game.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest{
    private CardFactory cf = new CardFactory();
    private Board board;
    private GameConfig config;
    private Deck tribeDeck;
    private BuildingDeck buildingDeckEra1;
    private BuildingDeck buildingDeckEra2;
    private BuildingDeck buildingDeckEra3;


    @BeforeEach
    void setUp(){
        config = GameConfig.create(2);
        tribeDeck = cf.buildTribeDeck(config);
        buildingDeckEra1 = cf.buildBuildingDeck(EraEnum.I, config);
        buildingDeckEra2 = cf.buildBuildingDeck(EraEnum.II, config);
        buildingDeckEra3 = cf.buildBuildingDeck(EraEnum.III, config);
        board = new Board(config, tribeDeck, buildingDeckEra1, buildingDeckEra2, buildingDeckEra3);
    }

    @Test
    void testInitializeRows(){
        int numCharacterLowerRow = 0;
        for(int i = 0; i < board.getLowerRow().size(); i++){
            if(board.getLowerRow().get(i) instanceof CharacterCard) numCharacterLowerRow++;
        }
        assertEquals(config.getLowerRowSize(), numCharacterLowerRow,
                "Lower row should have the correct size based on the config, and should be made of character cards");
        assertEquals(config.getUpperRowSize(), board.getUpperRow().size(),
                "Upper row board should have the correct size based on the config");
        assertFalse(board.getBuildingUpperRow().isEmpty());
        assertTrue(board.getBuildingDeckEra1().isEmpty());
    }


    @Test
    void testCheckEraSwitch(){
        List<BuildingCard> upperBefore = new ArrayList<>(board.getBuildingUpperRow());

        board.checkEraSwitch(EraEnum.II);

        assertTrue(board.getBuildingLowerRow().containsAll(upperBefore)); //
        assertFalse(board.getBuildingUpperRow().isEmpty());
        assertTrue(board.getBuildingDeckEra2().isEmpty());
    }

    @Test
    void testRowsEndRound(){
        List<TribeCard> upperBefore = new ArrayList<>(board.getUpperRow());

        board.rowsEndRound();

        assertTrue(board.getLowerRow().containsAll(upperBefore));
        assertEquals(config.getUpperRowSize(), board.getUpperRow().size());
    }

    @Test
    void testMoveBuildingsToLowerRow(){
        List<BuildingCard> upperBefore = board.getBuildingUpperRow();

        board.moveBuildingsToLowerRow();

        assertTrue(board.getBuildingLowerRow().containsAll(upperBefore));
        assertTrue(board.getBuildingUpperRow().isEmpty());
    }

    @Test
    void testFillBuildingUpperRow(){
        board.fillBuildingUpperRow(board.getBuildingDeckEra2());

        assertTrue(board.getBuildingDeckEra2().isEmpty());
        assertFalse(board.getBuildingUpperRow().isEmpty());
    }
}
