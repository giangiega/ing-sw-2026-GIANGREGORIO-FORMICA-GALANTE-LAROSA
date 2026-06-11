package it.polimi.ingsw.controller;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.network.ClientConnection;
import it.polimi.ingsw.persistence.TurnControllerSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TurnControllerTest {

    private List<Player> players = new ArrayList<>();
    private Map<String, ClientConnection> clientManagers = new HashMap<>();
    private GameController gc;
    private TurnController tc;

    private Player p1;
    private Player p2;
    private Player p3;
    private FakeClientConnection cmP1;
    private FakeClientConnection cmP2;
    private FakeClientConnection cmP3;

    @BeforeEach
    void setUp() {
        //clears collections to avoid duplicates on setUp recall
        players.clear();
        clientManagers.clear();

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
        gc.startGame(); // Internally calls tc.startPlacementPhase()
        tc = gc.getTurnController();
    }

    @Test
    void testStartPlacementPhase() {
        TurnOrderTile turnOrderTile = gc.getGame().getBoard().getTurnOrderTile();
        cmP1.clearEvents();
        cmP2.clearEvents();
        cmP3.clearEvents();

        tc.startPlacementPhase(turnOrderTile);

        boolean p1GotMove = cmP1.received("MoveTotemEvent");
        boolean p2GotMove = cmP2.received("MoveTotemEvent");
        boolean p3GotMove = cmP3.received("MoveTotemEvent");

        assertTrue(p1GotMove || p2GotMove || p3GotMove,
                "At least one player should receive MoveTotemEvent at the start of placement phase");
        assertNull(tc.getCurrentResolvingPlayer(),
                "There should be no resolving player during the placement phase");
    }

    @Test
    void testOnTotemPlaced() {
        TurnOrderTile turnOrderTile = gc.getGame().getBoard().getTurnOrderTile();
        List<OfferTile> track = gc.getGame().getBoard().getOfferTrack();
        List<Player> slots = turnOrderTile.getSlots();

        Player firstPlayer = slots.get(0);
        Player secondPlayer = slots.get(1);

        cmP1.clearEvents();
        cmP2.clearEvents();
        cmP3.clearEvents();

        OfferTile firstFreeTile = getFreeTiles(track).get(0);
        tc.onTotemPlaced(firstPlayer.getName(), turnOrderTile, track);

        FakeClientConnection secondPlayerCm = cmForPlayer(secondPlayer.getName());
        assertTrue(secondPlayerCm.received("MoveTotemEvent"),
                "The second player should receive MoveTotemEvent after the first places a totem");
        assertNull(tc.getCurrentResolvingPlayer(), "Should still be in placement phase");
    }

    @Test
    void testAskNextTotemPlacement() {
        TurnOrderTile turnOrderTile = gc.getGame().getBoard().getTurnOrderTile();

        cmP1.clearEvents();
        cmP2.clearEvents();
        cmP3.clearEvents();

        tc.askNextTotemPlacement(turnOrderTile);

        Player firstPlayer = turnOrderTile.getSlots().get(0);
        FakeClientConnection firstPlayerCm = cmForPlayer(firstPlayer.getName());

        assertTrue(firstPlayerCm.received("MoveTotemEvent"),
                "The next available player should be asked to place the totem");
    }

    @Test
    void testOnActionResolved() {
        TurnOrderTile turnOrderTile = gc.getGame().getBoard().getTurnOrderTile();
        List<OfferTile> track = gc.getGame().getBoard().getOfferTrack();

        // Saving list in a local variable to avoid dynamic calls which can cause IndexOutOfBounds
        List<Player> slots = turnOrderTile.getSlots();
        List<OfferTile> freeTiles = getFreeTiles(track);

        // Force transition to resolve phase
        gc.placeTotem(slots.get(0).getName(), freeTiles.get(0).getLetter());
        gc.placeTotem(slots.get(1).getName(), freeTiles.get(1).getLetter());
        gc.placeTotem(slots.get(2).getName(), freeTiles.get(2).getLetter());

        String firstToResolve = freeTiles.get(0).getOccupant().getName();
        String secondToResolve = freeTiles.get(1).getOccupant().getName();

        assertEquals(firstToResolve, tc.getCurrentResolvingPlayer());

        cmP1.clearEvents();
        cmP2.clearEvents();
        cmP3.clearEvents();

        tc.onActionResolved();

        assertEquals(secondToResolve, tc.getCurrentResolvingPlayer(),
                "After the first player resolves, it should be the second player's turn");
        assertTrue(cmForPlayer(secondToResolve).received("IsYourTurnEvent"),
                "The next player must receive IsYourTurnEvent");
    }

    @Test
    void testSkipCurrentPlayer() {
        TurnOrderTile turnOrderTile = gc.getGame().getBoard().getTurnOrderTile();
        List<OfferTile> track = gc.getGame().getBoard().getOfferTrack();

        List<Player> slots = new ArrayList<>(turnOrderTile.getSlots());
        List<OfferTile> freeTiles = getFreeTiles(track);

        gc.placeTotem(slots.get(0).getName(), freeTiles.get(0).getLetter());
        gc.placeTotem(slots.get(1).getName(), freeTiles.get(1).getLetter());
        gc.placeTotem(slots.get(2).getName(), freeTiles.get(2).getLetter());

        String firstToResolve = tc.getCurrentResolvingPlayer();

        tc.skipCurrentPlayer(firstToResolve);

        assertEquals(firstToResolve, tc.getCurrentResolvingPlayer(),
                "If the player is not disconnected, skipCurrentPlayer should not actually skip them");

        gc.handleDisconnection(firstToResolve);

        assertNotEquals(firstToResolve, tc.getCurrentResolvingPlayer(),
                "Once disconnected, the player should be skipped and turn passed");
    }

    @Test
    void testGetCurrentResolvingPlayer() {
        assertNull(tc.getCurrentResolvingPlayer(), "Should be null in placement phase");

        TurnOrderTile turnOrderTile = gc.getGame().getBoard().getTurnOrderTile();
        List<OfferTile> track = gc.getGame().getBoard().getOfferTrack();

        List<Player> slots = turnOrderTile.getSlots();
        List<OfferTile> freeTiles = getFreeTiles(track);

        gc.placeTotem(slots.get(0).getName(), freeTiles.get(0).getLetter());
        gc.placeTotem(slots.get(1).getName(), freeTiles.get(1).getLetter());
        gc.placeTotem(slots.get(2).getName(), freeTiles.get(2).getLetter());

        String expectedFirst = freeTiles.get(0).getOccupant().getName();
        assertEquals(expectedFirst, tc.getCurrentResolvingPlayer(),
                "Should return the correct player in resolve phase");
    }

    @Test
    void testOnPlayerDisconnected() {
        TurnOrderTile turnOrderTile = gc.getGame().getBoard().getTurnOrderTile();
        List<OfferTile> track = gc.getGame().getBoard().getOfferTrack();

        List<Player> slots = new ArrayList<>(turnOrderTile.getSlots());

        //disconnection during placement phase
        Player firstPlayer = slots.get(0);
        Player secondPlayer = slots.get(1);

        assertEquals(firstPlayer.getName(), tc.getSnapshot().getCurrentPlacementPlayer());

        gc.handleDisconnection(firstPlayer.getName());

        assertEquals(secondPlayer.getName(), tc.getSnapshot().getCurrentPlacementPlayer(),
                "If current placement player disconnects, turn passes to next connected player");

        //disconnection during resolving phase. Calls setUp() to reset the GameController and delete "Ale" from disconnected players
        setUp();

        turnOrderTile = gc.getGame().getBoard().getTurnOrderTile();
        track = gc.getGame().getBoard().getOfferTrack();

        List<Player> slots2 = new ArrayList<>(turnOrderTile.getSlots());
        List<OfferTile> freeTiles2 = getFreeTiles(track);

        gc.placeTotem(slots2.get(0).getName(), freeTiles2.get(0).getLetter());
        gc.placeTotem(slots2.get(1).getName(), freeTiles2.get(1).getLetter());
        gc.placeTotem(slots2.get(2).getName(), freeTiles2.get(2).getLetter());

        String firstToResolve = tc.getCurrentResolvingPlayer();
        String secondToResolve = freeTiles2.get(1).getOccupant().getName();

        gc.handleDisconnection(firstToResolve);

        assertEquals(secondToResolve, tc.getCurrentResolvingPlayer(),
                "If current resolving player disconnects, turn passes to the next player");
    }

    @Test
    void testResumeAfterSuspension() {
        TurnOrderTile turnOrderTile = gc.getGame().getBoard().getTurnOrderTile();
        List<OfferTile> track = gc.getGame().getBoard().getOfferTrack();

        cmP1.clearEvents();
        cmP2.clearEvents();
        cmP3.clearEvents();

        tc.resumeAfterSuspension(turnOrderTile, track);

        Player expectedPlayer = turnOrderTile.getSlots().get(0);
        assertTrue(cmForPlayer(expectedPlayer.getName()).received("MoveTotemEvent"),
                "Should ask the next player to place totem if resuming during placement phase");
    }

    @Test
    void testGetSnapshot() {
        TurnOrderTile turnOrderTile = gc.getGame().getBoard().getTurnOrderTile();
        List<OfferTile> track = gc.getGame().getBoard().getOfferTrack();

        Player firstPlayer = turnOrderTile.getSlots().get(0);
        gc.placeTotem(firstPlayer.getName(), getFreeTiles(track).get(0).getLetter());

        TurnControllerSnapshot snapshot = tc.getSnapshot();

        assertFalse(snapshot.isInResolvingPhase());
        assertTrue(snapshot.getTotemPlacedCurrRound().contains(firstPlayer.getName()));
        assertNotNull(snapshot.getCurrentPlacementPlayer());
    }

    @Test
    void testRestoreFromSnapshot() {
        TurnOrderTile turnOrderTile = gc.getGame().getBoard().getTurnOrderTile();
        List<OfferTile> track = gc.getGame().getBoard().getOfferTrack();
        Player firstPlayer = turnOrderTile.getSlots().get(0);

        gc.placeTotem(firstPlayer.getName(), getFreeTiles(track).get(0).getLetter());
        TurnControllerSnapshot snapshot = tc.getSnapshot();

        TurnController restoredTc = new TurnController(gc, 3);
        restoredTc.restoreFromSnapshot(snapshot);

        TurnControllerSnapshot restoredSnapshot = restoredTc.getSnapshot();
        assertEquals(snapshot.isInResolvingPhase(), restoredSnapshot.isInResolvingPhase());
        assertEquals(snapshot.getIdx(), restoredSnapshot.getIdx());
        assertEquals(snapshot.getCurrentPlacementPlayer(), restoredSnapshot.getCurrentPlacementPlayer());
        assertEquals(snapshot.getTotemPlacedCurrRound().size(), restoredSnapshot.getTotemPlacedCurrRound().size());
    }

    @Test
    void testRestoreBoard() {
        TurnOrderTile turnOrderTile = gc.getGame().getBoard().getTurnOrderTile();
        List<OfferTile> track = gc.getGame().getBoard().getOfferTrack();

        TurnController restoredTc = new TurnController(gc, 3);

        assertDoesNotThrow(() -> restoredTc.restoreBoard(turnOrderTile, track),
                "Restoring the board should safely update internal references without throwing exceptions");
    }

    private FakeClientConnection cmForPlayer(String playerName) {
        if (playerName.equals("Ale")) return cmP1;
        if (playerName.equals("Ric")) return cmP2;
        return cmP3;
    }

    private List<OfferTile> getFreeTiles(List<OfferTile> track) {
        List<OfferTile> freeTiles = new ArrayList<>();
        for (OfferTile t : track) {
            if (t.getFreeOfferTile()) freeTiles.add(t);
        }
        return freeTiles;
    }
}