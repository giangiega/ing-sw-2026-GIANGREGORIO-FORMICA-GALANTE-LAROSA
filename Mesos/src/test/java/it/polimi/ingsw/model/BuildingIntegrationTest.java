package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.enums.IconEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
public class BuildingIntegrationTest {

    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;
    private Player player2;

    private void addOneCompleteSet(Player p) {
        Hunter hunter = new Hunter(EraEnum.I, 2, false);
        player.addCharacterCard(hunter, board);
        Artist artist = new Artist(EraEnum.I, 2);
        player.addCharacterCard(artist, board);
        Builder builder = new Builder(EraEnum.I, 2, 0, 0);
        player.addCharacterCard(builder, board);
        Inventor inventor = new Inventor(EraEnum.I, 2, IconEnum.BOAT);
        player.addCharacterCard(inventor, board);
        Shaman shaman = new Shaman(EraEnum.I, 2, 1);
        player.addCharacterCard(shaman, board);
        Gatherer gatherer = new  Gatherer(EraEnum.I, 2);
        player.addCharacterCard(gatherer, board);
    }

    @BeforeEach
    void setUp() {
        config  = GameConfig.create(2);
        board = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("P1",  ColorEnum.BLUE);
        player2 = new Player("P2", ColorEnum.BLUE);
    }
    // ── BuildingFoodSet: interazione con addBuildingCard e addCharacterCard ──
    @Test//Checking to see if BuildingFoodSet gives any food on acquisition
    void buildingFoodSet_acquisitionViaAddBuildingCard_noImmediateFood() {
        BuildingCard card = new BuildingCard(EraEnum.I, 0, 0, new BuildingFoodSet());
        int foodBefore = player.getFood();
        player.addBuildingCard(card);
        assertEquals(foodBefore, player.getFood(),
                "BuildingFoodSet shouldn't give the player any food");
    }
    @Test//Checking to see if BuildingFoodSet gives any food after acquisition
    void buildingFoodSet_characterAddedAfterAcquisition_gives5Food() {
        BuildingCard card = new BuildingCard(EraEnum.I, 0, 0, new BuildingFoodSet());
        player.addBuildingCard(card);
        int foodBefore = player.getFood();
        addOneCompleteSet(player);
        assertEquals(foodBefore + 5, player.getFood(),
                "BuildingFoodSet should dive the player 5 food");
    }
    @Test//Checking for multiple sets
    void buildingFoodSet_multipleSetsCompleted() {
        BuildingCard card = new BuildingCard(EraEnum.I, 0, 0, new BuildingFoodSet());
        player.addBuildingCard(card);
        addOneCompleteSet(player); // +5
        int foodBefore = player.getFood();
        addOneCompleteSet(player); // +5
        assertEquals(foodBefore + 5, player.getFood(),
                "BuildingFoodSet should give the player 5 food after the second set is completed");
    }

    // ── BuildingBonusSameInventors: interazione con addCharacterCard ──────────
    @Test//Checking to see if BuildingBonusSameInventors gives any food on acquisition
    void buildingBonusSameInventors_acquisitionNoFood() {
        BuildingCard card = new BuildingCard(EraEnum.I, 0, 0, new BuildingBonusSameInventors());
        int foodBefore = player.getFood();
        player.addBuildingCard(card);
        assertEquals(foodBefore, player.getFood(),
                "BuildingFoodSet shouldn't give the player any food");
    }
    @Test//Checking to see if BuildingBonusSameInventors gives any food after acquisition
    void buildingBonusSameInventors_pairCompletedAfterAcquisition() {
        BuildingCard card = new BuildingCard(EraEnum.I, 0, 0, new BuildingBonusSameInventors());
        player.addBuildingCard(card);
        Inventor inventor1 = new Inventor(EraEnum.I, 2, IconEnum.BOAT);
        player.addCharacterCard(inventor1, board);
        int foodBefore = player.getFood();
        Inventor inventor2 = new Inventor(EraEnum.I, 2, IconEnum.BOAT);
        player.addCharacterCard(inventor1, board);
        assertEquals(foodBefore + 3, player.getFood(),
                "BuildingFoodSet should dive the player 5 food");
    }

