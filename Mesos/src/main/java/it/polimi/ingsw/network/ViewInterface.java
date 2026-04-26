package it.polimi.ingsw.network;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.*;

import java.util.List;
import java.util.Map;



public interface ViewInterface {
    void showLobby(List<String> lobby);
    void showGame();
    void updateupperRows(List<TribeCard> upperRow);
    void updatelowerRows(List<TribeCard> lowerRow);
    void updateofferTrack(List<OfferTile> offerTrack);
    void updatePlayer(String name, int food, int prestigePoints, Map<CharacterEnum,List<CharacterCard>>tribe);
    void updateTurnOrder(List<String> turnOrder);
    void selectCard(int numberOfCards, String row);
    void selectBuilding(List<BuildingCard> availableBuilding);
    void placeTotem(List<Integer> freeSlots);
    void eventResult(String eventName, EventOutcome outcomes);
    void invalidChoice(String message); // il messaggio dipenderà dal tipo di errore
    void showFinalScore(Map< String , Integer> ranking);



}
