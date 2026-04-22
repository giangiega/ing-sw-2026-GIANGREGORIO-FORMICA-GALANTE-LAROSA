package it.polimi.ingsw.model;
/**
 * @author Giuse
 */
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.enums.EraEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventHuntTest {
    private final CardFactory cf = new CardFactory();
    private Board board;
    private GameConfig config;

    /**
     * @param p : player
     * @param n : number of hunters to add
     * @param withHuntIcon : boolean for hunt icon
     * This method adds n hunters with/without the hunt icon to the player p's tribe
     */
    private void addHunters(Player p, int n, boolean withHuntIcon) {
        for (int i = 0; i < n; i++) {
            new Hunter(EraEnum.I, 2, withHuntIcon).AddToPlayerTribe(p, board);
        }
    }
    @BeforeEach
    public void setUp() {
        config = GameConfig.create(2);
        board = new Board(config,
                cf.buildTribeDeck(config),
                cf.buildBuildingDeck(EraEnum.I, config),
                cf.buildBuildingDeck(EraEnum.II, config),
                cf.buildBuildingDeck(EraEnum.III, config));
    }
    @Test//Checking to see if the event has the right properties: ppPerHunter
    void testGetPpPerHunter(){
        assertEquals(2, new EventHunt(EraEnum.I, false, 2).getPpPerHunter(),
                "getPpPerHunter should return ppPerHunter");
    }
    @Test//Checking to see if the class resolves the event in the right way with one hunter
    void testResolve_oneHunter(){
        Player player = new Player("Riccardo", ColorEnum.BLUE);
        addHunters(player, 1,  false);
        int foodBefore = player.getFood();
        int ppBefore = player.getPP();

        new EventHunt(EraEnum.I, false, 2).
                resolve(List.of(player), board);

        assertEquals(foodBefore + 1, player.getFood(),
                "The player should have received one food because he has one hunter");
        assertEquals(ppBefore + 2, player.getPP(),
                "The player should have received numberHunters * PpPerHunter prestige points");
    }
    @Test//Checking to see if the class resolves the event in the right way with more than one hunter
    void testResolve_moreHunters(){
        Player player = new Player("Giuseppe", ColorEnum.RED);
        addHunters(player, 3,  false);
        int foodBefore = player.getFood();
        int ppBefore = player.getPP();

        new EventHunt(EraEnum.I, false, 2).
                resolve(List.of(player), board);

        assertEquals(foodBefore + 3, player.getFood(),
                "The player should have received 3 food because he has 3 hunters");
        assertEquals(ppBefore + 6, player.getPP(),
                "The player should have received (3)numberOfHunters * PpPerHunter(2) prestige points");
    }
    @Test//Checking to see if the class resolves the event in the right way with zero hunters
    void testResolve_noHunters(){
        Player player = new Player("Alessandro", ColorEnum.PURPLE);
        int foodBefore = player.getFood();
        int ppBefore = player.getPP();

        new EventHunt(EraEnum.I, false, 2).
                resolve(List.of(player), board);

        assertEquals(foodBefore, player.getFood(),
                "The player should have received no food because he has no hunters");
        assertEquals(ppBefore, player.getPP(),
                "The player should have received no prestige points because he has no hunters");
    }
    @Test//Checking to see if the class resolves the event in the right way with two players
    void testResolve_twoPlayers(){
        Player player1 = new Player("Hunter", ColorEnum.RED);
        addHunters(player1, 2,  false);
        Player player2 = new Player("NoHunters", ColorEnum.PURPLE);

        int foodBefore1 = player1.getFood();
        int ppBefore1 = player1.getPP();
        int foodBefore2 = player2.getFood();
        int ppBefore2 = player2.getPP();

        new EventHunt(EraEnum.I, false, 2).resolve(List.of(player1, player2), board);

        assertEquals(foodBefore1 + 2, player1.getFood(),
                "Player1 should have received 2 food because he has 2 hunters");
        assertEquals(ppBefore1 + 4, player1.getPP(),
                "The player should have received (2)numberOfHunters * PpPerHunter(2) prestige points");
        assertEquals(foodBefore2, player2.getFood(),
                "Player2 should have received no food because he has no hunters");
        assertEquals(ppBefore2, player2.getPP(),
                "Player should have received no prestige points because he has no hunters");
    }
}
