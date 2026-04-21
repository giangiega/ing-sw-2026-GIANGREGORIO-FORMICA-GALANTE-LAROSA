package it.polimi.ingsw;
/**
 * @author Giuse
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BuildingBonusStarShamanTest {
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
    @Test//Checking to see if the building gives 3 extra stars
    void applyEventShamanBonusStars_setsExtraStars() {
        new BuildingBonusStarShaman().applyEventShamanBonusStars(player, board);
        assertEquals(3, player.getEffectiveStars(),
                "The building should have given 3 extra stars to the player");
    }
    @Test//Checking to see if the building gives more than 3 stars to the player
    void applyEventShamanBonusStars_calledTwice_stillReturns3() {
        BuildingBonusStarShaman effect = new BuildingBonusStarShaman();
        effect.applyEventShamanBonusStars(player, board);
        int starsAfterFirst = player.getEffectiveStars();
        effect.applyEventShamanBonusStars(player, board);
        assertTrue(player.getEffectiveStars() == starsAfterFirst,
                "The building should have given no extra stars to the player");
    }
    @Test//Checking to see if other applymethod modify something
    void otherApply_noChange (){
        int ppBefore = player.getPP();
        int foodBefore = player.getFood();
        BuildingFinal25PP b = new BuildingFinal25PP();
        b.applyOnCardAdded(player, board);
        try{
            b.applyEndTurn(player, board, -1, true);
        }catch (InvalidPlayerActionException e){
            return;
        }
        b.applyEventHunt(player, board);
        b.applyEventCavePainting(player, board);
        b.applyEventSustenance(player, board);
        b.applyEventShamanWinner(player, board, 10, false);
        assertEquals(ppBefore, player.getPP(),
                "Calling other apply methods shouldn't have modified the player's prestige points");
    }
}