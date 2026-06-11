/**
 * @author Giuse
 */
package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.cards.tribe.characters.Artist;
import it.polimi.ingsw.model.cards.buildings.BuildingBonusHunt;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.characters.Hunter;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.game.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BuildingBonusHuntTest {
    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;
    private BuildingBonusHunt effect;

    @BeforeEach
    void setUp() {
        config = GameConfig.create(2);
        board  = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("TestPlayer", ColorEnum.BLUE);
        effect = new BuildingBonusHunt();
    }
    @Test//Checking to see if the building gives the player anything if he has no hunters
    void noHunters() {
        int foodBefore = player.getFood();
        int ppBefore = player.getPP();
        effect.applyEventHunt(player, board);
        assertEquals(foodBefore, player.getFood());
        assertEquals(ppBefore, player.getPP(),
        "The player should have not received any prestige points");
    }
    @Test//Checking to see if the building gives 1 extra food and 1 extra prestige point
    void oneHunter() {
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        int foodBefore = player.getFood();
        int ppBefore = player.getPP();
        effect.applyEventHunt(player, board);
        assertEquals(foodBefore + 1, player.getFood(),
                "The player should have received 1 food because he has a hunter");
        assertEquals(ppBefore + 1, player.getPP(),
                "The player should have received 1 prestige point because he has a hunter");
    }
    @Test//Checking to see if the building gives you the right rewards for multiple hunters
    void multipleHunters() {
        for (int i = 0; i < 3; i++)
            new Hunter(EraEnum.I, 0, false).AddToPlayerTribe(player, board);
        int foodBefore = player.getFood();
        int ppBefore = player.getPP();
        effect.applyEventHunt(player, board);
        assertEquals(foodBefore + 3, player.getFood(),
                "The player should have received 3 food because he has 3 hunters");
        assertEquals(ppBefore + 3, player.getPP(),
                "The player should have received 3 prestige points because he has 3 hunters");
    }
    @Test//Checking to see if the building works just for hunters
    void onlyCountsHunters() {
        new Hunter(EraEnum.I, 0, false).AddToPlayerTribe(player, board);
        new Artist(EraEnum.I, 0).AddToPlayerTribe(player, board);
        int foodBefore = player.getFood();
        int ppBefore = player.getPP();
        effect.applyEventHunt(player, board);
        assertEquals(foodBefore + 1, player.getFood(),
                "The player should have received 1 food because he has 1 hunter");
        assertEquals(ppBefore + 1, player.getPP(),
                "The player should have received 1 prestige points because he has 1 hunter");
    }
    @Test
    void toStringTestValues(){
        BuildingCard c = new BuildingCard(EraEnum.I, 3, 2, new BuildingBonusHunt());
        String result = c.toString();

        assertTrue(result.contains("I"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("BuildingBonusHunt"));
    }

}
