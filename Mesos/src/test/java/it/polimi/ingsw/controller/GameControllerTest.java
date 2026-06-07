package it.polimi.ingsw.controller;

import it.polimi.ingsw.database.DatabaseManager;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.serverInterface.GameStartedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/*public class GameControllerTest {
    private List<Player> players = new ArrayList<>();
    private Map<String, ClientConnection> clientManagers = new HashMap<>();
    private Game game;
    private TurnController turnController;
    private DatabaseManager database;

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
                "player 1 deve riceve GameStartedEvent");
        assertTrue(cmP2.received("GameStartedEvent"),
                "player 2 deve ricevere GameStartedEvent");
        assertTrue(cmP3.received("GameStartedEvent"),
                "player 3 deve ricevere GameStartedEvent");
        boolean p1GotMove = cmP1.received("MoveTotemEvent");
        boolean p2GotMove = cmP2.received("MoveTotemEvent");
        boolean p3GotMove = cmP2.received("MoveTotemEvent");
        assertTrue(p1GotMove || p2GotMove || p3GotMove,
                "Almeno un giocatore deve ricevere MoveTotemEvent all'inizio");
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

   /* @Test
    void testHandleDisconnection(){
        int eventBeforeAleDisconnection = cmP1.eventCount();
        int disconnectedBefore = gc.getDisconnectedPlayers().size();

        gc.handleDisconnection("Ale");
        gc.handleDisconnection("playerNotInList");

        assertTrue(gc.getDisconnectedPlayers().contains("Ale"));
        assertEquals(2, gc.getConnectedPlayersCount());

        cmP2.clearEvents();
        cmP3.clearEvents();
        assertTrue(cmP1.received("PlayerDisconnectedEvent"),
                "Ric deve ricevere PlayerDisconnectedEvent");
        assertTrue(cmP1.received("PlayerDisconnectedEvent"),
                "Dan deve ricevere PlayerDisconnectedEvent");

        assertTrue(eventBeforeAleDisconnection == cmP1.eventCount());
        assertTrue(cmP1.countOf("PlayerDisconnectedEvent") == 0,
                "Alice non deve ricevere il proprio PlayerDisconnectedEvent");

        //disconnectedBefore-1 because Ale disconnected
        assertEquals(disconnectedBefore-1, gc.getDisconnectedPlayers().size());

        gc.handleDisconnection("Ale"); // double
        assertEquals(2, gc.getDisconnectedPlayers().size());

        cmP2.clearEvents();
        cmP3.clearEvents();
        gc.handleDisconnection("Ric"); //only Dan is in game
        assertTrue(cmP3.received("GameSuspendedEvent"),
                "Dan deve ricevere GameSuspendedEvent quando rimane solo");
    }*/

    /*@Test
    public void testHandleReconnection(){

    }*/
//}
