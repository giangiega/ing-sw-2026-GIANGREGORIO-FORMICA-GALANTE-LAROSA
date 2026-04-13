package it.polimi.ingsw;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    //da testare anche con numPlayers 3,4,5
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
                "la riga sotto della board dovrebbe avere la size prevista dal config, e le carte devono essere tutte personaggio");
        assertEquals(config.getUpperRowSize(), board.getUpperRow().size(),
                "la riga sopra della board deve avere la size prevista dal config");
        assertFalse(board.getBuildingUpperRow().isEmpty());
        assertTrue(board.getBuildingDeckEra1().isEmpty());
    }

    //da testare anche per passaggio da era II a III
    @Test
    void testCheckEraSwitch(){
        List<BuildingCard> upperBefore = board.getBuildingUpperRow();

        board.checkEraSwitch(EraEnum.II);

        assertTrue(board.getBuildingLowerRow().containsAll(upperBefore));
        assertFalse(board.getBuildingUpperRow().isEmpty());
        assertTrue(board.getBuildingDeckEra2().isEmpty());
    }

    @Test
    void testRowsEndRound(){
        List<TribeCard> upperBefore = board.getUpperRow();

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
