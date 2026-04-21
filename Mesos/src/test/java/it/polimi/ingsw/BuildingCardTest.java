package it.polimi.ingsw;
/**
 * @author Giuse
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BuildingCardTest {

    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;

    @BeforeEach
    void setUp() {
        config = GameConfig.create(2);
        board  = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("TestPlayer", ColorEnum.BLUE);
    }
    @Test//Checking to see if the getter  getBaseFC is okay
    void getBaseFC_returnsBaseFoodCost() {
        assertEquals(4, new BuildingCard(EraEnum.I, 4, 3, new BuildingFinal25PP()).getBaseFC());
    }
    @Test//Checking to see if the getter getBasePP is okay
    void getBasePP_returnsBasePrestigePoints() {
        assertEquals(3, new BuildingCard(EraEnum.I, 4, 3, new BuildingFinal25PP()).getBasePP());
    }
    @Test//Checking to see if the getter getEra is okay
    void getEra_returnsCorrectEra() {
        assertEquals(EraEnum.III,
                new BuildingCard(EraEnum.III, 4, 3, new BuildingFinal25PP()).getEra());
    }
    @Test//Checking to see if the getter getEffect is okay
    void getEffect_returnsCorrectEffect() {
        BuildingFinal25PP effect = new BuildingFinal25PP();
        assertSame(effect, new BuildingCard(EraEnum.I, 4, 3, effect).getEffect());
    }
    @Test//Checking to see if getCost returns the base cost if the player has no builder
    void getCost_noBuilders_returnsBaseCost() {
        assertEquals(4, new BuildingCard(EraEnum.I, 4, 3, new BuildingFinal25PP()).getCost(player),
                "The player has no builder: getCost should return the base cost");
    }
    @Test//Checking to see if getCost returns the updated cost if the player has one builder
    void getCost_oneBuilder() {
        new Builder(EraEnum.I, 0, 2, 0).AddToPlayerTribe(player, board);
        assertEquals(2, new BuildingCard(EraEnum.I, 4, 3, new BuildingFinal25PP()).getCost(player),
                "The player has one builder: getCost should return the updated cost");
    }
    @Test//Checking to see if the discount stops at 0
    void getCost_discountExceedsCost_returnsZero() {
        new Builder(EraEnum.I, 0, 5, 0).AddToPlayerTribe(player, board);
        assertEquals(0, new BuildingCard(EraEnum.I, 2, 3, new BuildingFinal25PP()).getCost(player),
                "The discount is greater than the cost of the building: getCost should return zero");
    }
    @Test
    void applyEffect_triggersApplyOnCardAdded() {
        BuildingFoodSet effect = new BuildingFoodSet();
        BuildingCard card = new BuildingCard(EraEnum.I, 0, 0, effect);

        // Passo 1: addBuildingCard chiama applyEffect → acquisizione (baseline = 0)
        player.addBuildingCard(card);

        // Passo 2: aggiungiamo i primi 5 tipi senza completare il set
        player.addCharacterCard(new Hunter(EraEnum.I, 2, false), board);
        player.addCharacterCard(new Artist(EraEnum.I, 2), board);
        player.addCharacterCard(new Builder(EraEnum.I, 2, 3, 2), board);
        player.addCharacterCard(new Inventor(EraEnum.I, 2, IconEnum.BOWL),  board);
        player.addCharacterCard(new Shaman(EraEnum.I, 2, 2), board);

        int foodBefore = player.getFood();

        // Passo 3: il Gatherer completa il set → addCharacterCard notifica il building
        // → BuildingFoodSet.applyOnCardAdded rileva completedSetsCount passato da 0 a 1
        // → il building dà 5 cibo
        player.addCharacterCard(new Gatherer(EraEnum.I, 2), board);

        assertEquals(foodBefore + 5, player.getFood(),
                "applyEffect should have triggered applyOnCardAdded, setting the acquisition " +
                        "baseline so that completing a set gives 5 food");
    }

}