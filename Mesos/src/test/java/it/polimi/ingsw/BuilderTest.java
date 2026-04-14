package it.polimi.ingsw;
/**
 * @author Giuse
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class BuilderTest {
    private final CardFactory cf = new CardFactory();
    private Board board;
    private Player player;
    private GameConfig config;

    @BeforeEach
    void setUp() {
        config = GameConfig.create(2);
        board = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("TestPlayer", ColorEnum.PURPLE);
    }

    @Test//Checking to see if the new builder has the right proprieties: ERA
    void testGetEra(){
        Builder builder = new Builder(EraEnum.I, 2, 2, 5);
        assertEquals(EraEnum.I, builder.getEra(), "The builder should have the chosen ERA");
    }
    @Test//Checking to see if the new builder has the right proprieties: numPlayers
    void testGetNumPlayers(){
        Builder builder = new Builder(EraEnum.I, 2, 2, 5);
        assertEquals(2, builder.getNumPlayers(), "The builder should have the chosen numPlayers");
    }
    @Test//Checking to see if the new builder has the right proprieties: wingCount
    void testGetWingCount(){
        Builder builder = new Builder(EraEnum.I, 2, 2, 5);
        assertEquals(2, builder.getWingCount(), "The builder should have the chosen wingCount");
    }
    @Test//Checking to see if the new builder has the right proprieties: endGamePP
    void testGetEndGamePP(){
        Builder builder = new Builder(EraEnum.I, 2, 2, 5);
        assertEquals(5, builder.getEndGamePP(), "The builder should have the chosen endGamePP");
    }

    @Test//checking to see if the player's builder list has increased in size after adding a builder
    void testAddToPlayerTribe_builderAddedToList(){
        Builder builder = new Builder(EraEnum.I, 2, 2, 5);
        int before = player.getCharacterByType(CharacterEnum.BUILDER).size();

        builder.AddToPlayerTribe(player, board);
        assertEquals(before + 1, player.getCharacterByType(CharacterEnum.BUILDER).size(),
                "The number of builders in the player's builder list should be increased by one");
    }
    @Test//checking to see if the player's builder list actually contains the new builder
    void testAddToPlayerTribe_correctCardInList(){
        Builder builder = new Builder(EraEnum.I, 2, 2, 5);
        builder.AddToPlayerTribe(player, board);

        assertTrue(player.getCharacterByType(CharacterEnum.BUILDER).contains(builder),
                "The player's builder list doesn't contain the new builder" );
    }
    @Test//checking to see if all the other card are still in the player's tribe
    void testAddToPlayerTribe_doesNotAffectOtherCards(){
        Builder builder = new Builder(EraEnum.I, 2, 2, 5);
        int huntersBefore = player.getCharacterByType(CharacterEnum.HUNTER).size();
        int gatherersBefore = player.getCharacterByType(CharacterEnum.GATHERER).size();
        int inventorsBefore = player.getCharacterByType(CharacterEnum.INVENTOR).size();
        int shamansBefore = player.getCharacterByType(CharacterEnum.SHAMAN).size();
        int artistsBefore = player.getCharacterByType(CharacterEnum.ARTIST).size();

        builder.AddToPlayerTribe(player, board);

        assertEquals(huntersBefore, player.getCharacterByType(CharacterEnum.HUNTER).size(),
                "Adding a builder shouldn't change the number of hunters in the player's tribe");
        assertEquals(gatherersBefore, player.getCharacterByType(CharacterEnum.GATHERER).size(),
                "Adding a builder shouldn't change the number of gatherers in the player's tribe");
        assertEquals(inventorsBefore, player.getCharacterByType(CharacterEnum.INVENTOR).size(),
                "Adding a builder shouldn't change the number of inventors in the player's tribe");
        assertEquals(shamansBefore, player.getCharacterByType(CharacterEnum.SHAMAN).size(),
                "Adding a builder shouldn't change the number of shamans in the player's tribe");
        assertEquals(artistsBefore, player.getCharacterByType(CharacterEnum.ARTIST).size(),
                "Adding a builder shouldn't change the number of artists in the player's tribe");
    }
    @Test//checking for error after multiple adds
    void testAddToPlayer_multipleBuilder_allAdded(){
        new Builder(EraEnum.I, 2, 2, 5).AddToPlayerTribe(player, board);
        new Builder(EraEnum.II, 3, 3, 5).AddToPlayerTribe(player, board);

        assertEquals(2, player.getCharacterByType(CharacterEnum.BUILDER).size(),
                "The number of builders in the player's builders list should be increased by two");

    }
    @Test//Each builder gives the player a discount when buying a building
    void testAddToPlayerTribe_foodDiscountUpdated(){
        Builder builder = new Builder(EraEnum.I, 2, 2, 5);
        int discountBefore = player.getTotalFoodDiscountBuilder();

        builder.AddToPlayerTribe(player, board);

        assertEquals(discountBefore + builder.getWingCount(), player.getTotalFoodDiscountBuilder(),
                "The food discount should have increased by wingCounts");
    }
    @Test//Multiple adds should give the player multiple discounts
    void testAddToPlayerTribe_multipleBuilder_multipleDiscount(){
        Builder builder1 = new Builder(EraEnum.I, 2, 2, 5);
        Builder builder2 = new Builder(EraEnum.I, 2, 3, 5);
        int discountBefore = player.getTotalFoodDiscountBuilder();

        builder1.AddToPlayerTribe(player, board);
        builder2.AddToPlayerTribe(player, board);

        assertEquals(discountBefore + builder1.getWingCount() + builder2.getWingCount(), player.getTotalFoodDiscountBuilder(),
                "The total discount should have increased by the sum of the two wincCount");
    }
}
