/**
 * @author Giuse
 */
package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.cards.tribe.characters.Hunter;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.game.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HunterTest {
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
        player = new Player("TestPlayer", ColorEnum.WHITE);
    }

    @Test//Checking to see if the new hunter has the right proprieties: ERA
    void testGetEra(){
        Hunter hunter = new Hunter(EraEnum.I, 2, true);
        assertEquals(EraEnum.I, hunter.getEra(), "The hunter should have the chosen ERA");
    }
    @Test//Checking to see if the new hunter has the right proprieties: numPlayers
    void testGetNumPlayers(){
        Hunter hunter = new Hunter(EraEnum.I, 2, false);
        assertEquals(2, hunter.getNumPlayers(), "The hunter should have the chosen numPlayers");
    }
    //It is important to check both values of hunt: the hunter gives you food only if hunt is true
    @Test//Checking to see if the new hunter has the right proprieties: hunt = true
    void testGetHunt_true(){
        Hunter hunter = new Hunter(EraEnum.I, 2, true);
        assertTrue(hunter.getHunt(), "The hunter should have the chosen hunt = true");
    }
    @Test//Checking to see if the new hunter has the right proprieties: hunt = false
    void testGetHunt_false(){
        Hunter hunter = new Hunter(EraEnum.I, 2, false);
        assertFalse(hunter.getHunt(), "The hunter should have the chosen hunt = false");
    }

    @Test//checking to see if the player's hunter list has increased in size after adding the hunter
    void testAddToPlayerTribe_hunterAddedToList(){
        Hunter hunter = new Hunter(EraEnum.I, 2, true);
        int before = player.getCharacterByType(CharacterEnum.HUNTER).size();

        hunter.AddToPlayerTribe(player, board);
        assertEquals(before + 1, player.getCharacterByType(CharacterEnum.HUNTER).size(),
                "The number of hunters in the player's hunter list should be increased by one");
    }
    @Test//checking to see if the player's hunter list actually contains the new hunter having hunt = true
    void testAddToPlayerTribe_correctCardInList_huntTrue(){
        Hunter hunter = new Hunter(EraEnum.I, 2, true);
        hunter.AddToPlayerTribe(player, board);

        assertTrue(player.getCharacterByType(CharacterEnum.HUNTER).contains(hunter),
                "The player's hunter list doesn't contain the new hunter having hunt = true") ;
    }
    @Test//checking to see if the player's hunter list actually contains the new hunter having hunt = false
    void testAddToPlayerTribe_correctCardInList_huntFalse(){
        Hunter hunter = new Hunter(EraEnum.I, 2, false);
        hunter.AddToPlayerTribe(player, board);

        assertTrue(player.getCharacterByType(CharacterEnum.HUNTER).contains(hunter),
                "The player's hunter list doesn't contain the new hunter") ;
    }
    @Test//checking to see if all the other card are still in the player's tribe
    void testAddToPlayerTribe_doesNotAffectOtherCards(){
        Hunter hunter = new Hunter(EraEnum.I, 2, true);
        int buildersBefore = player.getCharacterByType(CharacterEnum.BUILDER).size();
        int gatherersBefore = player.getCharacterByType(CharacterEnum.GATHERER).size();
        int inventorsBefore = player.getCharacterByType(CharacterEnum.INVENTOR).size();
        int shamansBefore = player.getCharacterByType(CharacterEnum.SHAMAN).size();
        int artistsBefore = player.getCharacterByType(CharacterEnum.ARTIST).size();

        hunter.AddToPlayerTribe(player, board);

        assertEquals(buildersBefore, player.getCharacterByType(CharacterEnum.BUILDER).size(),
                "Adding a hunter shouldn't change the number of builders in the player's tribe");
        assertEquals(gatherersBefore, player.getCharacterByType(CharacterEnum.GATHERER).size(),
                "Adding a hunter shouldn't change the number of gatherers in the player's tribe");
        assertEquals(inventorsBefore, player.getCharacterByType(CharacterEnum.INVENTOR).size(),
                "Adding a hunter shouldn't change the number of inventors in the player's tribe");
        assertEquals(shamansBefore, player.getCharacterByType(CharacterEnum.SHAMAN).size(),
                "Adding a hunter shouldn't change the number of shamans in the player's tribe");
        assertEquals(artistsBefore, player.getCharacterByType(CharacterEnum.ARTIST).size(),
                "Adding a hunter shouldn't change the number of artists in the player's tribe");
    }
    @Test//checking for error after multiple adds
    void testAddToPlayer_multipleHunters_allAdded(){
        new Hunter(EraEnum.I, 2, true).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.II, 3, false).AddToPlayerTribe(player, board);

        assertEquals(2, player.getCharacterByType(CharacterEnum.HUNTER).size(),
                "The number of hunters in the player's hunter list should be increased by two");

    }
    @Test//Each hunter having hunt = true gives the player 1 food
    void testAddToPlayerTribe_huntTrue(){
        Hunter hunter = new Hunter(EraEnum.I, 2, true);
        int foodBefore = player.getFood();

        hunter.AddToPlayerTribe(player, board);

        assertEquals(foodBefore + 1, player.getFood(),
                "The player's food should have increased by one");
    }
    @Test//Each hunter having hunt = false gives the player no food
    void testAddToPlayerTribe_huntFalse(){
        Hunter hunter = new Hunter(EraEnum.I, 2, false);
        int foodBefore = player.getFood();

        hunter.AddToPlayerTribe(player, board);

        assertEquals(foodBefore, player.getFood(),
                "The player's food should have not changed");
    }
    @Test//Multiple adds should give the player multiple discounts
    void testAddToPlayerTribe_twoMixedHunters(){
        Hunter hunter1 = new Hunter(EraEnum.I, 2, false);
        hunter1.AddToPlayerTribe(player, board);
        int foodAfterNoHunt = player.getFood();

        Hunter hunter2 = new Hunter(EraEnum.I, 2, true);
        hunter2.AddToPlayerTribe(player, board);

        assertEquals(foodAfterNoHunt + 2, player.getFood(),
                "The player's food should have increased by two, because he has 2 hunters in his tribe");
    }
}
