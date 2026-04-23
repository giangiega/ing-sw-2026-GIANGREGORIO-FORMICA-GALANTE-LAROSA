package it.polimi.ingsw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    private CardFactory cf = new CardFactory();
    private Board board;
    private GameConfig config;
    private Deck tribeDeck;
    private BuildingDeck buildingDeckEra1;
    private BuildingDeck buildingDeckEra2;
    private BuildingDeck buildingDeckEra3;
    private Game game;
    private List<Player> players = new ArrayList<>();
    private Player p1;
    private Player p2;
    private Player p3;

    private List<Integer> indexUpperCardsChosen = new ArrayList<>();
    private List<Integer> indexLowerCardsChosen = new ArrayList<>();
    private List<Integer> indexUpperBuildingsChosen = new ArrayList<>();
    private List<Integer> indexLowerBuildingsChosen = new ArrayList<>();

    //da testare anche con numPlayers 3,4,5
    @BeforeEach
    void setUp(){
        config = GameConfig.create(3);
        tribeDeck = cf.buildTribeDeck(config);
        buildingDeckEra1 = cf.buildBuildingDeck(EraEnum.I, config);
        buildingDeckEra2 = cf.buildBuildingDeck(EraEnum.II, config);
        buildingDeckEra3 = cf.buildBuildingDeck(EraEnum.III, config);
        board = new Board(config, tribeDeck, buildingDeckEra1, buildingDeckEra2, buildingDeckEra3);
        p1 = new Player("Francesco", ColorEnum.BLUE);
        p2 = new Player("Giannuzzi", ColorEnum.PURPLE);
        p3 = new Player("Giannuzzio", ColorEnum.WHITE);
        players.add(p1);
        players.add(p2);
        players.add(p3);
        game = new Game(players, board, config);
    }

    @Test
    void testStartGame(){
        game.startGame();

        assertEquals(2, players.getFirst().getFood());
        assertEquals(3, players.get(1).getFood());
        assertEquals(3, players.get(2).getFood());
        assertFalse(board.getTurnOrderTile().getOrder().isEmpty());
        assertEquals(3, board.getTurnOrderTile().getOrder().size());
    }

    @Test
    void testPlaceTotem() throws InvalidPlayerActionException {
        game.placeTotem(p1, board.getOfferTrack().get(0));
        game.placeTotem(p2, board.getOfferTrack().get(1));
        game.placeTotem(p3, board.getOfferTrack().get(2));

        assertFalse(board.getOfferTrack().get(0).getFreeOfferTile());
        assertFalse(board.getOfferTrack().get(1).getFreeOfferTile());
        assertFalse(board.getOfferTrack().get(2).getFreeOfferTile());

        assertTrue(board.getTurnOrderTile().getOrder().isEmpty());
    }

    @Test //sbagliato
    void testResolveAction() throws InvalidPlayerActionException {
        game.placeTotem(p1, board.getOfferTrack().get(0));
        int foodBefore = board.getOfferTrack().get(0).getOccupant().getFood();

        assertEquals(0, board.getOfferTrack().get(0).getOccupant().getFood());
        assertEquals(0, board.getTurnOrderTile().getOrder().size());
        assertEquals('A', board.getOfferTrack().get(0).getLetter());

        game.resolveAction(board.getOfferTrack().get(0), indexUpperCardsChosen, indexLowerCardsChosen, indexUpperBuildingsChosen, indexLowerBuildingsChosen);

        assertEquals(foodBefore+3+board.getTurnOrderTile().getFoodBonusForSlot(0), board.getTurnOrderTile().getOrder().getFirst().getFood());
    }
}
