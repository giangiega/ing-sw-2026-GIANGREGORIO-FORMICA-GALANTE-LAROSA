package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import it.polimi.ingsw.enums.IconEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BuildingBonusSameInventorsTest {
    private final CardFactory cf = new CardFactory();
    private GameConfig config;
    private Board board;
    private Player player;

    private void addOneInventor(Player p, IconEnum icon){
        new Inventor(EraEnum.I, 2, icon).AddToPlayerTribe(p, board);
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
        BuildingCard b = new BuildingCard(EraEnum.I, 0, 0, new BuildingBonusSameInventors());
        player.addBuildingCard(b);
    }
    @Test//Checking to see if the building gives food when the player acquires it
    void firstCall_noFood(){
        int foodBefore = player.getFood();
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        assertEquals(foodBefore, player.getFood(),
                "The building shouldn't have given the player any food, as it's just been acquired");
    }
    @Test//Checking to see if the building gives food to player if he has no couples
    void noPairs(){
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        int foodBefore = player.getFood();
        addOneInventor(player, IconEnum.BOWL);
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        assertEquals(foodBefore, player.getFood(),
                "The building shouldn't have given the player any food since he has no couples");
    }
    @Test//Checking to see if the building gives to the player 3 food if he has a couple
    void oneCouple(){
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        int foodBefore = player.getFood();
        addOneInventor(player, IconEnum.BOWL);
        addOneInventor(player, IconEnum.BOWL);
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        assertEquals(foodBefore + 3, player.getFood(),
                "The building should have given the player 3 food since he has a couple");
    }
    @Test//Checking to see if the building gives the player 6 food for 2 couples
    void twoCouples(){
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        int foodBefore = player.getFood();
        addOneInventor(player, IconEnum.BOWL);
        addOneInventor(player, IconEnum.BOWL);
        addOneInventor(player, IconEnum.BOAT);
        addOneInventor(player, IconEnum.BOAT);
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        assertEquals(foodBefore + 6, player.getFood(),
                "The building should have given the player 6 food since he has 2 couples");
    }
    @Test//Checking to see if the building gives the player any food if he already has a couple before the acquisition
    void coupleAAtAcquisition_noFood(){
        addOneInventor(player, IconEnum.BOWL);
        addOneInventor(player, IconEnum.BOWL);
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        int foodBefore = player.getFood();
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        assertEquals(foodBefore, player.getFood(),
                "The building shouldn't have given the player any food since he hasn't acquired any new couples after he acquired the building");
    }
    @Test//Checking for overlapping couples
    void oneOverlappingCouple(){
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        addOneInventor(player, IconEnum.BOWL);
        addOneInventor(player, IconEnum.BOWL);
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        int foodBefore = player.getFood();
        addOneInventor(player, IconEnum.BOWL);
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        assertEquals(foodBefore, player.getFood(),
                "The building shouldn't have given the player any food since he has not completed any new couples");
    }
    @Test//Checking for overlapping couples 2
    void twoOverlappingCouple(){
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        addOneInventor(player, IconEnum.BOWL);
        addOneInventor(player, IconEnum.BOWL);
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        addOneInventor(player, IconEnum.BOWL);
        int foodBefore = player.getFood();
        addOneInventor(player, IconEnum.BOWL);
        player.getBuildingCards().get(0).getEffect().applyOnCardAdded(player, board);
        assertEquals(foodBefore + 3, player.getFood(),
                "The building should have given the player 3 food since he has completed a new couple");
    }
    @Test
    void toStringTestValues(){
        BuildingCard c = new BuildingCard(EraEnum.I, 3, 2, new BuildingBonusSameInventors());
        String result = c.toString();

        assertTrue(result.contains("I"));
        assertTrue(result.contains("3"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("BuildingBonusSameInventors"));
    }
    @Test
    void toStringTestMessage(){
        BuildingCard c = new BuildingCard(EraEnum.I, 3, 2, new BuildingBonusSameInventors());
        String expected = "BuildingCard with effect: BuildingBonusSameInventors and" +
                "\nEra : I" +
                "\nFood cost : 3" +
                "\nPrestige point earned : 2";

        assertEquals(expected, c.toString());
    }
}