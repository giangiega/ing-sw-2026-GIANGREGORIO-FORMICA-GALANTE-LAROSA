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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface VirtualView extends Remote {

    /** Sent to the very first client (isFirst=true) or subsequent ones (false). */
    void onAck(boolean isFirst) throws RemoteException;

    /** Result of a login attempt; on success the current lobby list is provided. */
    void onLogged(boolean result, String name, String color,
                  List<String> lobbyPlayers) throws RemoteException;

    /** Fired when all players are connected and the game begins. */
    void onGameStarted(List<OfferTile> offerTrack, TurnOrderTile tile,
                       List<TribeCard> upperRow, List<TribeCard> lowerRow,
                       List<BuildingCard> buildingUpperRow,
                       List<BuildingCard> buildingLowerRow) throws RemoteException;

    /** It is this player's turn to choose cards. */
    void onSelectCard(int upperCount, int lowerCount, int cardsUpper, int cardsLower,
                      List<TribeCard> upperRow, List<TribeCard> lowerRow,
                      List<BuildingCard> buildingUpperRow,
                      List<BuildingCard> buildingLowerRow) throws RemoteException;

    /** It is this player's turn to place their totem. */
    void onMoveTotem(List<Character> freeSlots) throws RemoteException;

    /** The card rows have changed (cards were drawn). */
    void onUpdateBoard(List<TribeCard> upperRow, List<TribeCard> lowerRow,
                       List<BuildingCard> buildingUpperRow,
                       List<BuildingCard> buildingLowerRow) throws RemoteException;

    /** Global player status update (food and prestige points for every player). */
    void onUpdateAllPlayers(List<String> names, List<Integer> foods,
                            List<Integer> pps) throws RemoteException;

    /** The offer track and turn-order tile have changed. */
    void onUpdateOfferTrack(List<OfferTile> offerTrack,
                            TurnOrderTile turnOrderTile) throws RemoteException;

    /** The current round number has changed. */
    void onUpdateRound(int currentRound) throws RemoteException;

    /** The card rows have been refreshed. */
    void onUpdateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow,
                      List<BuildingCard> buildingUpperRow,
                      List<BuildingCard> buildingLowerRow) throws RemoteException;

    /** Shows the player which character cards in their tribe are valid this round. */
    void onValidCards(Map<CharacterEnum, List<CharacterCard>> tribe) throws RemoteException;

    /** The last action was invalid; a descriptive message is provided. */
    void onInvalidChoice(String message) throws RemoteException;

    /** The game has ended; final scores and winners are provided. */
    void onEndGame(List<String> winners, Map<String, Integer> finalScores) throws RemoteException;

    void onUpdateAllTribes(List<String> names,
                           List<Map<CharacterEnum, List<CharacterCard>>> tribes)
            throws RemoteException;
}