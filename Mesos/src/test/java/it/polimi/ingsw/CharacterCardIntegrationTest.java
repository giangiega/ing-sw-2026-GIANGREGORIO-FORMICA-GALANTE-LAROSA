package it.polimi.ingsw;
/**
 * @author Giuse
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests: verify that different CharacterCard and EventCard
 * subclasses interact correctly
 */
class CharacterCardIntegrationTest {

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
    @Test//Checking interaction between Hunter and EventHunt
    void testHunters_thenEventHunt_correctFoodAndPP() {
        Player player = new Player("Riccardo", ColorEnum.BLUE);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);

        int foodBefore = player.getFood();
        int ppBefore = player.getPP();

        new EventHunt(EraEnum.I, false, 2).resolve(List.of(player), board);

        assertEquals(foodBefore + 3, player.getFood(),
                "The player should have received 3 food because he has 3 hunters");
        assertEquals(ppBefore + 6, player.getPP(),
                "The player should have received 6 prestige points, because he has 3 (*2)hunters");
    }
    @Test//Checking interaction between Artist and EventCavePainting if the player has enough artists
    void testArtists_thenEventCavePainting_gainsPP() {
        Player player = new Player("Giuseppe", ColorEnum.RED);
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        int ppBefore = player.getPP();

        new EventCavePainting(EraEnum.I, false, 2, 3, 2)
                .resolve(List.of(player), board);

        assertEquals(ppBefore + 3 * 3, player.getPP(),
                "The player should have got 9 prestige points because he has 3 (*3)artists");
    }

    @Test//Checking interaction between Artist and EventCavePainting if the player hasn't got enough artists
    void testArtists_belowMin_thenEventCavePainting_losesPP() {
        Player player = new Player("Alessandro", ColorEnum.RED);
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        int ppBefore = player.getPP();

        new EventCavePainting(EraEnum.I, false, 2, 3, 2)
                .resolve(List.of(player), board);

        assertEquals(ppBefore - 2, player.getPP(),
                "The player should have lost lostPP = 2 prestige point because he has only 1 artist and minArtist = 2");
    }
    @Test//Checking interaction between Shaman and EventShamanRitual
    void testShaman_thenEventShamanRitual() {
        Player winner = new Player("Winner", ColorEnum.WHITE);
        Player loser = new Player("Loser", ColorEnum.YELLOW );

        new Shaman(EraEnum.I, 2, 5).AddToPlayerTribe(winner, board);
        new Shaman(EraEnum.I, 2, 1).AddToPlayerTribe(loser, board);

        int ppWinnerBefore = winner.getPP();
        int ppLoserBefore = loser.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(winner, loser), board);

        assertEquals(ppWinnerBefore + 4, winner.getPP(),
                "The winner should have gained gainedPP = 4 prestige points");
        assertEquals(ppLoserBefore  - 2, loser.getPP(),
                "The loser should have lost lostPP = 2 prestige points");
    }
    @Test//Checking interaction between Gatherer and EventSustenance: a gatherer lowers the cost by 3 food
    void testGatherers_thenEventSustenance() {
        Player player = new Player("Riccardo", ColorEnum.RED);
        // 4 characters: 1 gatherer (discount 3) + 3 others → must pay max(0, 4-3) = 1
        new Gatherer(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        player.gainFood(5);
        int foodBefore = player.getFood();
        int ppBefore = player.getPP();

        new EventSustenance(EraEnum.I, false, 2).resolve(List.of(player), board);

        assertEquals(foodBefore - 1, player.getFood(),
                "The player should have lost only 1 food");
        assertEquals(ppBefore, player.getPP(),
                "The player should have lost no Prestige points");
    }
    @Test//Checking for inventor distinct icon accumulation
    void testInventors_mixedIcons_correctDistinctCount() {
        Player player = new Player("Giuseppe", ColorEnum.RED);
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        new Inventor(EraEnum.I, 2, IconEnum.BOAT).AddToPlayerTribe(player, board);

        assertEquals(3, player.getCharacterByType(CharacterEnum.INVENTOR).size(),
                "The player's Inventor list should contain 3 inventors");
        assertEquals(2, player.getDistinctInventorsIcon(),
                "The player should have 2 distinct inventors icon");
    }
    @Test//Checking to see if food discount accumulates with multiple builders
    void testBuilders_foodDiscountAccumulates() {
        Player player = new Player("Alessandro", ColorEnum.WHITE);
        int discountBefore = player.getTotalFoodDiscountBuilder();

        new Builder(EraEnum.I, 2, 2, 0).AddToPlayerTribe(player, board);
        new Builder(EraEnum.II, 3, 3, 0).AddToPlayerTribe(player, board);

        assertEquals(discountBefore + 5, player.getTotalFoodDiscountBuilder(),
                "The discount should have increased by 5");
    }
    @Test//Checking to see if a hunter with a hunt icon gives food
    void testHunter_withHuntIcon_givesImmediateFood() {
        Player player = new Player("Daniele", ColorEnum.WHITE);
        int foodBefore = player.getFood();

        new Hunter(EraEnum.I, 2, true).AddToPlayerTribe(player, board);

        assertEquals(foodBefore + 1, player.getFood(),
                "The player should have gained 1 food because his only added hunter has an icon");
    }

    @Test//Checking to see if a hunter with no hunt icon gives food
    void testHunter_withoutHuntIcon_givesNoImmediateFood() {
        Player player = new Player("Riccardo", ColorEnum.YELLOW);
        int foodBefore = player.getFood();

        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);

        assertEquals(foodBefore, player.getFood(),
                "The player should have received no food");
    }
}
