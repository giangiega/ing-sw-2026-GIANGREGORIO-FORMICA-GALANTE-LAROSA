package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.ClientViewSocket;
import it.polimi.ingsw.network.clientInterface.*;

import java.util.Arrays;
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
        System.out.println("\n=== LOBBY ===");
        System.out.println("Players connected: " + lobby);
        System.out.println("Waiting for more players...");
    }

    /**
     * Asks numPlayers during the first login
     */
    @Override
    public void askNumPlayers() {
        int n = 0;
        while (n < 2 || n > 5) {
            System.out.print("You are the first player. How many players? (2-5): ");
            if (scanner.hasNextInt()) n = scanner.nextInt();
            else scanner.next();
        }
        sender.sendOperation(new NumPlayersOperation(n));
    }

    @Override
    public void askLogin() {
        System.out.print("Enter your name: ");
        String name = scanner.next();

        System.out.println("Available colors: " +
                Arrays.toString(it.polimi.ingsw.enums.ColorEnum.values()));
        System.out.print("Choose color: ");
        String color = scanner.next().toUpperCase();

        sender.sendOperation(new LoginOperation(name,
                it.polimi.ingsw.enums.ColorEnum.valueOf(color)));
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
