package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.ClientViewSocket;
import it.polimi.ingsw.network.clientInterface.*;

import java.util.*;

public class TUIView implements ViewInterface {
    private final Scanner scanner = new Scanner(System.in);
    private ClientViewSocket sender;

    public TUIView() {}

    @Override
    public void init(ClientViewSocket sender) {
        this.sender = sender;
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
    public void showGameStart() {

    }

    @Override
    public void updateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow) {

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

    /* capire se fare anche selectBuilding, perchè ho fatto sia ChooseCardOperation che resolveAction()
    del model trattando insieme sia le rows che le building rows*/

    @Override
    public void placeTotem(List<Character> freeSlots) {
        System.out.println("\nFree tiles: " + freeSlots);
        System.out.print("Choose a tile letter: ");
        char letter = scanner.next().toUpperCase().charAt(0);
        sender.sendOperation(new PlaceTotemOperation(letter));
    }

    @Override
    public void invalidChoice(String message) {
        System.out.println("err: " + message);
    }

    @Override
    public void showFinalScore(List<String> winners, Map< String , Integer> finalScores) {

    }

    @Override
    public void showValidCards(Map<CharacterEnum, List<CharacterCard>> tribe) {

    }
}
