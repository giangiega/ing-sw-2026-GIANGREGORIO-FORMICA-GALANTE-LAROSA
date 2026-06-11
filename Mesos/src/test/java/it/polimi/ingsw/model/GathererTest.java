/**
 * @author Giuse
 */
package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.cards.tribe.characters.Gatherer;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.game.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GathererTest {
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
        player = new Player("TestPlayer", ColorEnum.RED);
    }

    @Test//Checking to see if the new gatherer has the right proprieties: ERA
    void testGetEra(){
        Gatherer gatherer = new Gatherer(EraEnum.I, 2);
        assertEquals(EraEnum.I, gatherer.getEra(), "The gatherer should have the chosen ERA");
    }
    @Test//Checking to see if the new gatherer has the right proprieties: numPlayers
    void testGetNumPlayers(){
        Gatherer gatherer = new Gatherer(EraEnum.I, 2);
        assertEquals(2, gatherer.getNumPlayers(), "The gatherer should have the chosen numPlayers");
    }

    @Test//checking to see if the player's gatherer list has increased in size after adding a Gatherer
    void testAddToPlayerTribe_gathererAddedToList(){
        Gatherer gatherer = new Gatherer(EraEnum.I, 2);
        int before = player.getCharacterByType(CharacterEnum.GATHERER).size();

        gatherer.AddToPlayerTribe(player, board);
        assertEquals(before + 1, player.getCharacterByType(CharacterEnum.GATHERER).size(),
                "The number of gatherers in the player's gatherer list should be increased by one");
    }
    @Test//checking to see if the player's gatherer list actually contains the new gatherer
    void testAddToPlayerTribe_correctCardInList(){
        Gatherer gatherer = new Gatherer(EraEnum.I, 2);
        gatherer.AddToPlayerTribe(player, board);

        assertTrue(player.getCharacterByType(CharacterEnum.GATHERER).contains(gatherer),
                "The player's gatherer list doesn't contain the new gatherer");
    }
    @Test//checking to see if all the other card are still in the player's tribe
    void testAddToPlayerTribe_doesNotAffectOtherCards(){
        Gatherer gatherer = new Gatherer(EraEnum.I, 2);
        int huntersBefore = player.getCharacterByType(CharacterEnum.HUNTER).size();
        int buildersBefore = player.getCharacterByType(CharacterEnum.BUILDER).size();
        int inventorsBefore = player.getCharacterByType(CharacterEnum.INVENTOR).size();
        int shamansBefore = player.getCharacterByType(CharacterEnum.SHAMAN).size();
        int artistsBefore = player.getCharacterByType(CharacterEnum.ARTIST).size();

        gatherer.AddToPlayerTribe(player, board);

        assertEquals(huntersBefore, player.getCharacterByType(CharacterEnum.HUNTER).size(),
                "Adding a gatherer shouldn't change the number of hunters in the player's tribe");
        assertEquals(buildersBefore, player.getCharacterByType(CharacterEnum.BUILDER).size(),
                "Adding a gatherer shouldn't change the number of builders in the player's tribe");
        assertEquals(inventorsBefore, player.getCharacterByType(CharacterEnum.INVENTOR).size(),
                "Adding a gatherer shouldn't change the number of inventors in the player's tribe");
        assertEquals(shamansBefore, player.getCharacterByType(CharacterEnum.SHAMAN).size(),
                "Adding a gatherer shouldn't change the number of shamans in the player's tribe");
        assertEquals(artistsBefore, player.getCharacterByType(CharacterEnum.ARTIST).size(),
                "Adding a gatherer shouldn't change the number of artists in the player's tribe");
    }
    @Test//checking for error after multiple adds
    void testAddToPlayer_multipleGatherers_allAdded(){
        new Gatherer(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Gatherer(EraEnum.II, 3).AddToPlayerTribe(player, board);

        assertEquals(2, player.getCharacterByType(CharacterEnum.GATHERER).size(),
                "The number of gatherers in the player's gatherer list should be increased by two");

    }

}
