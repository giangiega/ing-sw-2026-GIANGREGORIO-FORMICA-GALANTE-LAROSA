package it.polimi.ingsw;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class CardFactoryIntegrationTest {

    private CardFactory cardFactory;
    private GameConfig gc2;

    @BeforeEach
    void setUp() {
        cardFactory = new CardFactory();
        gc2 = new GameConfig2();
    }

    @Test
    void testExactlyTwoFinalEvents() {
        Deck tribeDeck = cardFactory.buildTribeDeck(gc2);
        List<EventCard> events = new ArrayList<>();
        int finalEventCounter = 0;

        while(!tribeDeck.isEmpty()) {
            TribeCard card = tribeDeck.getFirstCard();
            if(!card.isPickable())
                events.add((EventCard)card);
        }
        for(EventCard card : events) {
            if(card.isFinalEvent())
                finalEventCounter++;
        }
        assertEquals(2, finalEventCounter);
    }

    @Test
    void testPlayerFilterCharacterCard() {
        Deck tribeDeck = cardFactory.buildTribeDeck(gc2);

        while(!tribeDeck.isEmpty()) {
            TribeCard card = tribeDeck.getFirstCard();
            if(!card.isEventCard()) {
                CharacterCard characterCard = (CharacterCard) card;
                assertTrue(characterCard.getNumPlayers() <= 2);
            }

        }
    }

    @Test
    void testEraAndSizeBuildingDeck() {
        BuildingDeck buildingDeck = cardFactory.buildBuildingDeck(EraEnum.I, gc2);
        assertEquals(1, buildingDeck.size());
        assertEquals(EraEnum.I, buildingDeck.getEra());

        while(!buildingDeck.isEmpty()) {
            BuildingCard buildingCard = buildingDeck.getFirstCard();
            assertEquals(EraEnum.I, buildingCard.getEra());
        }
    }

}


