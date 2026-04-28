package it.polimi.ingsw.network;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.*;

import java.util.List;
import java.util.Map;



public interface ViewInterface {
    void showLobby(List<String> lobby);
    void showGame();
    void updateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow);
    void updateofferTrack(List<OfferTile> offerTrack);
    void updatePlayer(String name, int food, int prestigePoints, Map<CharacterEnum,List<CharacterCard>>tribe);
    void updateTurnOrder(List<String> turnOrder);
    void selectCard(int upperCount, int lowerCount);
    void selectBuilding(List<BuildingCard> availableBuilding);
    void placeTotem(List<Character> freeSlots);
    void eventResult(String eventName, EventOutcome outcomes);
    void invalidChoice(String message); // il messaggio dipenderà dal tipo di errore
    void showFinalScore(Map< String , Integer> ranking);
    void showValidCards(List<CharacterCard> tribe);
    void showLoginScreen();

    // serve una classe che permetta ad ogni player di vedere le carte/pp/food degli altri players



}
