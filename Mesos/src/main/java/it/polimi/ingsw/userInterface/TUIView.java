package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.BuildingCard;
import it.polimi.ingsw.model.CharacterCard;
import it.polimi.ingsw.model.OfferTile;
import it.polimi.ingsw.model.TribeCard;
import it.polimi.ingsw.network.ClientViewSocket;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TUIView implements ViewInterface {
    private final Scanner scanner;
    private ClientViewSocket sender;

    public TUIView() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void showLobby(List<String> lobby) {

    }

    @Override
    public void askNumPlayers() {

    }

    @Override
    public void showLoginScreen() {

    }

    @Override
    public void showOtherPlayers(/* capire parametri */) {

    }

    @Override
    public void updateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow) {

    }

    @Override
    public void updateOfferTrack(List<OfferTile> offerTrack) {

    }

    @Override
    public void updatePlayer(String name, int food, int prestigePoints, Map<CharacterEnum,List<CharacterCard>> tribe) {

    }

    @Override
    public void updateTurnOrder(List<String> turnOrder) {

    }

    @Override
    public void selectCard(int upperCount, int lowerCount) {

    }

    @Override
    public void selectBuilding(List<BuildingCard> availableBuilding) {

    }

    @Override
    public void placeTotem(List<Character> freeSlots) {

    }

    @Override
    //void eventResult(String eventName, Map<Player, EventOutcome> playerEventOutcome);
    // capire come prendere pp/food influenzati dall'evento
    public void invalidChoice(String message) {     // il messaggio dipenderà dal tipo di errore

    }

    @Override
    public void showFinalScore(Map< String , Integer> ranking) {

    }

    @Override
    public void showValidCards(Map<CharacterEnum, List<CharacterCard>> tribe) {

    }
}
