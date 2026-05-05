package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.network.Server;
import org.junit.jupiter.api.BeforeEach;

public class LobbyControllerTest {
    Server server;
    LobbyController lobbyController;
    GameController gameController;

    @BeforeEach
    void setUp(){
        this.server = new Server();
        this.lobbyController = new LobbyController(server, 2);
    }
}
