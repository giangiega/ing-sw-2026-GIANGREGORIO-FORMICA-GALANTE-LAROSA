package it.polimi.ingsw;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerInteractionTest {

    @Test
    void testAddCharacterCard() {
        Player p = new Player("Riccardo" , ColorEnum.BLUE);
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

        p.addCharacterCard(c1, b); p.addCharacterCard(c2, b); p.addCharacterCard(c3, b);
        p.addCharacterCard(c4, b); p.addCharacterCard(c5, b); p.addCharacterCard(c6, b);
        p.addCharacterCard(c7, b); p.addCharacterCard(c8, b); p.addCharacterCard(c9, b);
        p.addCharacterCard(c10, b); p.addCharacterCard(c11, b); p.addCharacterCard(c12, b);

        for(CharacterEnum ce : CharacterEnum.values()) {
            assertNotNull(p.getCharacterByType(ce));
            assertEquals(2, p.getCharacterByType(ce).size());
        }

        assertEquals(4, p.getTotalFoodDiscountBuilder());
        assertEquals(2, p.getFood());
        assertEquals(1, p.getDistinctInventorsIcon());
        assertEquals(5, p.getTotalStarCount());

    }
}
