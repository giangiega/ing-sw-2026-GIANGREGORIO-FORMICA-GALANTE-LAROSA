package it.polimi.ingsw;
/**
 * @author Giuse
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BuildingSaveShamanPPTest {
    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;
    private BuildingSaveShamanPP effect;

    @BeforeEach
    void setUp() {
        config = GameConfig.create(2);
        board  = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("TestPlayer", ColorEnum.BLUE);
        effect = new BuildingSaveShamanPP();
    }

    @Test//Checking to see if the building gives the loser the lost prestige points
    void winFalse_reimbursesLostPP() {
        int ppBefore = player.getPP();
        effect.applyEventShamanWinner(player, board, 5, false);
        assertEquals(ppBefore + 5, player.getPP(),
                "The building should have given 5 prestige points to the player");
    }
    @Test//Checking to see if the building gives the winner any additional prestige points
    void winTrue_doesNothing() {
        int ppBefore = player.getPP();
        effect.applyEventShamanWinner(player, board, 5, true);
        assertEquals(ppBefore, player.getPP(),
                "The building should have given 0 prestige points to the player because he won the event");
    }
    @Test//Checking to see if the building gives you any pp if lostPP = 0
    void winFalse_zeroPP_noChange() {
        int ppBefore = player.getPP();
        effect.applyEventShamanWinner(player, board, 0, false);
        assertEquals(ppBefore, player.getPP(),
                "The building should have given 0 prestige points to the player");
    }
}