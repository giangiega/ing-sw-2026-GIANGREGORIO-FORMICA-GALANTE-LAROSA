package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShamanTest {
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

    @Test//Checking to see if the new shaman has the right proprieties: ERA
    void testGetEra(){
        Shaman shaman = new Shaman(EraEnum.I, 2, 3);
        assertEquals(EraEnum.I, shaman.getEra(), "The shaman should have the chosen ERA");
    }
    @Test//Checking to see if the new shaman has the right proprieties: numPlayers
    void testGetNumPlayers(){
        Shaman shaman = new Shaman(EraEnum.I, 2, 3);
        assertEquals(2, shaman.getNumPlayers(), "The shaman should have the chosen numPlayers");
    }
    @Test//Checking to see if the new shaman has the right proprieties: starCount
    void testGetStarCount(){
        Shaman shaman = new Shaman(EraEnum.I, 2, 3);
        assertEquals(3, shaman.getStarCount(), "The shaman should have the chosen starCount");
    }

    @Test//checking to see if the player's shaman list has increased in size after adding a shaman
    void testAddToPlayerTribe_shamanAddedToList(){
        Shaman shaman = new Shaman(EraEnum.I, 2, 3);
        int before = player.getCharacterByType(CharacterEnum.SHAMAN).size();

        shaman.AddToPlayerTribe(player, board);
        assertEquals(before + 1, player.getCharacterByType(CharacterEnum.SHAMAN).size(),
                "The number of shamans in the player's shaman list should be increased by one");
    }
    @Test//checking to see if the player's shaman list actually contains the new shaman
    void testAddToPlayerTribe_correctCardInList(){
        Shaman shaman = new Shaman(EraEnum.I, 2, 3);
        shaman.AddToPlayerTribe(player, board);

        assertTrue(player.getCharacterByType(CharacterEnum.SHAMAN).contains(shaman),
                "The player's shaman list doesn't contain the new shaman" );
    }
    @Test//checking to see if all the other card are still in the player's tribe
    void testAddToPlayerTribe_doesNotAffectOtherCards(){
        Shaman shaman = new Shaman(EraEnum.I, 2, 2);
        int huntersBefore = player.getCharacterByType(CharacterEnum.HUNTER).size();
        int gatherersBefore = player.getCharacterByType(CharacterEnum.GATHERER).size();
        int inventorsBefore = player.getCharacterByType(CharacterEnum.INVENTOR).size();
        int buildersBefore = player.getCharacterByType(CharacterEnum.BUILDER).size();
        int artistsBefore = player.getCharacterByType(CharacterEnum.ARTIST).size();

        shaman.AddToPlayerTribe(player, board);

        assertEquals(huntersBefore, player.getCharacterByType(CharacterEnum.HUNTER).size(),
                "Adding a shaman shouldn't change the number of hunters in the player's tribe");
        assertEquals(gatherersBefore, player.getCharacterByType(CharacterEnum.GATHERER).size(),
                "Adding a shaman shouldn't change the number of gatherers in the player's tribe");
        assertEquals(inventorsBefore, player.getCharacterByType(CharacterEnum.INVENTOR).size(),
                "Adding a shaman shouldn't change the number of inventors in the player's tribe");
        assertEquals(buildersBefore, player.getCharacterByType(CharacterEnum.BUILDER).size(),
                "Adding a shaman shouldn't change the number of builders in the player's tribe");
        assertEquals(artistsBefore, player.getCharacterByType(CharacterEnum.ARTIST).size(),
                "Adding a shaman shouldn't change the number of artists in the player's tribe");
    }
    @Test//checking for error after multiple adds
    void testAddToPlayer_multipleShamans_allAdded(){
        new Shaman(EraEnum.I, 2, 2).AddToPlayerTribe(player, board);
        new Shaman(EraEnum.II, 3, 3).AddToPlayerTribe(player, board);

        assertEquals(2, player.getCharacterByType(CharacterEnum.SHAMAN).size(),
                "The number of shamans in the player's shamans list should be increased by two");

    }
    @Test//Each shaman increases the player's effectiveStars
    void testAddToPlayerTribe_countShamanStarsUpdated(){
        int starsBefore = player.getEffectiveStars();
        Shaman shaman = new Shaman(EraEnum.I, 2, 3);

        shaman.AddToPlayerTribe(player, board);

        assertEquals(starsBefore + shaman.getStarCount(), player.getEffectiveStars(),
                "The amount of shaman stars should have increased by starCount");
    }
    @Test//A shaman with no stars (although it doesn't exist) shouldn't give you anything
    void testAddToPlayerTribe_zeroStars_zeroChange(){
        int starsBefore = player.getEffectiveStars();
        Shaman shaman = new Shaman(EraEnum.I, 2, 0);

        shaman.AddToPlayerTribe(player, board);

        assertEquals(starsBefore, player.getEffectiveStars(), "The amount of stars should have not increased");
    }
    @Test//Multiple adds should give the player multiple stars
    void testAddToPlayerTribe_multipleShamans_multipleStarsAdded(){
        int starsBefore = player.getEffectiveStars();
        Shaman shaman1 = new Shaman(EraEnum.I, 2, 3);
        Shaman shaman2 = new Shaman(EraEnum.I, 2,2);

        shaman1.AddToPlayerTribe(player, board);
        shaman2.AddToPlayerTribe(player, board);

        assertEquals(starsBefore + shaman1.getStarCount() + shaman2.getStarCount(), player.getEffectiveStars(),
                "The amount of shaman stars should have increased by the sum of the two starCounts");
    }
}
