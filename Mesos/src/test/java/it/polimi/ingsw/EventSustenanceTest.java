package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventSustenanceTest {

    private final CardFactory cf = new CardFactory();
    private Board board;
    private GameConfig config;

    @BeforeEach
    void setUp() {
        config = GameConfig.create(2);
        board = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
    }
    @Test//Checking to see if the event has the right properties: ppPerUnfedCharacter
    void testGetPpPerUnfedCharacter() {
        assertEquals(2, new EventSustenance(EraEnum.I, false, 2).getPpPerUnfedCharacter(),
                "getPpPerUnfedCharacter() should return the correct value");
    }
    @Test//Checking to see if the class resolves the event in the right way if the player has enough food but no gatherers
    void testResolve_enoughFood_noGatherers() {
        Player player = new Player("Riccardo", ColorEnum.BLUE);
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        player.gainFood(5);
        int foodBefore = player.getFood();
        int ppBefore = player.getPP();

        new EventSustenance(EraEnum.I, false, 2).resolve(List.of(player), board);

        assertEquals(foodBefore - 2, player.getFood(),
                "The player should have paid 2 food because he has 2 characters in his tribe");
        assertEquals(ppBefore, player.getPP(),
                "The player should have lost no PP because he has enough food");
    }
    @Test//Checking to see if the class resolves the event in the right way if the player has enough food and one gatherer
    void testResolve_oneGatherer() {
        Player player = new Player("Giuseppe", ColorEnum.BLUE);
        new Gatherer(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        player.gainFood(5);
        int foodBefore = player.getFood();
        int ppBefore = player.getPP();

        new EventSustenance(EraEnum.I, false, 2).resolve(List.of(player), board);

        assertEquals(foodBefore - 1, player.getFood(),
                "The player should have paid 1 food because he has 1(+3) gatherer and 4(-4) characters in his tribe");
        assertEquals(ppBefore, player.getPP(),
                "The player should have lost no PP because he has enough food");
    }
    @Test//Checking to see if the class resolves the event in the right way if the discount is enough to pay everything
    void testResolve_paysNothing() {
        Player player = new Player("Alessandro", ColorEnum.PURPLE);
        new Gatherer(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        int foodBefore = player.getFood();
        int ppBefore = player.getPP();

        new EventSustenance(EraEnum.I, false, 2).resolve(List.of(player), board);

        assertEquals(foodBefore, player.getFood(),
                "The player should have paid no food because he has 1(+3) gatherer and 3(-3) characters in his tribe");
        assertEquals(ppBefore, player.getPP(),
                "The player should have lost no PP because he has enough food");
    }
    @Test//Checking to see if the class resolves the event in the right way if the player hasn't got enough food
    void testResolve_notEnoughFood_paysAllAndLosesPP() {
        Player player = new Player("Daniele", ColorEnum.WHITE);
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        player.gainFood(1);
        int ppBefore = player.getPP();

        new EventSustenance(EraEnum.I, false, 2).resolve(List.of(player), board);

        assertEquals(0, player.getFood(),
                "The player should have 0 food left");
        assertEquals(ppBefore - 6, player.getPP(),
                "The player should have lost 6 prestige points because he has 3 (*2)unfed characters");
    }

    @Test//Checking to see if the class resolves the event in the right way if the player has no food nor characters
    void testResolve_zeroFood_zeroCharacters() {
        Player player = new Player("Riccardo", ColorEnum.YELLOW);
        int ppBefore = player.getPP();

        new EventSustenance(EraEnum.I, false, 2).resolve(List.of(player), board);

        assertEquals(0, player.getFood(),
                "The player should have 0 food left");
        assertEquals(ppBefore, player.getPP(),
                "The player should have lost no PP because he didn't have to pay anything");
    }
    @Test//Checking to see if the class resolves the event in the right way with multiple players
    void testResolve_multiplePlayers() {
        Player rich = new Player("Rich", ColorEnum.BLUE);
        Player poor = new Player("Poor", ColorEnum.RED);

        new Artist(EraEnum.I, 2).AddToPlayerTribe(rich, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(rich, board);
        rich.gainFood(10);

        new Artist(EraEnum.I, 2).AddToPlayerTribe(poor, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(poor, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(poor, board);

        int richFoodBefore = rich.getFood();
        int poorPPBefore = poor.getPP();

        new EventSustenance(EraEnum.I, false, 2).resolve(List.of(rich, poor), board);

        assertEquals(richFoodBefore - 2, rich.getFood(),
                "Rich player is okay");
        assertEquals(poorPPBefore  - 6,  poor.getPP(),
                "Poor player should have lost 6 prestige points, because he has 3 (*2)unfed characters");     }
}