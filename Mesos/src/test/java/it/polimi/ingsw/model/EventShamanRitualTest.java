/**
 * @author Giuse
 */
package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.cards.buildings.BuildingBonusDoubleShamanPP;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.buildings.BuildingSaveShamanPP;
import it.polimi.ingsw.model.cards.tribe.events.EventShamanRitual;
import it.polimi.ingsw.model.cards.tribe.characters.Shaman;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.game.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EventShamanRitual compares getEffectiveStars() among all players.
 * We use Shaman.AddToPlayerTribe to set each player's star count
 * before calling resolve(), so no external state-setter is needed.
 * We are assuming the players have no buildings
 */
class EventShamanRitualTest {

    private final CardFactory cf = new CardFactory();
    private Board board;
    private GameConfig config;

    /**
     * @param p : player
     * @param stars shaman's stars
     * This method adds a shaman with 'stars' stars. It basically sets the player's starCount
     */
    private void addStars(Player p, int stars) {
        new Shaman(EraEnum.I, 2, stars).AddToPlayerTribe(p, board);
    }
    /**
     * @param p  player
     * This method adds BuildingBonusDoubleShamanPP to the player's deck
     */
    private void addDoubleShamanBuilding(Player p) {
        BuildingCard building = new BuildingCard(
                EraEnum.I, 0, 0, new BuildingBonusDoubleShamanPP());
        p.addBuildingCard(building);
    }
    /**
     * @param p : player
     * This method adds BuildingBonusSaveShamanPP to the player's deck
     */
    private void addSaveShamanBuilding(Player p) {
        BuildingCard building = new BuildingCard(
                EraEnum.I, 0, 0, new BuildingSaveShamanPP());
        p.addBuildingCard(building);
    }
    @BeforeEach
    void setUp() {
        config = GameConfig.create(2);
        board = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
    }
    @Test//Checking to see if the event has the right properties: gainedPP
    void testGetGainedPP() {
        assertEquals(4, new EventShamanRitual(EraEnum.I, false, 4, 2).getGainedPP(),
                "getGainedPP() should return correct value");
    }

    @Test//Checking to see if the event has the right properties: lostPP
    void testGetLostPP() {
        assertEquals(2, new EventShamanRitual(EraEnum.I,false,4,2).getLostPP(),
                "getLostPP() should return correct value");
    }
    @Test//Checking to see if the winner gets gainedPP prestige points
    void testResolve_winnerGainsPP() {
        Player winner = new Player("Winner", ColorEnum.BLUE);
        Player loser = new Player("Loser", ColorEnum.RED);
        addStars(winner, 5);
        addStars(loser, 1);
        int ppWinnerBefore = winner.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(winner, loser), board);

