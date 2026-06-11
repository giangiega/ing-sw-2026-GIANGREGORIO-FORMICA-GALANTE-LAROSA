package it.polimi.ingsw.controller;


import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.network.ClientConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {
    private List<Player> players = new ArrayList<>();
    private Map<String, ClientConnection> clientManagers = new HashMap<>();
    private Game game;
    private TurnController turnController;

    private Player p1;
    private Player p2;
    private Player p3;
    private FakeClientConnection cmP1;
    private FakeClientConnection cmP2;
    private FakeClientConnection cmP3;

    private List<String> disconnectedPlayers;
    private Set<String> playersRequiredToResume = new HashSet<>();
    private boolean isRecoveryMode = false;

    private List<String> reconnectedThisRound = new ArrayList<>();

    private GameController gc;



    @BeforeEach
    void setUp(){
        p1 = new Player("Ale", ColorEnum.RED);
        p2 = new Player("Ric", ColorEnum.BLUE);
        p3 = new Player("Dan", ColorEnum.YELLOW);

        players.add(p1);
        players.add(p2);
        players.add(p3);

        cmP1 = new FakeClientConnection("Ale");
        cmP2 = new FakeClientConnection("Ric");
        cmP3 = new FakeClientConnection("Dan");

        clientManagers.put("Ale", cmP1);
        clientManagers.put("Ric", cmP2);
        clientManagers.put("Dan", cmP3);

        gc = new GameController(players, clientManagers);
        gc.startGame();
    }

    @Test
    void testStartGame(){
        gc.startGame();

        assertFalse(gc.getGameOver());
        assertTrue(gc.getDisconnectedPlayers().isEmpty());
        assertEquals(3, gc.getConnectedPlayersCount());
        assertNotNull(gc.getGame());
        assertEquals(3, gc.getGame().getPlayers().size());
        assertNotNull(gc.getGame().getBoard());
        assertFalse(gc.getGame().getBoard().getOfferTrack().isEmpty());

        assertTrue(cmP1.received("GameStartedEvent"),
                "player 1 should receive GameStartedEvent");
        assertTrue(cmP2.received("GameStartedEvent"),
                "player 2 should receive GameStartedEvent");
        assertTrue(cmP3.received("GameStartedEvent"),
                "player 3 should receive GameStartedEvent");
        boolean p1GotMove = cmP1.received("MoveTotemEvent");
        boolean p2GotMove = cmP2.received("MoveTotemEvent");
        boolean p3GotMove = cmP2.received("MoveTotemEvent");
        assertTrue(p1GotMove || p2GotMove || p3GotMove,
                "One player should receive MoveTotemEvent at the start of the game");
    }

   @Test
   void testGetPlayerColor(){
       ColorEnum color = gc.getPlayerColor("Ale");

       assertNotNull(color);
       assertEquals(ColorEnum.RED, color);
       assertNull(gc.getPlayerColor("playerNotInList"));
   }

   @Test
    void testIsDisconnectedPlayer(){
       assertFalse(gc.isDisconnectedPlayer("Ale"));

       gc.handleDisconnection("Ale");
       assertTrue(gc.isDisconnectedPlayer("Ale"));
    }

   @Test
    void testHandleDisconnection(){
        int eventBeforeAleDisconnection = cmP1.eventCount();
        int disconnectedBefore = gc.getDisconnectedPlayers().size();

        cmP2.clearEvents();
        cmP3.clearEvents();

        gc.handleDisconnection("Ale");
        gc.handleDisconnection("playerNotInList");

        assertTrue(gc.getDisconnectedPlayers().contains("Ale"));
        assertEquals(2, gc.getConnectedPlayersCount());

        assertTrue(cmP2.received("PlayerDisconnectedEvent"),
                "Ric should receive PlayerDisconnectedEvent");
        assertTrue(cmP3.received("PlayerDisconnectedEvent"),
                "Dan should receive PlayerDisconnectedEvent");

        assertTrue(eventBeforeAleDisconnection == cmP1.eventCount());
        assertTrue(cmP1.countOf("PlayerDisconnectedEvent") == 0,
                "Alice shouldn't receive PlayerDisconnectedEvent");

        //disconnectedBefore-1 because Ale disconnected
        assertEquals(disconnectedBefore+1, gc.getDisconnectedPlayers().size());

        gc.handleDisconnection("Ale"); // double
        assertEquals(1, gc.getDisconnectedPlayers().size());

        cmP2.clearEvents();
        cmP3.clearEvents();
        gc.handleDisconnection("Ric"); //only Dan is in game
        assertTrue(cmP3.received("GameSuspendedEvent"),
                "Dan should receive GameSuspendedEvent when it becomes the only player left");
    }

    @Test
    public void testHandleReconnection(){
        gc.handleDisconnection("Ale");
        FakeClientConnection newCm = new FakeClientConnection("Ale");
        gc.handleReconnection("Ale", newCm);

        assertFalse(gc.getDisconnectedPlayers().contains("Ale"));
        assertTrue(newCm.received("UpdateRoundEvent") ||
                        newCm.received("UpdateOfferTrackEvent"),
                "New connection should receive updated state of the game");

        newCm.clearEvents();
        assertTrue(newCm.eventCount() == 0,
                "An ok connection shouldn't receive events from handleReconnection");

        gc.handleDisconnection("Ale");
        assertEquals(2, gc.getConnectedPlayersCount());
        newCm.clearEvents();
        gc.handleReconnection("Ale", newCm);
        assertEquals(3, gc.getConnectedPlayersCount());

        gc.handleDisconnection("Ale");
        cmP2.clearEvents();

        newCm.clearEvents();
        gc.handleReconnection("Ale", newCm);
        assertTrue(cmP2.received("PlayerReconnectedEvent"),
                "Ric should receive PlayerReconnectedEvent when Ale reconnect");
    }

    @Test
    void testPlaceTotem() {
        List<Player> slots = gc.getGame().getBoard().getTurnOrderTile().getSlots();
        List<OfferTile> track = gc.getGame().getBoard().getOfferTrack();

        Player first  = slots.get(0);
        Player second = slots.get(1);

        OfferTile firstFreeTile = null;
        for (OfferTile t : track) {
            if (t.getFreeOfferTile()) { firstFreeTile = t; break; }
        }
        assertNotNull(firstFreeTile, "There must be at least one free tile at game start");

        FakeClientConnection cmSecond = cmForPlayer(second.getName());

        cmP1.clearEvents();
        cmP2.clearEvents();
        cmP3.clearEvents();

        gc.placeTotem(first.getName(), firstFreeTile.getLetter());

        assertFalse(firstFreeTile.getFreeOfferTile(),
                "The tile must be occupied after placeTotem");
        assertEquals(first.getName(), firstFreeTile.getOccupant().getName());

        boolean someoneReceivedUpdate =
                cmP1.received("UpdateOfferTrackEvent") ||
                        cmP2.received("UpdateOfferTrackEvent") ||
                        cmP3.received("UpdateOfferTrackEvent");
        assertTrue(someoneReceivedUpdate,
                "UpdateOfferTrackEvent must be sent after a valid placement");

        cmSecond.clearEvents();
        gc.placeTotem(second.getName(), firstFreeTile.getLetter());

        assertTrue(cmSecond.received("InvalidChoiceEvent"),
                "The second player must receive InvalidChoiceEvent for an occupied tile");

        assertDoesNotThrow(() -> gc.placeTotem(first.getName(), 'Z'),
                "placeTotem with an unknown letter must not throw");

        assertDoesNotThrow(() -> gc.placeTotem("UnknownPlayer", 'B'),
                "placeTotem with an unknown player must not throw");
    }

    @Test
    void testResolveAction() {
        List<Player> slots = gc.getGame().getBoard().getTurnOrderTile().getSlots();
        List<OfferTile> track = gc.getGame().getBoard().getOfferTrack();

        List<OfferTile> freeTiles = new ArrayList<>();
        for (OfferTile t : track) {
            if (t.getFreeOfferTile()) freeTiles.add(t);
        }

        gc.placeTotem(slots.get(0).getName(), freeTiles.get(0).getLetter());
        gc.placeTotem(slots.get(1).getName(), freeTiles.get(1).getLetter());
        gc.placeTotem(slots.get(2).getName(), freeTiles.get(2).getLetter());

        String firstToResolve = freeTiles.get(0).getOccupant().getName();
        String wrongPlayer    = freeTiles.get(1).getOccupant().getName();

        FakeClientConnection cmWrong = cmForPlayer(wrongPlayer);
        cmWrong.clearEvents();
        gc.resolveAction(wrongPlayer,
                new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>());

        assertFalse(cmWrong.received("ValidCardsEvent"),
                "The non-current player must not receive ValidCardsEvent");

        assertDoesNotThrow(() ->
                        gc.resolveAction(firstToResolve,
                                new ArrayList<>(), new ArrayList<>(),
                                new ArrayList<>(), new ArrayList<>()),
                "resolveAction with empty lists must not throw"
        );

        FakeClientConnection cmCorrect = cmForPlayer(firstToResolve);
        assertTrue(cmCorrect.received("ValidCardsEvent"),
                "The current player must receive ValidCardsEvent after resolveAction");
    }

    @Test
    void testEndRound() {
        //testing a full round
        int roundBefore = gc.getGame().getCurrentRound();

        List<Player> slots = gc.getGame().getBoard().getTurnOrderTile().getSlots();
        List<OfferTile> track = gc.getGame().getBoard().getOfferTrack();

        List<OfferTile> freeTiles = new ArrayList<>();
        for (OfferTile t : track) {
            if (t.getFreeOfferTile()) freeTiles.add(t);
        }

        //all players place their totem
        gc.placeTotem(slots.get(0).getName(), freeTiles.get(0).getLetter());
        gc.placeTotem(slots.get(1).getName(), freeTiles.get(1).getLetter());
        gc.placeTotem(slots.get(2).getName(), freeTiles.get(2).getLetter());

        String first = freeTiles.get(0).getOccupant().getName();
        String second = freeTiles.get(1).getOccupant().getName();
        String third = freeTiles.get(2).getOccupant().getName();

        gc.resolveAction(first,  new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>());
        gc.resolveAction(second, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>());

        cmP1.clearEvents();
        cmP2.clearEvents();
        cmP3.clearEvents();

        //this resolveAction calls endRound
        gc.resolveAction(third, new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>());

        assertTrue(gc.getGame().getCurrentRound() > roundBefore,
                "The round must advance after all players have resolved");

        boolean someoneReceivedUpdateRound =
                cmP1.received("UpdateRoundEvent") ||
                        cmP2.received("UpdateRoundEvent") ||
                        cmP3.received("UpdateRoundEvent");
        assertTrue(someoneReceivedUpdateRound,
                "UpdateRoundEvent must be broadcast at the end of the round");
    }

    private FakeClientConnection cmForPlayer(String playerName) {
        if (playerName.equals("Ale")) return cmP1;
        if (playerName.equals("Ric")) return cmP2;
        return cmP3;
    }
}
