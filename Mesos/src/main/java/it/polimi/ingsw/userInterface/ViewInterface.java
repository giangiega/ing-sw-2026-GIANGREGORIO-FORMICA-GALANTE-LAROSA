package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.network.socket.ClientViewSocket;

import java.util.List;
import java.util.Map;



public interface ViewInterface {
    void init(ClientViewSocket sender);
    void showLobby(List<String> lobby);
    void askNumPlayers();
    void askLogin();
    void showGameStart();
    void updateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow, List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow);
    void updateOfferTrack(List<OfferTile> offerTrack);
    void updateAllPlayers(List<String> names, List<Integer> foods, List<Integer> pps);
    void updateRound(int currentRound);
    void updateTurnOrder(TurnOrderTile turnOrder);
    void selectCard(int upperCount, int lowerCount, int cardsUpper, int cardsLower,List<TribeCard> upperRow, List<TribeCard> lowerRow, List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow);
    void placeTotem(List<Character> freeSlots);
    void invalidChoice(String message);
    void showFinalScore(List<String> winners, Map< String , Integer> finalScores);
    void showValidCards(Map<CharacterEnum, List<CharacterCard>> tribe);
}
