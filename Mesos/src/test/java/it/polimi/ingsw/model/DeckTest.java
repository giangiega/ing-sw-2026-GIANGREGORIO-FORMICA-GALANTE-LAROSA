package it.polimi.ingsw.model;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.cards.tribe.characters.Artist;
import it.polimi.ingsw.model.cards.tribe.characters.Builder;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.model.decks.Deck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {
    private Deck deck;
    private List<TribeCard> cards;

    @BeforeEach
    void setUp() {
        cards = new ArrayList<>();
        TribeCard c1 = new Artist(EraEnum.I, 2);
        cards.add(c1);
        TribeCard c2 = new Artist(EraEnum.I, 2);
        cards.add(c2);
        TribeCard c3 = new Builder(EraEnum.I, 2, 1, 2);
        cards.add(c3);
        deck = new Deck(cards);
    }

    @Test
    void testSizeFirstCard() {
        assertEquals(3, deck.size());
        TribeCard drawnCard = deck.getFirstCard();
        assertEquals(2, deck.size());
    }

    @Test
    void testEmptyDeckException() {
        Deck emptyDeck = new Deck(new ArrayList<>());
        assertThrows(EmptyDeckException.class, () -> {
            emptyDeck.getFirstCard();
        }, "Empty deck throws exception");
    }

    @Test
    void testClearDeck() {
        assertFalse(deck.isEmpty());

        deck.getFirstCard();
        deck.getFirstCard();
        deck.getFirstCard();

        assertTrue(deck.isEmpty());
    }
}
