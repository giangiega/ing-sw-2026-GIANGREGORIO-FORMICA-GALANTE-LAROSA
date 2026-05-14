package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.network.ClientSender;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


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
    void showValidCards(Map<CharacterEnum, List<CharacterCard>> tribe);
    void updateAllTribes(List<String> names, List<Map<CharacterEnum, List<CharacterCard>>> tribes);
}
