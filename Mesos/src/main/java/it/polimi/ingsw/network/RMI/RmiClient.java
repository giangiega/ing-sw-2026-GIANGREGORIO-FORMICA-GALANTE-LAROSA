/**
 * @author Giuse
 */
package it.polimi.ingsw.network.RMI;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.userInterface.ViewInterface;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

public class RmiClient extends UnicastRemoteObject implements VirtualView {

    private static final String SERVER_NAME = "MesosServer";

    private final String host;
    private final int port;

    // Set in connect(); used by all VirtualView callbacks
    private ViewInterface view;

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
     * @param view: the concrete view (TUIView or GUIView) to notify
     * @throws IOException : if the registry lookup or remote call fails
     * Connects to the RMI server and wires up the full client stack: by
     * looking up the VirtualServer stub in the registry.
     * It also creates the ClientViewRMI and injects it into the view via
     * ViewInterface.init()so that the view can send operations.
     */
    public void connect(ViewInterface view) throws IOException {
        try {
            //looking up the VirtualServer stub in the registry and creates the ClientViewRMI
            Registry registry = LocateRegistry.getRegistry(host, port);
            VirtualServer server = (VirtualServer) registry.lookup(SERVER_NAME);

            // Inject the sender: from this point the view can call sendOperation()
            ClientViewRMI sender = new ClientViewRMI(server, this);
            view.init(sender);

            // Store for callbacks
            this.view = view;

            // Register this callback object — server replies with AckEvent
            server.connect(this);

        } catch (NotBoundException e) {
            throw new IOException("Server not found in RMI registry: " + e.getMessage(), e);
        }
    }

    /**
     * @param isFirst : for the first player it has to ask how many players will be needed to play a game
     * @throws RemoteException
     */
    @Override
    public void onAck(boolean isFirst) throws RemoteException {
        if (isFirst) view.askNumPlayers();
        else view.askLogin();
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
            view.showLobby(lobbyPlayers);
        } else {
            if (lobbyPlayers.size() >= 2)
                view.invalidChoice("The lobby is full");
            else {
                view.invalidChoice("Name or color already used");
                view.askLogin();
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
        view.showGameStart();
        view.updateOfferTrack(offerTrack);
        view.updateTurnOrder(tile);
        view.updateRows(upperRow, lowerRow, buildingUpperRow, buildingLowerRow);
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
        view.selectCard(upperCount, lowerCount, cardsUpper, cardsLower,
                upperRow, lowerRow, buildingUpperRow, buildingLowerRow);
    }

    /**
     * @param freeSlots
     * @throws RemoteException
     */
    @Override
    public void onMoveTotem(List<Character> freeSlots) throws RemoteException {
        view.placeTotem(freeSlots);
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
        view.updateRows(upperRow, lowerRow, buildingUpperRow, buildingLowerRow);
    }

    /**
     * @param names
     * @param foods
     * @param pps
     * @throws RemoteException
     */
    @Override
    public void onUpdateAllPlayers(List<String> names, List<Integer> foods,
                                   List<Integer> pps) throws RemoteException {
        view.updateAllPlayers(names, foods, pps);
    }

    /**
     * @param offerTrack
     * @param turnOrderTile
     * @throws RemoteException
     */
    @Override
    public void onUpdateOfferTrack(List<OfferTile> offerTrack,
                                   TurnOrderTile turnOrderTile) throws RemoteException {
        view.updateOfferTrack(offerTrack);
        view.updateTurnOrder(turnOrderTile);
    }

    /**
     * @param currentRound
     * @throws RemoteException
     */
    @Override
    public void onUpdateRound(int currentRound) throws RemoteException {
        view.updateRound(currentRound);
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
        view.updateRows(upperRow, lowerRow, buildingUpperRow, buildingLowerRow);
    }

    /**
     * @param tribe
     * @throws RemoteException
     */
    @Override
    public void onValidCards(Map<CharacterEnum, List<CharacterCard>> tribe) throws RemoteException {
        view.showValidCards(tribe);
    }

    /**
     * @param message
     * @throws RemoteException
     */
    @Override
    public void onInvalidChoice(String message) throws RemoteException {
        view.invalidChoice(message);
    }

    /**
     * @param winners
     * @param finalScores
     * @throws RemoteException
     */
    @Override
    public void onEndGame(List<String> winners, Map<String, Integer> finalScores) throws RemoteException {
        view.showFinalScore(winners, finalScores);
    }
}