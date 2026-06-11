/**
 * @author Giuse
 */
package it.polimi.ingsw.model;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.exceptions.InvalidPlayerActionException;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.buildings.BuildingFinal25PP;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.game.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BuildingFinal25PPTest {
    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;

    @BeforeEach
    public void setUp(){
        config = GameConfig.create(2);
        board = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
        player = new Player("TestPlayer", ColorEnum.BLUE);
    }
    @Test//Checking to see if the building has given 25 pp to the player
    void applyEndGame_gives25PP(){
        int ppBefore = player.getPP();
        new BuildingFinal25PP().applyEndGame(player, board);
        assertEquals(ppBefore + 25, player.getPP(),
                "The building should have given 25 prestige points to the player");
    }
    @Test//Checking for multiple calls
    void applyEndGame_calledTwice(){
        int ppBefore = player.getPP();
        BuildingFinal25PP b = new BuildingFinal25PP();
        b.applyEndGame(player, board);
        b.applyEndGame(player, board);
        assertEquals(ppBefore + 50, player.getPP(),
                "The building should have given 50 prestige points to the player, because applyEndGame" +
                        "was called twice");
    }
    @Test//Checking to see if other applyMethod modify something
    void otherApply_noChange (){
        int ppBefore = player.getPP();
        int foodBefore = player.getFood();
        BuildingFinal25PP b = new BuildingFinal25PP();
        b.applyOnCardAdded(player, board);
        try{
            b.applyEndTurn(player, board, -1, true);
        }catch (InvalidPlayerActionException e){
            return;
        }
        b.applyEventHunt(player, board);
        b.applyEventCavePainting(player, board);
        b.applyEventSustenance(player, board);
        b.applyEventShamanBonusStars(player, board);
        b.applyEventShamanBonusStars(player, board);
        assertEquals(foodBefore, player.getFood(),
                "Calling other apply methods shouldn't have modified the player's food");
        assertEquals(ppBefore, player.getPP(),
                "Calling other apply methods shouldn't have modified the player's prestige points");
    }
    @Test
    void toStringTestValues(){
        BuildingCard c = new BuildingCard(EraEnum.I, 3, 2, new BuildingFinal25PP());
        String result = c.toString();

        assertTrue(result.contains("I"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("BuildingFinal25PP"));
    }
}