        assertEquals(ppWinnerBefore + 4, winner.getPP(),
                "The winner should have received gainedPP prestige points");
    }

    @Test//Checking to see if the loser loses lostPP prestige points
    void testResolve_loserLosesPP() {
        Player winner = new Player("Winner", ColorEnum.PURPLE);
        Player loser = new Player("Loser", ColorEnum.WHITE);
        addStars(winner, 5);
        addStars(loser, 1);
        int ppLoserBefore = loser.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(winner, loser), board);

        assertEquals(ppLoserBefore - 2, loser.getPP(),
                "The loser should have lost lostPP prestige points" );
    }
    @Test//Checking to see if the class resolves the event in the right way with 3 players
    void testResolve_threePlayers() {
        Player winner = new Player("Winner", ColorEnum.YELLOW);
        Player middle = new Player("Middle", ColorEnum.BLUE);
        Player loser  = new Player("Loser",  ColorEnum.RED);
        addStars(winner, 6);
        addStars(middle, 3);
        addStars(loser, 1);
        int ppMiddleBefore = middle.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(winner, middle, loser), board);

        assertEquals(ppMiddleBefore, middle.getPP(),
                "The middle player should have the same PP after resolving the event because he hasn't won, nor lost");
    }
    @Test//Checking to see if the class resolves the event in the right way with 2 players: they tied
    void testResolve_Tie() {
        Player p1 = new Player("Riccardo", ColorEnum.PURPLE);
        Player p2 = new Player("Giuseppe", ColorEnum.WHITE);
        addStars(p1, 3);
        addStars(p2, 3);
        int pp1Before = p1.getPP();
        int pp2Before = p2.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(p1, p2), board);
        assertEquals(pp1Before + 4 - 2, p1.getPP(),
                "They both lost and won: at the end they both should have received 2 prestige points");
        assertEquals(pp2Before + 4 - 2, p2.getPP(),
                "They both lost and won: at the end they both should have received 2 prestige points");
    }
    @Test//Checking to see if the class resolves the event in the right way with 3 players: 2 won, 1 lost
    void testResolve_twoWinners_bothGainPP() {
        Player w1 = new Player("W1", ColorEnum.YELLOW);
        Player w2 = new Player("W2", ColorEnum.BLUE);
        Player loser = new Player("Loser", ColorEnum.RED);
        addStars(w1,5);
        addStars(w2,5);
        addStars(loser,1);
        int ppW1Before = w1.getPP();
        int ppW2Before = w2.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(w1, w2, loser), board);

        assertEquals(ppW1Before + 4, w1.getPP(),
                "The winner1 should have received gainedPP prestige points");
        assertEquals(ppW2Before + 4, w2.getPP(),
                "The winner2 should have received gainedPP prestige points");
    }
    /**
     * Now assuming they have building
     */
    @Test//Checking to see if the winner gets double the points if he has the right building
    void testResolve_winnerWithDoubleBuilding() {
        Player winner = new Player("Winner", ColorEnum.BLUE);
        Player loser = new Player("Loser", ColorEnum.RED);
        addStars(winner, 5);
        addStars(loser, 1);
        addDoubleShamanBuilding(winner);
        int ppBefore = winner.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(winner, loser), board);

        assertEquals(ppBefore + 8, winner.getPP(),
                "The winner should have received (4)gainedPP * 2 prestige points");
    }

    @Test//Checking to see if the winner three times the points if he has two right buildings
    void testResolve_winnerWithTwoDoubleBuildings() {
        Player winner = new Player("Winner", ColorEnum.PURPLE);
        Player loser = new Player("Loser", ColorEnum.WHITE);
        addStars(winner, 5);
        addStars(loser, 1);
        addDoubleShamanBuilding(winner);
        addDoubleShamanBuilding(winner);
        int ppBefore = winner.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(winner, loser), board);

        assertEquals(ppBefore + 12, winner.getPP(),
                "The winner should have received (4)gainedPP * 3 prestige points, because he has two double buildings");
    }

    @Test//Checking to assure that the loser doesn't get double points if he has the Double building
    void testResolve_loserWithDoubleBuilding_isNotTriggered() {
        Player winner = new Player("Winner", ColorEnum.YELLOW);
        Player loser = new Player("Loser", ColorEnum.BLUE);
        addStars(winner, 5);
        addStars(loser, 1);
        addDoubleShamanBuilding(loser);
        int ppLoserBefore = loser.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(winner, loser), board);

        assertEquals(ppLoserBefore - 2, loser.getPP(),
                "The loser should only have lost lostPP prestige points");
    }
    @Test//Checking to see if the loser gets his points back if he has the right building
    void testResolve_loserWithSaveBuilding() {
        Player winner = new Player("Winner", ColorEnum.RED);
        Player loser = new Player("Loser",  ColorEnum.PURPLE);
        addStars(winner, 5);
        addStars(loser, 1);
        addSaveShamanBuilding(loser);
        int ppLoserBefore = loser.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(winner, loser), board);

        assertEquals(ppLoserBefore, loser.getPP(),
                "The loser should have lost no prestige points, because he has the Save building");
    }

    @Test//Checking to assure that the winner doesn't get double points if he has the Save building
    void testResolve_winnerWithSaveBuilding_isNotTriggered() {
        Player winner = new Player("Winner", ColorEnum.WHITE);
        Player loser = new Player("Loser", ColorEnum.YELLOW);
        addStars(winner, 5);
        addStars(loser, 1);
        addSaveShamanBuilding(winner);
        int ppWinnerBefore = winner.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(winner, loser), board);

        assertEquals(ppWinnerBefore + 4, winner.getPP(),
                "The winner should only have received gainedPP prestige points");
    }
    @Test//Checking to see if the winner gets double points, and the loser gets his points back
    void testResolve_winnerDoubles_loserSaved() {
        Player winner = new Player("Winner", ColorEnum.BLUE);
        Player loser = new Player("Loser", ColorEnum.RED);
        addStars(winner, 5);
        addStars(loser, 1);
        addDoubleShamanBuilding(winner);
        addSaveShamanBuilding(loser);
        int ppWinnerBefore = winner.getPP();
        int ppLoserBefore = loser.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(winner, loser), board);

        assertEquals(ppWinnerBefore + 8, winner.getPP(),
                "The winner should have received (4)gainedPP * 2 prestige points");
        assertEquals(ppLoserBefore, loser.getPP(),
                "The loser should have lost no prestige points, because he has the Save building");
    }

    @Test//Checking for mixed cases
    void testResolve_allTied_winnerWithDoubleBuilding() {
        Player p1 = new Player("P1", ColorEnum.PURPLE);
        Player p2 = new Player("P2", ColorEnum.WHITE);
        addStars(p1, 3);
        addStars(p2, 3);
        addDoubleShamanBuilding(p1);
        int pp1Before = p1.getPP();
        int pp2Before = p2.getPP();

        new EventShamanRitual(EraEnum.I, false, 4, 2)
                .resolve(List.of(p1, p2), board);


        assertEquals(pp1Before + 6, p1.getPP(),
                "p1 is both a winner and a loser and has the Double building;" +
                        "he should have received +4 (gain) +4 (double building) -2 (lose) = net +6 prestige points");
        assertEquals(pp2Before + 2, p2.getPP(),
                "p2 is both a winner and a loser but has no building;" +
                        "he should have received net +4 -2 = +2 prestige points");
    }

}