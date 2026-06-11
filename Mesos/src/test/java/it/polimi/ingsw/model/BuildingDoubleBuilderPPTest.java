/**
 * @author Giuse
 */
package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.cards.tribe.characters.Builder;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.buildings.BuildingDoubleBuilderPP;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.game.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class BuildingDoubleBuilderPPTest {
    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;
    private BuildingDoubleBuilderPP effect;

    @BeforeEach
    void setUp() {
        config = GameConfig.create(2);
        board  = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("TestPlayer", ColorEnum.BLUE);
        effect = new BuildingDoubleBuilderPP();
    }
    @Test//Checking to see if the building gives the player any prestige points if he has no builders
    void noBuilders() {
        int ppBefore = player.getPP();
        effect.applyEndGame(player, board);
        assertEquals(ppBefore, player.getPP(),
                "The player should have not received any prestige points because he has no builders");
    }
    @Test//Checking to see if the building gives the player any prestige points if he has 1 builder
    void oneBuilder() {
        new Builder(EraEnum.I, 2, 0, 3).AddToPlayerTribe(player, board);
        int ppBefore = player.getPP();
        effect.applyEndGame(player, board);
        assertEquals(ppBefore + 3, player.getPP(),
                "The player should have received 3 prestige points because his only hunter gives 3 prestige points");
    }
    @Test//Checking to see if the building gives the player any prestige points if he has multiple builders
    void multipleBuilders() {
        new Builder(EraEnum.I, 2, 0, 3).AddToPlayerTribe(player, board);
        new Builder(EraEnum.I, 2, 0, 1).AddToPlayerTribe(player, board);
        int ppBefore = player.getPP();
        effect.applyEndGame(player, board);
        assertEquals(ppBefore + 4, player.getPP(),
                "The player should have received 4 prestige points (3 + 1)");
    }
    @Test//Checking to see if the building gives the player any food
    void doesNotChangeFood() {
        new Builder(EraEnum.I, 0, 0, 3).AddToPlayerTribe(player, board);
        int foodBefore = player.getFood();
        effect.applyEndGame(player, board);
        assertEquals(foodBefore, player.getFood(),
                "The player should have received food no food");
    }
    @Test
    void toStringTestValues(){
        BuildingCard c = new BuildingCard(EraEnum.I, 3, 2, new BuildingDoubleBuilderPP());
        String result = c.toString();

        assertTrue(result.contains("I"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("BuildingDoubleBuilderPP"));
    }
}