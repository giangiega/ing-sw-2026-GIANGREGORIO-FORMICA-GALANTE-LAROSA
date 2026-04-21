package it.polimi.ingsw;
/**
 * @author Giuse
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventCavePaintingTest {
    private final CardFactory cf = new CardFactory();
    private Board board;
    private GameConfig config;

    /**
     * @param p : player
     * @param n : number of artists to add
     * This method adds n artists to the player p's tribe
     */
    private void addArtists(Player p, int n) {
        for (int i = 0; i < n; i++) {
            new Artist(EraEnum.I, 2).AddToPlayerTribe(p, board);
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
    @Test//Checking to see if the event has the right properties: minArtist
    void testGetMinArtist(){
        assertEquals(2, new EventCavePainting(EraEnum.I, false, 2, 3, 2).getMinArtist(),
                "getMinArtist should return minArtist");
    }
    @Test//Checking to see if the event has the right properties: gainedPP
    void testGetGainedPP(){
        assertEquals(3, new EventCavePainting(EraEnum.I, false, 2, 3, 2).getGainedPP(),
                "getGainedPP should return gainedPP");
    }
    @Test//Checking to see if the event has the right properties: lostPP
    void testGetLostPP(){
        assertEquals(2, new EventCavePainting(EraEnum.I, false, 2, 3, 2).getLostPP(),
                "getLostPP should return lostPP");
    }
    @Test//Checking to see if the class resolves the event in the right way if the number of artists matches
    void testResolve_equalsArtist(){
        Player player = new Player("Riccardo", ColorEnum.BLUE);
        addArtists(player, 2);
        int ppBefore = player.getPP();

        new EventCavePainting(EraEnum.I, false, 2, 3, 2).
                resolve(List.of(player), board);

        assertEquals(ppBefore + 2 * 3, player.getPP(),
                "The player should have received numberOfArtists * gainedPP prestige points");
    }
    @Test//Checking to see if the class resolves the event in the right way if the number of artists is greater than minArtists
    void testResolve_moreArtist(){
        Player player = new Player("Giuseppe", ColorEnum.RED);
        addArtists(player, 4);
        int ppBefore = player.getPP();

        new EventCavePainting(EraEnum.I, false, 2, 3, 2).
                resolve(List.of(player), board);

        assertEquals(ppBefore + 4 * 3, player.getPP(),
                "The player should have received numberOfArtists * gainedPP prestige points");
    }
    @Test//Checking to see if the class resolves the event in the right way if the number of artists is less than minArtists
    void testResolve_lessArtist(){
        Player player = new Player("Alessandro", ColorEnum.PURPLE);
        addArtists(player, 1);
        int ppBefore = player.getPP();

        new EventCavePainting(EraEnum.I, false, 2, 3, 2).
                resolve(List.of(player), board);

        assertEquals(ppBefore - 2, player.getPP(),
                "The player should have lost lostPP prestige points");
    }
    @Test//Checking to see if the class resolves the event in the right way if the number of artists is zero
    void testResolve_zeroArtist(){
        Player player = new Player("Daniele", ColorEnum.WHITE);
        int ppBefore = player.getPP();

        new EventCavePainting(EraEnum.I, false, 2, 3, 2).
                resolve(List.of(player), board);

        assertEquals(ppBefore - 2, player.getPP(),
                "The player should have lost lostPP prestige points");
    }
    @Test//Checking to see if the class resolves the event in the right way with two players
    void testResolve_twoPlayers(){
        Player player1 = new Player("Winner", ColorEnum.YELLOW);
        Player player2 = new Player("Loser", ColorEnum.WHITE);
        addArtists(player1, 3);

        int ppPlayer1Before = player1.getPP();
        int ppPlayer2Before = player2.getPP();

        new EventCavePainting(EraEnum.I, false, 2, 3, 2).
                resolve(List.of(player1, player2), board);

        assertEquals(ppPlayer1Before + 3 * 3, player1.getPP(),
                "The player should have received numberOfArtists * gainedPP prestige points");
        assertEquals(ppPlayer2Before - 2, player2.getPP(),
                "The player should have lost lostPP prestige points");
    }


}
