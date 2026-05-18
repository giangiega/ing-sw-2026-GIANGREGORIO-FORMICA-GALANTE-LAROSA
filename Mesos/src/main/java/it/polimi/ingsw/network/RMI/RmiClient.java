/**
 * @author Giuse
 */
package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.userInterface.ViewInterface;

import javax.swing.text.View;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RmiClient extends UnicastRemoteObject implements VirtualView {

    private static final String SERVER_NAME = "MesosServer";
    private static final int HEARTBEAT_INTERVAL_S = 5;
    private static final int INITIAL_RETRY_DELAY_S = 2;
    private static final int MAX_RETRY_DELAY_S = 30;

    private final String host;
    private final int port;

    // Set in connect(); used by all VirtualView callbacks
    private ViewInterface view;

    private volatile VirtualServer server;
    private ScheduledExecutorService heartbeat;

    /**
     * @param host: RMI registry host
     * @param port: RMI registry port
     * @throws RemoteException required by UnicastRemoteObject
     * Constructor of this class
     */
    public RmiClient(String host, int port) throws RemoteException {
        super();
        this.host = host;
        this.port = port;
    }

    /**
     * @param view : concrete view (GUI or TUI) to notify
     * @throws IOException if the registry lookup or initial remote call fails
     * @throws RemoteException
     */
    public void connect(ViewInterface view) throws IOException {
        this.view = view;
        doConnect();
    }

    /**
     * @throws IOException : if the registry lookup or remote call fails
     * (Ri)Connects to the RMI server and wires up the full client stack: by
     * looking up the VirtualServer stub in the registry.
     * It also creates the ClientViewRMI and injects it into the view via
     * ViewInterface.init()so that the view can send operations.
     */
    public void doConnect() throws IOException {
        try {
            //looking up the VirtualServer stub in the registry and creates the ClientViewRMI
            Registry registry = LocateRegistry.getRegistry(host, port);
            server = (VirtualServer) registry.lookup(SERVER_NAME);

            // Inject the sender: from this point the view can call sendOperation()
            ClientViewRMI sender = new ClientViewRMI(server, this);
            view.init(sender);

            // Store for callbacks using connect
            server.connect(this);

            startHeartbeat();

        } catch (NotBoundException e) {
            throw new IOException("Server not found in RMI registry: " + e.getMessage(), e);
        }
    }

    /**
     * This method starts a periodic ping loop.  If any ping call throws a
     * RemoteException the connection is considered lost: the heartbeat
     * is stopped, the view is notified, and a reconnection retry loop begins.
     */
    private void startHeartbeat() {
        stopHeartbeat();
        heartbeat = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "rmi-heartbeat");
            t.setDaemon(true); // must not prevent JVM shutdown
            return t;
        });
        heartbeat.scheduleAtFixedRate(() -> {
            try {
                server.ping(this);
            } catch (RemoteException e) {
                System.err.println("[RmiClient] heartbeat failed: " + e.getMessage());
                stopHeartbeat();
                view.getUIDispatcher().accept(() ->
                        view.showPlayerDisconnected("server"));
                scheduleReconnect();
            }
        }, HEARTBEAT_INTERVAL_S, HEARTBEAT_INTERVAL_S, TimeUnit.SECONDS);
    }

    /** Shuts down the heartbeat scheduler without blocking. */
    private void stopHeartbeat() {
        if (heartbeat != null && !heartbeat.isShutdown()) {
            heartbeat.shutdownNow();
            heartbeat = null;
        }
    }

    /**
     * This method launches a background thread that retries doConnect() with
     * exponential backoff (2 s → 4 s → … capped at 30 s).
     * Once the connection is re-established the player is prompted to log in again.
     * The server's LobbyController will recognise
     * the nickname as a reconnection and resume the game.
     */
    private void scheduleReconnect() {
        new Thread(() -> {
            int delayS = INITIAL_RETRY_DELAY_S;
            while (true) {
                System.out.println("[RmiClient] reconnecting in " + delayS + "s …");
                try {
                    TimeUnit.SECONDS.sleep(delayS);
                    doConnect();
                    return;
                } catch (IOException e) {
                    System.err.println("[RmiClient] reconnect failed: " + e.getMessage());
                    delayS = Math.min(delayS * 2, MAX_RETRY_DELAY_S);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "rmi-reconnect").start();
    }

    /**
     * @param isFirst : for the first player it has to ask how many players will be needed to play a game
     * @throws RemoteException
     */
    @Override
    public void onAck(boolean isFirst) throws RemoteException {
        if (isFirst) view.getUIDispatcher().accept(() -> view.askNumPlayers());
        else view.getUIDispatcher().accept(() -> view.askLogin());
    }

    /**
     * @param result
     * @param name
     * @param color
     * @param lobbyPlayers
     * @throws RemoteException
     */
    @Override
    public void onLogged(boolean result, String name, String color,
                         List<String> lobbyPlayers) throws RemoteException {
        if (result) {
            view.getUIDispatcher().accept(() -> view.showLobby(lobbyPlayers));
        } else {
            if (lobbyPlayers.size() >= 2)
                view.getUIDispatcher().accept(() -> view.invalidChoice("The lobby is full"));
            else {
                view.getUIDispatcher().accept(() -> view.invalidChoice("Name or color already used"));
                view.getUIDispatcher().accept(() -> view.askLogin());
            }
        }
    }

    /**
     * @param offerTrack
     * @param tile
     * @param upperRow
     * @param lowerRow
     * @param buildingUpperRow
     * @param buildingLowerRow
     * @throws RemoteException
     */
    @Override
    public void onGameStarted(List<OfferTile> offerTrack, TurnOrderTile tile,
                              List<TribeCard> upperRow, List<TribeCard> lowerRow,
                              List<BuildingCard> buildingUpperRow,
                              List<BuildingCard> buildingLowerRow) throws RemoteException {
        view.getUIDispatcher().accept(() -> view.showGameStart());
        view.getUIDispatcher().accept(() -> view.updateOfferTrack(offerTrack));
        view.getUIDispatcher().accept(() -> view.updateTurnOrder(tile));
        view.getUIDispatcher().accept(() -> view.updateRows(upperRow, lowerRow, buildingUpperRow, buildingLowerRow));
    }

    /**
     * @param upperCount
     * @param lowerCount
     * @param cardsUpper
     * @param cardsLower
     * @param upperRow
     * @param lowerRow
     * @param buildingUpperRow
     * @param buildingLowerRow
     * @throws RemoteException
     */
    @Override
    public void onSelectCard(int upperCount, int lowerCount, int cardsUpper, int cardsLower,
                             List<TribeCard> upperRow, List<TribeCard> lowerRow,
                             List<BuildingCard> buildingUpperRow,
                             List<BuildingCard> buildingLowerRow) throws RemoteException {
        view.getUIDispatcher().accept(() ->
                view.selectCard(upperCount, lowerCount, cardsUpper, cardsLower,
                upperRow, lowerRow, buildingUpperRow, buildingLowerRow));
    }

    /**
     * @param freeSlots
     * @throws RemoteException
     */
    @Override
    public void onMoveTotem(List<Character> freeSlots) throws RemoteException {
        view.getUIDispatcher().accept(() -> view.placeTotem(freeSlots));
    }

    /**
     * @param upperRow
     * @param lowerRow
     * @param buildingUpperRow
     * @param buildingLowerRow
     * @throws RemoteException
     */
    @Override
    public void onUpdateBoard(List<TribeCard> upperRow, List<TribeCard> lowerRow,
                              List<BuildingCard> buildingUpperRow,
                              List<BuildingCard> buildingLowerRow) throws RemoteException {
        view.getUIDispatcher().accept(() -> view.updateRows(upperRow, lowerRow, buildingUpperRow, buildingLowerRow));
    }

    /**
     * @param names
     * @param foods
     * @param pps
     * @throws RemoteException
     */
    @Override
    public void onUpdateAllPlayers(List<String> names, List<Integer> foods,
                                   List<Integer> pps,
                                   List<Map<CharacterEnum, List<String>>> tribeDesc,
                                   List<List<String>> buildingDesc) throws RemoteException {
        view.getUIDispatcher().accept(() -> view.updateAllPlayers(names, foods, pps, tribeDesc, buildingDesc));
    }

    /**
     * @param offerTrack
     * @param turnOrderTile
     * @throws RemoteException
     */
    @Override
    public void onUpdateOfferTrack(List<OfferTile> offerTrack,
                                   TurnOrderTile turnOrderTile) throws RemoteException {
        view.getUIDispatcher().accept(() -> view.updateOfferTrack(offerTrack));
        view.getUIDispatcher().accept(() -> view.updateTurnOrder(turnOrderTile));
    }

    /**
     * @param currentRound
     * @throws RemoteException
     */
    @Override
    public void onUpdateRound(int currentRound) throws RemoteException {
        view.getUIDispatcher().accept(() -> view.updateRound(currentRound));
    }

    /**
     * @param upperRow
     * @param lowerRow
     * @param buildingUpperRow
     * @param buildingLowerRow
     * @throws RemoteException
     */
    @Override
    public void onUpdateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow,
                             List<BuildingCard> buildingUpperRow,
                             List<BuildingCard> buildingLowerRow) throws RemoteException {
        view.getUIDispatcher().accept(() -> view.updateRows(upperRow, lowerRow, buildingUpperRow, buildingLowerRow));
    }

    /**
     * @param tribe
     * @throws RemoteException
     */
    @Override
    public void onValidCards(Map<CharacterEnum, List<CharacterCard>> tribe) throws RemoteException {
        view.getUIDispatcher().accept(() -> view.showValidCards(tribe));
    }

    /**
     * @param message
     * @throws RemoteException
     */
    @Override
    public void onInvalidChoice(String message) throws RemoteException {
        view.getUIDispatcher().accept(() -> view.invalidChoice(message));
    }

    /**
     * @param winners
     * @param finalScores
     * @throws RemoteException
     */
    @Override
    public void onEndGame(List<String> winners, Map<String, Integer> finalScores) throws RemoteException {
        view.getUIDispatcher().accept(() -> view.showFinalScore(winners, finalScores));
    }

    /**
     * @param names
     * @param tribes
     * @throws RemoteException
     */
    @Override
    public void onUpdateAllTribes(List<String> names,
                                  List<Map<CharacterEnum, List<CharacterCard>>> tribes)
            throws RemoteException {
        view.getUIDispatcher().accept(() -> view.updateAllTribes(names, tribes));
    }

    /**
     * @param playerName
     * @throws RemoteException
     */
    @Override
    public void onPlayerDisconnected(String playerName) throws RemoteException {
        view.getUIDispatcher().accept(() -> view.showPlayerDisconnected(playerName));
    }

    /**
     * @param playerName
     * @throws RemoteException
     */
    @Override
    public void onPlayerReconnected(String playerName) throws RemoteException {
        view.getUIDispatcher().accept(() -> view.showPlayerReconnected(playerName));
    }

    /**
     * @param timeoutSeconds seconds before the remaining player is declared winner.
     * @throws RemoteException
     */
    @Override
    public void onGameSuspended(int timeoutSeconds) throws RemoteException {
        view.getUIDispatcher().accept(() -> view.showGameSuspended(timeoutSeconds));
    }

    /**
     * @throws RemoteException
     */
    @Override
    public void onGameResumed() throws RemoteException {
        view.getUIDispatcher().accept(view::showGameResumed);
    }

    /**
     * @param totemColor : right old color
     */
    @Override
    public void onReconnectedTotem(ColorEnum totemColor) throws RemoteException {
        view.getUIDispatcher().accept(()-> view.showReconnectedTotem(totemColor));
    }

    /**
     * @throws RemoteException
     * Ping server-->client. If rhe connection is down, it throws RemoteException
     */
    @Override
    public void onPing() throws RemoteException {}
}