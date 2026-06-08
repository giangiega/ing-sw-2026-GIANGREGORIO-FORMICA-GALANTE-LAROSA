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
        this.lobbyController = new LobbyController(2); // Lobby da 2 giocatori per testare il riempimento veloce
        this.lobbyController.setServer(this.server);

        cmP1 = new FakeClientConnection("Ale");
        cmP2 = new FakeClientConnection("Ric");
        cmP3 = new FakeClientConnection("Dan");
    }

    @Test
    void testHandleLobbyDisconnection() {
        // Aggiungiamo un giocatore valido
        lobbyController.addPlayer("Ale", ColorEnum.RED, cmP1);
        cmP1.clearEvents();

        // 1. Simula disconnessione durante la lobby
        assertDoesNotThrow(() -> lobbyController.handleLobbyDisconnection("Ale"),
                "handleLobbyDisconnection should not throw exceptions for valid disconnected players");

        // 2. Disconnessione di un giocatore inesistente non deve causare errori
        assertDoesNotThrow(() -> lobbyController.handleLobbyDisconnection("GhostPlayer"),
                "handleLobbyDisconnection should safely ignore unknown players");

        // 3. Verifichiamo che il giocatore possa rientrare (riconosciuto come ghost player)
        lobbyController.addPlayer("Ale", ColorEnum.RED, cmP1);
        assertTrue(cmP1.received("LoggedEvent"),
                "A ghost player should successfully receive a LoggedEvent when reconnecting before the game starts");
    }

    @Test
    void testAddPlayer() {
        // 1. Aggiunta valida del primo giocatore
        lobbyController.addPlayer("Ale", ColorEnum.RED, cmP1);
        assertTrue(cmP1.received("LoggedEvent"), "First player should receive a LoggedEvent");

        // 2. Aggiunta non valida (nome già preso)
        lobbyController.addPlayer("Ale", ColorEnum.BLUE, cmP2);
        assertTrue(cmP2.received("LoggedEvent"), "Player with duplicate name should receive a rejection LoggedEvent");

        // 3. Aggiunta non valida (colore già preso)
        lobbyController.addPlayer("Ric", ColorEnum.RED, cmP2);
        assertTrue(cmP2.received("LoggedEvent"), "Player with duplicate color should receive a rejection LoggedEvent");
        cmP2.clearEvents();

        // 4. Aggiunta valida del secondo giocatore (La lobby si riempie -> gameStarted diventa true)
        lobbyController.addPlayer("Ric", ColorEnum.BLUE, cmP2);
        assertTrue(cmP2.received("LoggedEvent"), "Second player should receive a LoggedEvent");

        // 5. Aggiunta rifiutata a gioco iniziato / lobby piena
        assertDoesNotThrow(() -> lobbyController.addPlayer("Dan", ColorEnum.YELLOW, cmP3),
                "Adding a player when the game is already started should not crash");
        assertTrue(cmP3.received("LoggedEvent"), "Late player should receive a rejection LoggedEvent because lobby is full/started");
    }

    @Test
    void testSetServer() {
        LobbyController testLc = new LobbyController(2);

        // 1. Primo inserimento del server
        assertDoesNotThrow(() -> testLc.setServer(server),
                "setServer should correctly assign the server instance without throwing");

        // 2. Richiamarlo una seconda volta (deve ignorare l'assegnazione e ritornare subito senza errori)
        assertDoesNotThrow(() -> testLc.setServer(server),
                "Setting the server a second time should return early safely");
    }
}