package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.*;

import java.util.List;
import java.util.Map;



public interface ViewInterface {
    void showLobby(List<String> lobby);
    void askNumPlayers();
    void showLoginScreen();
    void showOtherPlayers(/* capire parametri */);
    void updateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow);
    void updateOfferTrack(List<OfferTile> offerTrack);
    void updatePlayer(String name, int food, int prestigePoints, Map<CharacterEnum,List<CharacterCard>>tribe);
    void updateTurnOrder(List<String> turnOrder);
    void selectCard(int upperCount, int lowerCount);
    void selectBuilding(List<BuildingCard> availableBuilding);
    void placeTotem(List<Character> freeSlots);
    //void eventResult(String eventName, Map<Player, EventOutcome> playerEventOutcome);
    // capire come prendere pp/food influenzati dall'evento
    void invalidChoice(String message); // il messaggio dipenderà dal tipo di errore
    void showFinalScore(Map< String , Integer> ranking);
    void showValidCards(Map<CharacterEnum, List<CharacterCard>> tribe);





}
