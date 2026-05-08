package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.LobbyController;
import it.polimi.ingsw.model.Player;

import java.util.List;
import java.util.Map;

/**
 * this class would be an interface but RmiClient already implements an interface
 */
abstract public class ServerClass {
   abstract public void fullLobby(List<Player> lobbyPlayers, Map<String, ClientConnection> clientManagers);
   abstract public LobbyController getLobbyController();
   abstract public GameController getGameController();
}
