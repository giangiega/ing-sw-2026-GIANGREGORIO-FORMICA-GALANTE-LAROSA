package it.polimi.ingsw.model;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.game.GameConfig;
import it.polimi.ingsw.model.game.GameConfig3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TurnOrderTileTest {
    TurnOrderTile tile;
    GameConfig gc3;
    Player p1;
    Player p2;
    Player p3;

    @BeforeEach
    void setUp() {
        gc3 = new GameConfig3();
        tile = new TurnOrderTile(3, gc3.getFoodBonuses());

        p1 = new Player("p1", ColorEnum.BLUE);
        p2 = new Player("p2", ColorEnum.RED);
        p3 = new Player("p3", ColorEnum.WHITE);
        tile.totemIn(p1);
        tile.totemIn(p2);
        tile.totemIn(p3);
    }

    @Test
    void testTotemInAndOutOrder(){
        assertEquals(3, tile.getOrder().size());
        assertEquals(p1, tile.getOrder().get(0));
        assertEquals(p2, tile.getOrder().get(1));
        assertEquals(p3, tile.getOrder().get(2));

        tile.totemOut(p2);

        assertEquals(2, tile.getOrder().size());
        assertEquals(p1, tile.getOrder().get(0));
        assertEquals(p3, tile.getOrder().get(1));
    }

    @Test
    void testLastSlot(){
        assertFalse(tile.isLastSlot(0));
        assertFalse(tile.isLastSlot(1));
        assertTrue(tile.isLastSlot(2));
    }

    @Test
    void testFoodBonuses(){
        assertEquals(2, tile.getFoodBonusForSlot(0));
        assertEquals(0, tile.getFoodBonusForSlot(1));
        assertEquals(0, tile.getFoodBonusForSlot(2));
    }

    @Test
    void testSlots(){
        assertEquals(3, tile.getSlots().size());
        assertEquals(p1, tile.getSlots().get(0));
        assertEquals(p2, tile.getSlots().get(1));
        assertEquals(p3, tile.getSlots().get(2));
    }

}
