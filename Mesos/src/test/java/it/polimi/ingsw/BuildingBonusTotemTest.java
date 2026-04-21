package it.polimi.ingsw;
/**
 * @author Giuse
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class BuildingBonusTotemTest {

    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;
    private BuildingBonusTotem effect;

    @BeforeEach
    void setUp() throws Exception {
        config = GameConfig.create(2);
        board  = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("TestPlayer", ColorEnum.BLUE);
        effect = new BuildingBonusTotem();
    }

    @Test//Checking to see if the building gives 1 food
    void applyEndTurn_gives1ExtraFood() throws Exception {
        int foodBefore = player.getFood();
        effect.applyEndTurn(player, board, -1, false);
        assertEquals(foodBefore + 1, player.getFood(),
                "The player should have received 1 food");
    }

    @Test//Checking to see if chosenIndex is relevant
    void applyEndTurn_chosenIndexIgnored() throws Exception {
        int foodBefore = player.getFood();
        effect.applyEndTurn(player, board, 5, false);
        assertEquals(foodBefore + 1, player.getFood(),
                "The player should have received 1 food");
    }
    @Test//Checking to see if chosenIsBuilding is relevant
    void applyEndTurn_chosenIsBuildingIgnored() throws Exception {
        int foodBefore = player.getFood();
        effect.applyEndTurn(player, board, 0, true);
        assertEquals(foodBefore + 1, player.getFood(),
                "The player should have received 1 food");
    }
    @Test//Checking to see if the player gets prestige points
    void applyEndTurn_doesNotChangePP() throws Exception {
        int ppBefore = player.getPP();
        effect.applyEndTurn(player, board, -1, false);
        assertEquals(ppBefore, player.getPP(),
                "The player should have not received any prestige points");
    }
}
