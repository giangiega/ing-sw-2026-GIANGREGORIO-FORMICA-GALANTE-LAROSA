package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.database.RankingRow;
import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.network.ClientSender;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * last 2 methods are defined as default because TUI doesn't need them.
 * Those methods used to manage the update of every player's tribe, now is managed in updateAllPLayers().
 * GUI needs those 2 methods, so it must override them.
 */

public interface ViewInterface {
    Consumer<Runnable> getUIDispatcher();
    void init(ClientSender sender);
    void showLobby(List<String> lobby);
    void askNumPlayers();
    void askLogin();
    void showGameStart();
    void updateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow, List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow);
    void updateOfferTrack(List<OfferTile> offerTrack);
    void updateAllPlayers(List<String> names, List<Integer> foods, List<Integer> pps, List<Map<CharacterEnum, List<String>>> tribeDesc,  List<List<String>> buildingDesc);
    void updateRound(int currentRound);
    void updateTurnOrder(TurnOrderTile turnOrder);
    void selectCard(int upperCount, int lowerCount, int cardsUpper, int cardsLower,List<TribeCard> upperRow, List<TribeCard> lowerRow, List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow);
    void placeTotem(List<Character> freeSlots);
    void invalidChoice(String message);
    void showFinalScore(List<String> winners, Map< String , Integer> finalScores);
    void showLeaderboard(List<RankingRow> ranking, Map<String, Integer> playersPosition);
    void showWaitingForRecovery(int playersStillNeeded);
    void resetInputState();
    void showServerCrashed();


    /**
     * This two methods are defined as default because TUI doesn't need them.
     * Those methods used to manage the update of every player's tribe, now is managed in updateAllPlayers()
     * GUI needs those 2 methods, so it must override them
     *
     */
    default void showValidCards(Map<CharacterEnum, List<CharacterCard>> tribe, List<BuildingCard> buildings){}
    default void updateAllTribes(List<String> names, List<Map<CharacterEnum, List<CharacterCard>>> tribes, List<List<BuildingCard>> buildings){}


    /**
     * @author Giuse
     * @param playerName : name of the player who left the game
     * This method notifies that a player has disconnected mid-game
     */
    void showPlayerDisconnected(String playerName);

    /**
     * @author Giuse
     * @param playerName : name of the player who returned to the game
     * This method notifies that a previously disconnected player has reconnected
     */
    void showPlayerReconnected(String playerName);

    /**
     * @author Giuse
     * @param timeoutSeconds seconds before the remaining player is declared winner by timeout
     * This method notifies that only one player is left and the game is now paused
     */
    void showGameSuspended(int timeoutSeconds);

    /**
     * @author Giuse
     * This method notifies that a second player reconnected and the game is resuming
     */
    void showGameResumed();

    /**
     * @author Giuse
     * @param totemColor
     * This method tells the reconnected player what the color of is totem before disconnecting
     */
    void showReconnectedTotem(ColorEnum totemColor);
}