    @Test//Checking interaction: BuildingBonusHunt-->EventHunt
    void buildingBonusHunt_eventHuntFired_extraFoodAndPP() {
        BuildingCard card = new BuildingCard(EraEnum.I, 0, 0, new BuildingBonusHunt());
        player.addBuildingCard(card);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);

        int foodBefore = player.getFood();
        int ppBefore = player.getPP();

        for (BuildingCard b : player.getBuildingCards())
            b.getEffect().applyEventHunt(player, board);

        assertEquals(foodBefore + 2, player.getFood(),
                "The player should have received 2 food");
        assertEquals(ppBefore + 2, player.getPP(),
                "The player should have received 2 prestige points");
    }
    @Test//Checking interaction: BuildingDiscountFood-->EventSustenance
    void buildingDiscountFood_sustainanceEvent_reducesNetCost() {
        BuildingCard card = new BuildingCard(EraEnum.I, 0, 0,
                new BuildingDiscountFood(CharacterEnum.ARTIST));
        player.addBuildingCard(card);
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);
        new Artist(EraEnum.I, 2).AddToPlayerTribe(player, board);

        int foodBefore = player.getFood();

        for (BuildingCard b : player.getBuildingCards())
            b.getEffect().applyEventSustenance(player, board);

        assertEquals(foodBefore + 2, player.getFood(),
                "The player should have received 2 (2 * 1) food as a discount");
    }
    @Test//Checking interaction: BuildingFinale25PP-->calculateFinalScores
    void buildingFinal25PP_endGame_25PPAddedBeforeScoreCalculation() {
        BuildingCard card = new BuildingCard(EraEnum.I, 0, 0, new BuildingFinal25PP());
        player.addBuildingCard(card);
        int ppBefore = player.getPP();

        // Simulation of end-game phase di Game.calculateFinalScores()
        for (BuildingCard b : player.getBuildingCards())
            b.getEffect().applyEndGame(player, board);

        assertEquals(ppBefore + 25, player.getPP(),
                "The player should have received 25 prestige points");
    }
    @Test//Checking interaction: BuildingDoubleBuilderPP-->calculateFinalScores
    void buildingDoubleBuilderPP_endGame_doublesBuilderReward() {
        BuildingCard card = new BuildingCard(EraEnum.I, 0, 0, new BuildingDoubleBuilderPP());
        player.addBuildingCard(card);
        new Builder(EraEnum.I, 2, 0, 4).AddToPlayerTribe(player, board);
        int ppBefore = player.getPP();

        for (BuildingCard b : player.getBuildingCards())
            b.getEffect().applyEndGame(player, board);

        assertEquals(ppBefore + 4, player.getPP(),
                "The player should have received 4 prestige points");
    }
    @Test//Checking interaction: BuildingBonusTotem-->Game.unplaceTotem
    void buildingBonusTotem_slotWithFoodBonus() throws Exception {
        BuildingCard card = new BuildingCard(EraEnum.I, 0, 0, new BuildingBonusTotem());
        player.addBuildingCard(card);
        int foodBefore = player.getFood();

        // calls Game.unplaceTotem on bonus slot
        for (BuildingCard b : player.getBuildingCards())
            if (b.getEffect() instanceof BuildingBonusTotem)
                b.getEffect().applyEndTurn(player, board, -1, false);

        assertEquals(foodBefore + 1, player.getFood(),
                "The player should have received 1 food because the player chose the food tale");
    }
    @Test//BuildingBonusForCharacterType + BuildingPPForSet
    void twoEndGameBuildings_bothApplied() {
        player.addBuildingCard(new BuildingCard(EraEnum.I, 0, 0,
                new BuildingBonusForCharacterType(2, CharacterEnum.HUNTER)));
        player.addBuildingCard(new BuildingCard(EraEnum.I, 0, 0,
                new BuildingPPForSet()));
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(player, board);
        addOneCompleteSet(player);
        int ppBefore = player.getPP();

        for (BuildingCard b : player.getBuildingCards())
            b.getEffect().applyEndGame(player, board);
        assertEquals(ppBefore + 12, player.getPP(),
                "The player should have received 10 prestige points: 3 hunters × 2 = 6 PP, 1 set × 6 = 6 PP ");
    }
    @Test//BuildingSaveShamanPP + BuildingBonusDoubleShamanPP: EventShamanRitual
    void saveShamanPP_loserDoesNotLosePP() {
        Player winner = new Player("Winner", ColorEnum.BLUE);
        Player loser = player;

        new Shaman(EraEnum.I, 2, 3).AddToPlayerTribe(winner, board);
        new Shaman(EraEnum.I, 2, 1).AddToPlayerTribe(loser, board);

        loser.addBuildingCard(new BuildingCard(EraEnum.I, 0, 0, new BuildingSaveShamanPP()));

        int ppLoserBefore = loser.getPP();
        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(winner, loser), board);
        assertEquals(ppLoserBefore, loser.getPP(),
                "The player should have received 0 prestige points: losePP(2) then gainPP(2) from SaveShamanPP ");
    }
    @Test//Checking for double point for the winner
    void doubleShamanPP() {
        Player winner = player;
        Player loser  = player2;

        new Shaman(EraEnum.I, 2, 3).AddToPlayerTribe(winner, board);
        new Shaman(EraEnum.I, 2, 1).AddToPlayerTribe(loser,  board);

        winner.addBuildingCard(new BuildingCard(EraEnum.I, 0, 0, new BuildingBonusDoubleShamanPP()));

        int ppWinnerBefore = winner.getPP();
        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(winner, loser), board);
        assertEquals(ppWinnerBefore + 8, winner.getPP(),
                "The player should have received 8 prestige points: gainPP(4) base + gainPP(4) da DoubleShamanPP ");
    }
    @Test//Checking to see if bonusStarShaman has any effect on the event
    void bonusStarShaman_changesOutcomeOfEvent() {
        Player p1 = player;
        Player p2 = player2;

        new Shaman(EraEnum.I, 2, 1).AddToPlayerTribe(p1, board);
        new Shaman(EraEnum.I, 2, 2).AddToPlayerTribe(p2, board);
        p1.addBuildingCard(new BuildingCard(EraEnum.I, 0, 0, new BuildingBonusStarShaman()));

        int pp1Before = p1.getPP();
        int pp2Before = p2.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(p1, p2), board);
        assertEquals(pp1Before + 4, p1.getPP(),
                "Player 1 won the event thanks to the extra stars: he should have received 4 prestige points");
        assertEquals(pp2Before - 2, p2.getPP(),
                "Player 2 lost the event: he should have lost 2 prestige points");
    }
    @Test//BuildingCard.getCost discount works
    void buildingCard_getCost_builderDiscountApplied_() {
        new Builder(EraEnum.I, 2, 2, 0).AddToPlayerTribe(player, board);
        BuildingCard target = new BuildingCard(EraEnum.I, 5, 3, new BuildingFinal25PP());
        assertEquals(3, target.getCost(player),
                "The player should have lost only 3 food since he has a two food discount");
    }
    @Test//BuildingCard.getCost  multiple discount works
    void buildingCard_getCost_multipleBuilders() {
        new Builder(EraEnum.I, 2, 2, 0).AddToPlayerTribe(player, board);
        new Builder(EraEnum.I, 2, 1, 0).AddToPlayerTribe(player, board);
        BuildingCard target = new BuildingCard(EraEnum.I, 5, 3, new BuildingFinal25PP());
        assertEquals(2, target.getCost(player),
                "The player should have lost only 2 food since he has a 3 (2 + 1) food discount");
    }
}
