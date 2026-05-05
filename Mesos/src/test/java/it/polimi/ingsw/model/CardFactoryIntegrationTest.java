package it.polimi.ingsw.model;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.buildings.BuildingDeck;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.model.cards.tribe.events.EventCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.decks.Deck;
import it.polimi.ingsw.model.game.GameConfig;
import it.polimi.ingsw.model.game.GameConfig2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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


