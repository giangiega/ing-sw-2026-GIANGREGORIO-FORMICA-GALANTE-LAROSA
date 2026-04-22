package it.polimi.ingsw.model;
import it.polimi.ingsw.exceptions.EmptyDeckException;
import it.polimi.ingsw.enums.EraEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class BuildingDeckTest {
    private BuildingDeck buildingDeck;
    private List<BuildingCard> cards;

    @BeforeEach
    void setUp() {
        cards = new ArrayList<>();
        BuildingCard c1 = new BuildingCard(EraEnum.I, 5, 3, null);
        cards.add(c1);
        BuildingCard c2 = new BuildingCard(EraEnum.I, 5, 3, null);
        cards.add(c2);
        BuildingCard c3 = new BuildingCard(EraEnum.I, 5, 3, null);
        cards.add(c3);
        buildingDeck = new BuildingDeck(EraEnum.I, cards);
    }

    @Test
    void testSizeFirstCard() {
        assertEquals(3, buildingDeck.size());
        BuildingCard drawnCard = buildingDeck.getFirstCard();
        assertEquals(2, buildingDeck.size());
    }

    @Test
    void testEmptyDeckException() {
        BuildingDeck emptyDeck = new BuildingDeck(EraEnum.I, new ArrayList<>());
        Assertions.assertThrows(EmptyDeckException.class, () -> {
            emptyDeck.getFirstCard();
        }, "Empty buildingDeck throws exception");
    }

    @Test
    void testClearDeck() {
        assertFalse(buildingDeck.isEmpty());

        buildingDeck.getFirstCard();
        buildingDeck.getFirstCard();
        buildingDeck.getFirstCard();

        assertTrue(buildingDeck.isEmpty());
    }
}
