package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.enums.IconEnum;
import it.polimi.ingsw.exceptions.InvalidPlayerActionException;
import it.polimi.ingsw.model.boardAndTiles.Board;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.buildings.BuildingFoodSet;
import it.polimi.ingsw.model.cards.tribe.characters.*;
import it.polimi.ingsw.model.decks.CardFactory;
import it.polimi.ingsw.model.game.GameConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BuildingFoodSetTest {
    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;

    private void addOneCompleteSet(Player p){
        new Hunter(EraEnum.I, 2, false).AddToPlayerTribe(p, board);
        new Artist(EraEnum.I, 2).AddToPlayerTribe(p, board);
        new Builder(EraEnum.I, 2, 3, 2).AddToPlayerTribe(p, board);
        new Inventor(EraEnum.I, 2, IconEnum.BOWL).AddToPlayerTribe(p, board);
        new Shaman(EraEnum.I, 2, 2).AddToPlayerTribe(p, board);
        new Gatherer(EraEnum.I, 2).AddToPlayerTribe(p, board);
        player.updateCompletedSetsCount();//Updating set count
    }

    private BuildingCard addBuildingToPlayer(Player p){
        BuildingCard b = new BuildingCard(EraEnum.I, 0, 0, new BuildingFoodSet());
        player.addBuildingCard(b);
        return b;
    }

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
    @Test//Checking to see if the building gives food when the player acquires it
    void firstCall_noFood(){
        int foodBefore = player.getFood();
        BuildingFoodSet b = new BuildingFoodSet();
        b.applyOnCardAdded(player, board);
        assertEquals(foodBefore, player.getFood(),
                "The building shouldn't have given the player any food, as it's just been acquired");
    }
    @Test //Checking to see if the building gives the player food if he has no set
    void secondCall_noFood(){
        BuildingFoodSet b = new BuildingFoodSet();
        int foodBefore = player.getFood();
        b.applyOnCardAdded(player, board);
        assertEquals(foodBefore, player.getFood(),
                "The building shouldn't have given the player any food since he has no completed set");
    }
    @Test //Checking to see if the building give the player any food if he has just completed a set
    void secondCall_5Food(){
        BuildingCard b = addBuildingToPlayer(player);
        player.addCharacterCard(new Hunter(EraEnum.I, 2, false), board);
        //This call simulates the logic: everytime a card is picked, applyOnCardAdded is called
        //So, we are saying: "we've added the building to the deck"; no food should be given

        int foodBefore = player.getFood();
        addOneCompleteSet(player);
        b.getEffect().applyOnCardAdded(player, board);

        assertEquals(foodBefore + 5, player.getFood(),
                "The building should have given the player 5 food");
    }
    @Test//Checking to see if the building give the player 10 food if he has just completed 2 sets
    void twoCompletedSets(){
        BuildingCard b = addBuildingToPlayer(player);
        player.addCharacterCard(new Hunter(EraEnum.I, 2, false), board);

        int foodBefore = player.getFood();
        addOneCompleteSet(player);
        addOneCompleteSet(player);
        b.getEffect().applyOnCardAdded(player, board);
        assertEquals(foodBefore + 10, player.getFood(),
                "The building should have given the player 10 food");
    }
    @Test//Checking to see if the building gives food if a player already has a set and hasn't completed a new one
    void noNewSet(){
        BuildingFoodSet b = new BuildingFoodSet();
        addOneCompleteSet(player);
        b.applyOnCardAdded(player, board);
        int foodBefore = player.getFood();
        b.applyOnCardAdded(player, board);
        assertEquals(foodBefore, player.getFood(),
                "The player shouldn't have received any food since he hasn't completed any new set");
    }
    @Test//Checking to see if other applymethods modify something
    void otherApply_noChange (){
        int ppBefore = player.getPP();
        int foodBefore = player.getFood();
        BuildingFoodSet b = new BuildingFoodSet();
        try{
            b.applyEndTurn(player, board, -1, true);
        }catch (InvalidPlayerActionException e){
            return;
        }
        b.applyEndGame(player, board);
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
        BuildingCard c = new BuildingCard(EraEnum.I, 3, 2, new BuildingFoodSet());
        String result = c.toString();

        assertTrue(result.contains("I"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("BuildingFoodSet"));
    }
    @Test
    void toStringTestMessage(){
        BuildingCard c = new BuildingCard(EraEnum.I, 3, 2, new BuildingFoodSet());
        String expected = "BuildingCard with effect: BuildingFoodSet and" +
                "\nEra : I" +
                "\nFood cost : 3" +
                "\nPrestige point earned : 2";

        assertEquals(expected, c.toString());
    }
}

