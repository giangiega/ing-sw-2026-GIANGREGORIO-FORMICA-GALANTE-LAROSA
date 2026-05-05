package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.enums.IconEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.buildings.BuildingDeck;
import it.polimi.ingsw.model.cards.buildings.BuildingFinal25PP;
import it.polimi.ingsw.model.cards.buildings.BuildingFoodSet;
import it.polimi.ingsw.model.cards.tribe.*;
import it.polimi.ingsw.model.cards.tribe.characters.*;
import it.polimi.ingsw.model.cards.tribe.events.EventCard;
import it.polimi.ingsw.model.cards.tribe.events.EventSustenance;
import it.polimi.ingsw.model.decks.Deck;
import it.polimi.ingsw.model.game.GameConfig;
import it.polimi.ingsw.model.game.GameConfig2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class OfferTileIntegrationTest {
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
        EventCard eventCard = new EventSustenance(EraEnum.I, false, 2);
        List<TribeCard> cards = new ArrayList<>();
        List<BuildingCard> buildingCards = new ArrayList<>();

        cards.add(c1); cards.add(c2); cards.add(c3); cards.add(c4);
        cards.add(c5); cards.add(c6); cards.add(c7); cards.add(c8);
        cards.add(c9); cards.add(c10); cards.add(c11); cards.add(c12); cards.add(eventCard);

        Deck td = new Deck(cards);

        BuildingCard b1 = new BuildingCard(EraEnum.I, 3, 5, new BuildingFoodSet());
        BuildingCard b2 = new BuildingCard(EraEnum.I, 5, 10, new BuildingFinal25PP());
        buildingCards.add(b1); buildingCards.add(b2);

        BuildingDeck bd1 = new BuildingDeck(EraEnum.I, buildingCards);
        BuildingDeck bd2 = new BuildingDeck(EraEnum.II, buildingCards);
        BuildingDeck bd3 = new BuildingDeck(EraEnum.III, buildingCards);
        board = new Board(gc, td, bd1, bd2, bd3);
    }

    @Test
    void testPlayerMoveBuilding() {
        player.gainFood(5);
        List<Integer> upperBuildingsToBuy = new ArrayList<>();
        upperBuildingsToBuy.add(0);

        assertDoesNotThrow(() -> {
            tile.playerMove(player,board, new ArrayList<>(), new ArrayList<>(), upperBuildingsToBuy , new ArrayList<>());
        });

        assertEquals(2, player.getFood());
        assertEquals(1, player.getBuildingCards().size());
        assertEquals(1, board.getBuildingUpperRow().size());
    }

    @Test
    void testPlayerMove_SuccessfulCharacterPick() {
        int initialUpperTribeCount = board.getUpperRow().size();
        int initialLowerTribeCount = board.getLowerRow().size();

        List<Integer> upperCardsToPick = new ArrayList<>();
        upperCardsToPick.add(0);

        List<Integer> lowerCardsToPick = new ArrayList<>();
        lowerCardsToPick.add(0);

        assertDoesNotThrow(() -> {
            tile.playerMove(player, board, upperCardsToPick, lowerCardsToPick, new ArrayList<>(), new ArrayList<>());
        });

        assertEquals(initialUpperTribeCount - 1, board.getUpperRow().size());
        assertEquals(initialLowerTribeCount - 1, board.getLowerRow().size());

        assertEquals(2, player.getTotalCharactersCount());
    }

}
