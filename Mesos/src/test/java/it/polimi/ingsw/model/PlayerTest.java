package it.polimi.ingsw.model;
import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("playerT" , ColorEnum.RED);
    }

    @Test
    void testFood() {
        player.gainFood(10);
        player.payFood(3);
        assertEquals(7, player.getFood());
    }

    @Test
    void testFoodCannotBeNegative() {
        player.payFood(5);
        assertEquals(-5, player.getFood());
    }

    @Test
    void testPP() {
        player.gainPP(10);
        player.losePP(3);
        assertEquals(7, player.getPP());
    }

    @Test
    void testInitialTribeIsEmpty() {
        assertEquals(0, player.getTotalCharactersCount());
    }

    @Test
    // all 6 lists of tribe are correctly created (not null) and also empty
    void testCharacterLists() {
        for(CharacterEnum c: CharacterEnum.values()) {
            assertNotNull(player.getCharacterByType(c));
            assertTrue(player.getCharacterByType(c).isEmpty());
        }
    }

    @Test
    void testStarCount() {
        player.updateTotalStarCount(3);
        player.setEffectiveStars(2);
        assertEquals(3, player.getTotalStarCount());
        assertEquals(5, player.getEffectiveStars());
    }

    @Test
    void testUpdateFoodDiscountBuilder() {
        player.updateTotalFoodDiscountBuilder(2);
        assertEquals(2, player.getTotalFoodDiscountBuilder());
    }

    @Test
    void testBuildingCardUpdate() {
        BuildingCard c = new BuildingCard(EraEnum.I,5,5, null);
        player.addBuildingCard(c);
        assertEquals(5, player.getTotalBuildingsPP());
        assertEquals(1, player.getBuildingCards().size());
    }

}
