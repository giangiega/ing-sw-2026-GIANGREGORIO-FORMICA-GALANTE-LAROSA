/**
 * @author Giuse
 */
package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.enums.IconEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.cards.tribe.characters.Inventor;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.game.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventorTest {
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
        player = new Player("TestPlayer", ColorEnum.YELLOW);
    }

    @Test
//Checking to see if the new inventor has the right proprieties: ERA
    void testGetEra() {
        Inventor inventor = new Inventor(EraEnum.I, 2, IconEnum.POINTER);
        assertEquals(EraEnum.I, inventor.getEra(), "The inventor should have the chosen ERA");
    }

    @Test
//Checking to see if the new inventor has the right proprieties: numPlayers
    void testGetNumPlayers() {
        Inventor inventor = new Inventor(EraEnum.I, 2, IconEnum.POINTER);
        assertEquals(2, inventor.getNumPlayers(), "The inventor should have the chosen numPlayers");
    }

    @Test
//Checking to see if the new inventor has the right proprieties: iconType
    void testGetIconType() {
        Inventor inventor = new Inventor(EraEnum.I, 2, IconEnum.POINTER);
        assertEquals(IconEnum.POINTER, inventor.getIconType(), "The inventor should have the chosen iconType");
    }

    @Test
//checking to see if the player's inventor list has increased in size after adding an inventor
    void testAddToPlayerTribe_inventorAddedToList() {
        Inventor inventor = new Inventor(EraEnum.I, 2, IconEnum.POINTER);
        int before = player.getCharacterByType(CharacterEnum.INVENTOR).size();

        inventor.AddToPlayerTribe(player, board);
        assertEquals(before + 1, player.getCharacterByType(CharacterEnum.INVENTOR).size(),
                "The number of inventors in the player's inventor list should be increased by one");
    }

    @Test
//checking to see if the player's inventors list actually contains the new inventor
    void testAddToPlayerTribe_correctCardInList() {
        Inventor inventor = new Inventor(EraEnum.I, 2, IconEnum.POINTER);
        inventor.AddToPlayerTribe(player, board);

        assertTrue(player.getCharacterByType(CharacterEnum.INVENTOR).contains(inventor),
                "The player's inventors list doesn't contain the new inventor");
    }

    @Test
//checking to see if all the other card are still in the player's tribe
    void testAddToPlayerTribe_doesNotAffectOtherCards() {
        Inventor inventor = new Inventor(EraEnum.I, 2, IconEnum.POINTER);
        int huntersBefore = player.getCharacterByType(CharacterEnum.HUNTER).size();
        int gatherersBefore = player.getCharacterByType(CharacterEnum.GATHERER).size();
        int shamansBefore = player.getCharacterByType(CharacterEnum.SHAMAN).size();
        int buildersBefore = player.getCharacterByType(CharacterEnum.BUILDER).size();
        int artistsBefore = player.getCharacterByType(CharacterEnum.ARTIST).size();

        inventor.AddToPlayerTribe(player, board);

        assertEquals(huntersBefore, player.getCharacterByType(CharacterEnum.HUNTER).size(),
                "Adding an inventor shouldn't change the number of hunters in the player's tribe");
        assertEquals(gatherersBefore, player.getCharacterByType(CharacterEnum.GATHERER).size(),
                "Adding an inventor shouldn't change the number of gatherers in the player's tribe");
        assertEquals(shamansBefore, player.getCharacterByType(CharacterEnum.SHAMAN).size(),
                "Adding an inventor shouldn't change the number of shamans in the player's tribe");
        assertEquals(buildersBefore, player.getCharacterByType(CharacterEnum.BUILDER).size(),
                "Adding an inventor shouldn't change the number of builders in the player's tribe");
        assertEquals(artistsBefore, player.getCharacterByType(CharacterEnum.ARTIST).size(),
                "Adding an inventor shouldn't change the number of artists in the player's tribe");
    }

    @Test
//checking for error after multiple adds
    void multipleAdds() {
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        new Inventor(EraEnum.II, 3, IconEnum.BOAT).AddToPlayerTribe(player, board);

        assertEquals(2, player.getCharacterByType(CharacterEnum.INVENTOR).size(),
                "The number of inventors in the player's inventors list should be increased by two");

    }

    @Test
//Adding an inventor with a new iconType should increase the player's DistinctInventorsIconCount
    void testAddToPlayer_firstIconType_incrementsCount() {
        int distinctBefore = player.getDistinctInventorsIcon();

        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);

        assertEquals(distinctBefore + 1, player.getDistinctInventorsIcon(),
                "The counter should have increased by one");
    }

    @Test
//Adding an inventor with a duplicated IconType shouldn't increase the counter
    void testAddToPlayer_duplicatedIconType_noIncrement() {
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        int distinctAfterFirst = player.getDistinctInventorsIcon();

        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);

        assertEquals(distinctAfterFirst, player.getDistinctInventorsIcon(),
                "The counter should have not increased");
    }

    @Test
//Adding two inventors with a new IconType increments the counter twice
    void testAddToPlayer_multipleNewInventors_twoIncrement() {
        int distinctAfterFirst = player.getDistinctInventorsIcon();

        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        new Inventor(EraEnum.I, 2, IconEnum.BOAT).AddToPlayerTribe(player, board);

        assertEquals(distinctAfterFirst + 2, player.getDistinctInventorsIcon(),
                "The counter should have increased by two");
    }


    @Test
//Adding mixed inventors with a mixed IconType
    void testAddToPlayer_mixedInventors() {
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        new Inventor(EraEnum.I, 2, IconEnum.BOAT).AddToPlayerTribe(player, board);

        assertEquals(2, player.getDistinctInventorsIcon(),
                "The counter should have increased by two");
        assertEquals(3, player.getCharacterByType(CharacterEnum.INVENTOR).size(),
                "The number of inventors in the player's inventor list should be increased by 3");
    }

    @Test
//Checking for no couples
    void noCoupleSameInventorsUpdate() {
        int couplesBefore = player.getCoupleSameInventors();
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        assertEquals(couplesBefore, player.getCoupleSameInventors(),
                "The player should have 0 couple");
    }

    @Test
//Checking to see if coupleSameInventors gets updated
    void coupleSameInventorsUpdate() {
        int couplesBefore = player.getCoupleSameInventors();
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        assertEquals(couplesBefore + 1, player.getCoupleSameInventors(),
                "The player should have 1 couple");
    }

    @Test
//Checking for multiple couples
    void multipleCouplesSameInventorsUpdate() {
        int couplesBefore = player.getCoupleSameInventors();
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        assertEquals(couplesBefore + 2, player.getCoupleSameInventors(),
                "The player should have 2 couple");
    }

    @Test
//Checking for incomplete couples
    void incompleteCoupleSameInventorsUpdate() {
        int couplesBefore = player.getCoupleSameInventors();
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        new Inventor(EraEnum.I, 2, IconEnum.POINTER).AddToPlayerTribe(player, board);
        assertEquals(couplesBefore + 1, player.getCoupleSameInventors(),
                "The player should have only 1 couple");
    }
}