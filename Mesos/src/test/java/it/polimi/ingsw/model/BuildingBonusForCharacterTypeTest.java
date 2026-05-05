package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.cards.tribe.characters.Artist;
import it.polimi.ingsw.model.cards.buildings.BuildingBonusForCharacterType;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.characters.Hunter;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.game.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BuildingBonusForCharacterTypeTest {
    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;

    @BeforeEach
    void setUp() {
        config = GameConfig.create(2);
        board  = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("TestPlayer", ColorEnum.BLUE);
    }
    @Test//Checking the constructor
    void getPP_returnsConstructorValue() {
        assertEquals(3, new BuildingBonusForCharacterType(3, CharacterEnum.HUNTER).getPP());
    }
    @Test
    void getCharacter_returnsConstructorValue() {
        assertEquals(CharacterEnum.SHAMAN,
                new BuildingBonusForCharacterType(2, CharacterEnum.SHAMAN).getCharacter());
    }
    @Test//Checking to see if the building gives the player any prestige points if he has no characters of the required type
    void noCharactersOfType() {
        int ppBefore = player.getPP();
        new BuildingBonusForCharacterType(3, CharacterEnum.HUNTER).applyEndGame(player, board);
        assertEquals(ppBefore, player.getPP(),
                "The player should have received 0 prestige points because he has no hunters");
    }
    @Test//Checking to see if the building gives the player any prestige points if he has 1 character of the required type
    void oneCharacter() {
        new Hunter(EraEnum.I, 0, false).AddToPlayerTribe(player, board);
        int ppBefore = player.getPP();
        new BuildingBonusForCharacterType(3, CharacterEnum.HUNTER).applyEndGame(player, board);
        assertEquals(ppBefore + 3, player.getPP(),
               "The player should have received 3 prestige points as stated on the building card" );
    }
    @Test
    void multipleCharacters() {
        new Hunter(EraEnum.I, 0, false).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 0, false).AddToPlayerTribe(player, board);
        int ppBefore = player.getPP();
        new BuildingBonusForCharacterType(3, CharacterEnum.HUNTER).applyEndGame(player, board);
        assertEquals(ppBefore + 6, player.getPP(),
                "The player should have received 6 prestige points as stated on the building card (3 * 2 characters)" );
    }
    @Test//Checking to see if the building works just for the right type
    void onlyCountsSpecifiedType() {
        new Hunter(EraEnum.I, 0, false).AddToPlayerTribe(player, board);
        new Artist(EraEnum.I, 0).AddToPlayerTribe(player, board);
        int ppBefore = player.getPP();
        new BuildingBonusForCharacterType(3, CharacterEnum.HUNTER).applyEndGame(player, board);
        assertEquals(ppBefore + 3, player.getPP(),
                "The player should have received just 3 prestige points because he has only 1 character of the required type"); // solo 1 hunter
    }
    @Test//Checking to see if the building gives the playe any food
    void doesNotChangeFood() {
        new Hunter(EraEnum.I, 0, false).AddToPlayerTribe(player, board);
        int foodBefore = player.getFood();
        new BuildingBonusForCharacterType(3, CharacterEnum.HUNTER).applyEndGame(player, board);
        assertEquals(foodBefore, player.getFood(),
                "The player shouldn't have received any food");
    }
    @Test
    void toStringTestValues(){
        BuildingCard c = new BuildingCard(EraEnum.I, 3, 2, new BuildingBonusForCharacterType(3, CharacterEnum.BUILDER));
        String result = c.toString();

        assertTrue(result.contains("I"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("BuildingBonusForCharacterType"));
    }
    @Test
    void toStringTestMessage(){
        BuildingCard c = new BuildingCard(EraEnum.I, 3, 2, new BuildingBonusForCharacterType(3, CharacterEnum.BUILDER));
        String expected = "BuildingCard with effect: BuildingBonusForCharacterType: " +
                "for each character BUILDER it gives 3 prestige points " + "and -->" +
                "\n\t\tEra : I" +
                "\n\t\tFood cost : 3" +
                "\n\t\tPrestige point earned : 2";

        assertEquals(expected, c.toString());
    }
}

