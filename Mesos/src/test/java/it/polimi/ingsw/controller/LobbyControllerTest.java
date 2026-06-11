package it.polimi.ingsw.controller;

import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.network.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyControllerTest {

    private Server server;
    private LobbyController lobbyController;

    private FakeClientConnection cmP1;
    private FakeClientConnection cmP2;
    private FakeClientConnection cmP3;

    @BeforeEach
    void setUp() {
        this.server = new Server();
        this.lobbyController = new LobbyController(2); // Lobby with 2 players to test fast full lobby
        this.lobbyController.setServer(this.server);

        cmP1 = new FakeClientConnection("Ale");
        cmP2 = new FakeClientConnection("Ric");
        cmP3 = new FakeClientConnection("Dan");
    }

    @Test
    void testHandleLobbyDisconnection() {
        // Adding valid player
        lobbyController.addPlayer("Ale", ColorEnum.RED, cmP1);
        cmP1.clearEvents();

        // Simulating disconnection in the lobby
        assertDoesNotThrow(() -> lobbyController.handleLobbyDisconnection("Ale"),
                "handleLobbyDisconnection should not throw exceptions for valid disconnected players");

        // Disconnection of a non-existing player shouldn't cause an error
        assertDoesNotThrow(() -> lobbyController.handleLobbyDisconnection("GhostPlayer"),
                "handleLobbyDisconnection should safely ignore unknown players");

        // Player should be able to reconnect (as a ghost player)
        lobbyController.addPlayer("Ale", ColorEnum.RED, cmP1);
        assertTrue(cmP1.received("LoggedEvent"),
                "A ghost player should successfully receive a LoggedEvent when reconnecting before the game starts");
    }

    @Test
    void testAddPlayer() {
        // Adding first player
        lobbyController.addPlayer("Ale", ColorEnum.RED, cmP1);
        assertTrue(cmP1.received("LoggedEvent"), "First player should receive a LoggedEvent");

        // Cannot add player: name has been already taken
        lobbyController.addPlayer("Ale", ColorEnum.BLUE, cmP2);
        assertTrue(cmP2.received("LoggedEvent"), "Player with duplicate name should receive a rejection LoggedEvent");

        // Cannot add player: color has been already taken
        lobbyController.addPlayer("Ric", ColorEnum.RED, cmP2);
        assertTrue(cmP2.received("LoggedEvent"), "Player with duplicate color should receive a rejection LoggedEvent");
        cmP2.clearEvents();

        // Adding last player (Full lobby -> set gameStated to true)
        lobbyController.addPlayer("Ric", ColorEnum.BLUE, cmP2);
        assertTrue(cmP2.received("LoggedEvent"), "Second player should receive a LoggedEvent");

        // Cannot add a player to a full lobby/started game
        assertDoesNotThrow(() -> lobbyController.addPlayer("Dan", ColorEnum.YELLOW, cmP3),
                "Adding a player when the game is already started should not crash");
        assertTrue(cmP3.received("LoggedEvent"), "Late player should receive a rejection LoggedEvent because lobby is full/started");
    }

    @Test
    void testSetServer() {
        LobbyController testLc = new LobbyController(2);

        // First server insert
        assertDoesNotThrow(() -> testLc.setServer(server),
                "setServer should correctly assign the server instance without throwing");

        // Calling again (must ignore and return without errors)
        assertDoesNotThrow(() -> testLc.setServer(server),
                "Setting the server a second time should return early safely");
    }
}