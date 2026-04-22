package it.polimi.ingsw.model;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.enums.IconEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OfferTileTest {
    OfferTile tile;
    Player player;

    @BeforeEach
    void setUp() {
        tile = new OfferTile('B', 0, 1);
        player = new Player("Riccardo", ColorEnum.BLUE);
    }

    @Test
    void testInitialization() {
        assertEquals('B', tile.getLetter());
        assertEquals(0, tile.getCountUpperArrow());
        assertEquals(1, tile.getCountLowerArrow());
        assertNull(tile.getOccupant());
    }

    @Test
    void testOccupant() {
        tile.setOccupant(player);
        assertFalse(tile.getFreeOfferTile());
        assertEquals(player, tile.getOccupant());

        tile.setOccupant(null);
        assertTrue(tile.getFreeOfferTile());
        assertNull(tile.getOccupant());
    }

}
