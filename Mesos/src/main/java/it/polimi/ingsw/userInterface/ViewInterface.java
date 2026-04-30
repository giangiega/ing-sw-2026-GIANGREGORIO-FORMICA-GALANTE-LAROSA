package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.ClientViewSocket;

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
    void updatePlayer(String name, int food, int prestigePoints, Map<CharacterEnum,List<CharacterCard>>tribe);
    void updateTurnOrder(List<String> turnOrder);
    void selectCard(int upperCount, int lowerCount);

    void placeTotem(List<Character> freeSlots);
    //void eventResult(String eventName, Map<Player, EventOutcome> playerEventOutcome);
    // capire come prendere pp/food influenzati dall'evento
    void invalidChoice(String message);
    void showFinalScore(List<String> winners, Map< String , Integer> finalScores);
    void showValidCards(Map<CharacterEnum, List<CharacterCard>> tribe);





}
