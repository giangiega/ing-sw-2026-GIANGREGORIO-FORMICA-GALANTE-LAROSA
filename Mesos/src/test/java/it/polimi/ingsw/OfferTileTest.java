package it.polimi.ingsw;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OfferTileTest {
    OfferTile tile;
    Player player;
    Board board;

    @BeforeEach
    void setUp() {
        tile = new OfferTile('B', 0, 1);
        player = new Player("Riccardo", ColorEnum.BLUE);
        GameConfig gc = new GameConfig2();
        CharacterCard c1 = new Artist(EraEnum.I, 2);
        CharacterCard c2 = new Artist(EraEnum.I, 2);
        CharacterCard c3 = new Builder(EraEnum.I, 2, 1, 2);
        CharacterCard c4 = new Builder(EraEnum.I, 2, 3, 3);
        CharacterCard c5 = new Gatherer(EraEnum.I, 2);
        CharacterCard c6 = new Gatherer(EraEnum.I, 2);
        CharacterCard c7 = new Hunter(EraEnum.I, 2, false);
        CharacterCard c8 = new Hunter(EraEnum.I, 2, true);
        CharacterCard c9 = new Inventor(EraEnum.I, 2, IconEnum.POINTER);
        CharacterCard c10 = new Inventor(EraEnum.I, 2, IconEnum.POINTER);
        CharacterCard c11 = new Shaman(EraEnum.I, 2, 2);
        CharacterCard c12 = new Shaman(EraEnum.I, 2, 3);
        List<TribeCard> cards = new ArrayList<>();
        List<BuildingCard> buildingCards = new ArrayList<>();

        cards.add(c1); cards.add(c2); cards.add(c3); cards.add(c4); cards.add(c5); cards.add(c6);
        cards.add(c7); cards.add(c8); cards.add(c9); cards.add(c10); cards.add(c11); cards.add(c12);

        Deck td = new Deck(cards);
        BuildingDeck bd1 = new BuildingDeck(EraEnum.I, buildingCards);
        BuildingDeck bd2 = new BuildingDeck(EraEnum.II, buildingCards);
        BuildingDeck bd3 = new BuildingDeck(EraEnum.III, buildingCards);
        Board b = new Board(gc, td, bd1, bd2, bd3);
    }

    @Test
    void testInitialization() {
        assertEquals('B', tile.getLetter());
        assertEquals(0, tile.getCountUpperArrow());
        assertEquals(1, tile.getCountLowerArrow());
        assertNull(tile.getOccupant());
    }

    void testOccupant() {
        tile.setOccupant(player);
        assertFalse(tile.getFreeOfferTile());
        assertEquals(player, tile.getOccupant());

        tile.setOccupant(null);
        assertTrue(tile.getFreeOfferTile());
        assertNull(tile.getOccupant());
    }

}
