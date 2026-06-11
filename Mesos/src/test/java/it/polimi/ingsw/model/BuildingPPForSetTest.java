/**
 * @author Giuse
 */
package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.enums.IconEnum;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.buildings.BuildingPPForSet;
import it.polimi.ingsw.model.cards.tribe.characters.*;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.game.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BuildingPPForSetTest {
    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;
    private BuildingPPForSet effect;

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
        config = GameConfig.create(2);
        board  = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("TestPlayer", ColorEnum.BLUE);
        effect = new BuildingPPForSet();
    }

    @Test//Checking to see if the building gives the player any prestige points if he has no completed set
    void noSets() {
        int ppBefore = player.getPP();
        effect.applyEndGame(player, board);
        assertEquals(ppBefore, player.getPP(),
                "The player shouldn't have received any prestige point because he has no completed set");
    }
    @Test//Checking to see if the building gives the player any prestige points if he has 1 completed set
    void oneSet() {
        addOneCompleteSet(player);
        int ppBefore = player.getPP();
        effect.applyEndGame(player, board);
        assertEquals(ppBefore + 6, player.getPP(),
                "The player should have received 6 prestige points because he has one completed set");
    }
    @Test
    void multipleSets() {
        addOneCompleteSet(player);
        addOneCompleteSet(player);
        int ppBefore = player.getPP();
        effect.applyEndGame(player, board);
        assertEquals(ppBefore + 12, player.getPP(),
                "The player should have received 12 prestige points because he has two completed set");
    }
    @Test//Checking to see if the building gives the player any prestige points if he has just one incomplete set
    void incompleteSet() {
        // Only 5 out of 6
        new Hunter(EraEnum.I, 0, false).AddToPlayerTribe(player, board);
        new Artist(EraEnum.I, 0).AddToPlayerTribe(player, board);
        new Builder(EraEnum.I, 0, 0, 0).AddToPlayerTribe(player, board);
        new Inventor(EraEnum.I, 0, IconEnum.values()[0]).AddToPlayerTribe(player, board);
        new Shaman(EraEnum.I, 0, 1).AddToPlayerTribe(player, board);
        int ppBefore = player.getPP();
        effect.applyEndGame(player, board);
        assertEquals(ppBefore, player.getPP(),
                "The player shouldn't have received any prestige points because he has just one incomplete set");
    }
    @Test//Checking to see if the building gives the player any food
    void doesNotChangeFood() {
        addOneCompleteSet(player);
        int foodBefore = player.getFood();
        effect.applyEndGame(player, board);
        assertEquals(foodBefore, player.getFood(),
                "The player should have not received any food");
    }
    @Test
    void toStringTestValues(){
        BuildingCard c = new BuildingCard(EraEnum.I, 3, 2, new BuildingPPForSet());
        String result = c.toString();

        assertTrue(result.contains("I"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("BuildingPPForSet"));
    }
}