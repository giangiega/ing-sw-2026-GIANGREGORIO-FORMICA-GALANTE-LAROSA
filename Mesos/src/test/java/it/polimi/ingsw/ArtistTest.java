package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class ArtistTest {
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
        player = new Player("TestPlayer", ColorEnum.BLUE);
    }

    @Test//Checking to see if the new artist has the right proprieties: ERA
    void testGetEra(){
        Artist artist = new Artist(EraEnum.I, 2);
        assertEquals(EraEnum.I, artist.getEra(), "Artist should have the chosen ERA");
    }
    @Test
    void testGetNumPlayers(){//Checking to see if the new artist has the right proprieties: numPlayers
        Artist artist = new Artist(EraEnum.I, 2);
        assertEquals(2, artist.getNumPlayers(), "Artist should have the chosen number of players");
    }

    @Test//checking to see if the player's artist list has increased in size after adding an artist
    void testAddToPlayerTribe_artistAddedToList(){
        Artist artist = new Artist(EraEnum.I, 2);
        int before = player.getCharacterByType(CharacterEnum.ARTIST).size();

        artist.AddToPlayerTribe(player, board);
        assertEquals(before + 1, player.getCharacterByType(CharacterEnum.ARTIST).size(),
                "The number of artists in the player's artist list should be increased by one" );
    }
    @Test//checking to see if the player's artist list actually contains the new artist
    void testAddToPlayerTribe_correctCardInList(){
        Artist artist = new Artist(EraEnum.I, 2);
        artist.AddToPlayerTribe(player, board);

        assertTrue(player.getCharacterByType(CharacterEnum.ARTIST).contains(artist),
                "The player's artist list doesn't contain the new artist" );
    }
    @Test//checking to see if all the other card are still in the player's tribe
    void testAddToPlayerTribe_doesNotAffectOtherCards(){
        Artist artist = new Artist(EraEnum.I, 2);
        int huntersBefore = player.getCharacterByType(CharacterEnum.HUNTER).size();
        int buildersBefore = player.getCharacterByType(CharacterEnum.BUILDER).size();
        int inventorsBefore = player.getCharacterByType(CharacterEnum.INVENTOR).size();
        int shamansBefore = player.getCharacterByType(CharacterEnum.SHAMAN).size();
        int gatherersBefore = player.getCharacterByType(CharacterEnum.GATHERER).size();

        artist.AddToPlayerTribe(player, board);

        assertEquals(huntersBefore, player.getCharacterByType(CharacterEnum.HUNTER).size(),
                "Adding an artist shouldn't change the number of hunters in the player's tribe");
        assertEquals(buildersBefore, player.getCharacterByType(CharacterEnum.BUILDER).size(),
                "Adding an artist shouldn't change the number of builders in the player's tribe");
        assertEquals(inventorsBefore, player.getCharacterByType(CharacterEnum.INVENTOR).size(),
                "Adding an artist shouldn't change the number of inventors in the player's tribe");
        assertEquals(shamansBefore, player.getCharacterByType(CharacterEnum.SHAMAN).size(),
                "Adding an artist shouldn't change the number of shamans in the player's tribe");
        assertEquals(gatherersBefore, player.getCharacterByType(CharacterEnum.GATHERER).size(),
                "Adding an artist shouldn't change the number of gatherers in the player's tribe");
    }
    @Test//checking for error after multiple adds
    void testAddToPlayer_multipleArtists_allAdded(){
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Artist(EraEnum.II, 3).AddToPlayerTribe(player, board);

        assertEquals(2, player.getCharacterByType(CharacterEnum.ARTIST).size(),
                "The number of artists in the player's artist list should be increased by two");

    }

}
