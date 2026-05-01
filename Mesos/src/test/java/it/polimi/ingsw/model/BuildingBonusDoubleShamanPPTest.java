package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BuildingBonusDoubleShamanPPTest {
    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;
    private BuildingBonusDoubleShamanPP effect;

    @BeforeEach
    void setUp() {
        config = GameConfig.create(2);
        board  = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("TestPlayer", ColorEnum.BLUE);
        effect = new BuildingBonusDoubleShamanPP();
    }
    @Test//Checking to see if the building gives double the pp to the winner
    void winTrue_addsGainedPP() {
        int ppBefore = player.getPP();
        effect.applyEventShamanWinner(player, board, 10, true);
        assertEquals(ppBefore + 10, player.getPP(),
                "The building should have given 10 prestige points to the player because he won");
    }
    @Test//Checking to see if the building gives any point if win is false
    void winFalse_doesNothing() {
        int ppBefore = player.getPP();
        effect.applyEventShamanWinner(player, board, 10, false);
        assertEquals(ppBefore, player.getPP(),
                "The building should have given the player no prestige points because win was false");
    }
    @Test//Checking to see if the building gives you any pp if gainedPP = 0
    void winTrue_zeroPP_noChange() {
        int ppBefore = player.getPP();
        effect.applyEventShamanWinner(player, board, 0, true);
        assertEquals(ppBefore, player.getPP(),
                "\"The building should have given the player no prestige points");
    }
    @Test
    void toStringTestValues(){
        BuildingCard c = new BuildingCard(EraEnum.I, 3, 2, new BuildingBonusDoubleShamanPP());
        String result = c.toString();

        assertTrue(result.contains("I"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("BuildingBonusDoubleShamanPP"));
    }
    @Test
    void toStringTestMessage(){
        BuildingCard c = new BuildingCard(EraEnum.I, 3, 2, new BuildingBonusDoubleShamanPP());
        String expected = "BuildingCard with effect: BuildingBonusDoubleShamanPP and" +
                "\nEra : I" +
                "\nFood cost : 3" +
                "\nPrestige point earned : 2";

        assertEquals(expected, c.toString());
    }
}
