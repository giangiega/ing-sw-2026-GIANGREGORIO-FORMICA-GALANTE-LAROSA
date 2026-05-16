package it.polimi.ingsw.userInterface;
import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.network.ClientSender;
import it.polimi.ingsw.network.clientInterface.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;




public class TUIView implements ViewInterface {
    private final Scanner scanner = new Scanner(System.in);
    private ClientSender sender;

    private static final String RESET = "\033[0m";
    private static final String CYAN = "\033[0;36m";
    private static final String YELLOW = "\033[0;33m";
    private static final String GREEN = "\033[0;32m";
    private static final String RED = "\033[0;31m";
    private static final String MAGENTA = "\033[0;35m";
    private static final String BLUE    = "\033[0;34m";
    private static final String BRIGHT_GRAY = "\033[0;90m";
    private static final String BOLD = "\033[1m";

    private final ExecutorService uiExecutor = Executors.newSingleThreadExecutor();

    public TUIView() {}

    @Override
    public Consumer<Runnable> getUIDispatcher() {
       return uiExecutor::execute;
    }

    @Override
    public void init(ClientSender sender) {
        this.sender = sender;
    }

    @Override
    public void showLobby(List<String> lobby) {
        System.out.println(CYAN + BOLD + "\n=== LOBBY ===" + RESET);
        System.out.println("Players connected: " + lobby);
        System.out.println("Waiting for more players...");
    }

    /**
     * Asks numPlayers during the first login
     */
    @Override
    public void askNumPlayers() {
        int n;
        while (true) {
            System.out.println(RED + BOLD + "----- MESOS LOGIN -----" + RESET);
            System.out.print(GREEN + BOLD + "Enter the number of players (2-5): " + RESET);
            if (scanner.hasNextInt()) {
                n = scanner.nextInt();
                if (n >= 2 && n <= 5)
                    break;
            } else scanner.next();

            System.out.println(RED + BOLD + "err: Invalid input" + RESET);
        }
            sender.sendOperation(new NumPlayersOperation(n));
            askLogin();
    }

    @Override
    public void askLogin() {
        System.out.print(GREEN + BOLD + "Enter your name: " + RESET);
        String name = scanner.next();

        System.out.println("Available colors: " +
                Arrays.toString(it.polimi.ingsw.enums.ColorEnum.values()));
        System.out.print(GREEN + BOLD + "Choose color: " + RESET);
        String color = scanner.next().toUpperCase();

        try {
            sender.sendOperation(new LoginOperation(name, ColorEnum.valueOf(color)));
        } catch (IllegalArgumentException e) {
            System.out.println(RED + BOLD + "err: Invalid color, try again." + RESET);
            askLogin();
        }
    }

    @Override
    public void showGameStart() {
        System.out.println(CYAN + "\n╔═════════════════════════════╗");
        System.out.println("║        GAME STARTED!        ║");
        System.out.println("╚═════════════════════════════╝" + RESET);
    }

    @Override
    public void updateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow) {
        System.out.println(BRIGHT_GRAY + BOLD + "\n--- Upper Row ---" + RESET);
        for (int i = 0; i < upperRow.size(); i++)
            System.out.println("  [" + i + "] " + upperRow.get(i));

        if (!buildingUpperRow.isEmpty()) {
            System.out.println(BRIGHT_GRAY + BOLD + "  \nBuildings Upper Row:" + RESET);
            for (int i = 0; i < buildingUpperRow.size(); i++)
                System.out.println("  [" + (i + upperRow.size()) + "] " + buildingUpperRow.get(i));
        }

        System.out.println(BRIGHT_GRAY + BOLD + "\n--- Lower Row ---" + RESET);
        for (int i = 0; i < lowerRow.size(); i++)
            System.out.println("  [" + i + "] " + lowerRow.get(i));

        if (!buildingLowerRow.isEmpty()) {
            System.out.println(BRIGHT_GRAY + BOLD + "  \nBuildings Lower Row:" + RESET);
            for (int i = 0; i < buildingLowerRow.size(); i++)
                System.out.println("  [" + (i + lowerRow.size()) + "] " + buildingLowerRow.get(i));
        }
    }

    @Override
    public void updateOfferTrack(List<OfferTile> offerTrack) {
        System.out.println(BRIGHT_GRAY + BOLD + "\n--- Offer Track ---" + RESET);
        for (OfferTile tile : offerTrack) {
            String occupant = tile.getFreeOfferTile() ? "free" : tile.getOccupant().getName();

            System.out.printf("  [%c] ↑%d ↓%d  → %s%n",
                    tile.getLetter(), tile.getCountUpperArrow(),
                    tile.getCountLowerArrow(), occupant);
        }
    }

    @Override
    public void updateTurnOrder(TurnOrderTile turnOrder) {
        StringBuilder sb = new StringBuilder();
        sb.append(BRIGHT_GRAY + BOLD + "--- Turn Order ---\n" + RESET);
        for (int i = 0; i < turnOrder.getSlots().size(); i++) {
            Player p = turnOrder.getSlots().get(i);
            if (p != null)
                sb.append(String.format("  %d. [%s] %s%n", i + 1, p.getTotemColor().name(), p.getName()));
            else
                sb.append(String.format("  %d. [ empty ]%n", i + 1));
        }
        System.out.println(sb);

    }

    @Override
    public void updateAllPlayers(List<String> names, List<Integer> foods, List<Integer> pps) {
        System.out.println(CYAN + BOLD + "\n============= PLAYERS STATUS =============" + RESET);
        for (int i = 0; i < names.size(); i++)
            System.out.printf("  %-15s  food: %2d   PP: %3d%n",
                    names.get(i), foods.get(i), pps.get(i));
        System.out.println(CYAN + BOLD + "=========================================" + RESET);
    }

    @Override
    public void updateRound(int currentRound) {
        System.out.printf(BLUE + BOLD + "\n\n ============= ROUND %2d / 10 =============\n" + RESET, currentRound);
    }

    @Override
    public void selectCard(int upperCount, int lowerCount, int cardsUpper, int cardsLower,
                           List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> upperBuildings, List<BuildingCard> lowerBuildings) {
        List<Integer> upperCards = new ArrayList<>();
        List<Integer> lowerCards = new ArrayList<>();
        List<Integer> upperBuildings_ = new ArrayList<>();
        List<Integer> lowerBuildings_ = new ArrayList<>();
        int input = 0;
        int count = 0;

        if(cardsUpper < upperCount) {
            if(!upperBuildings.isEmpty()) {
                System.out.println(GREEN + BOLD + "\nUpper row has not enough cards to pick.\n Do you want to pick buildings from upper row?");
                System.out.println("\nEnter [0] to choose buildings or [-1] to skip --> " + RESET);
                try {
                    input = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println(RED + BOLD + "err: scanner.nextInt() does not work" + RESET);
                    System.out.print(GREEN + BOLD + "Choose a correct index: " + RESET);
                    scanner.next();
                }
                while(input != -1 && input != 0) {
                    System.out.println(RED + BOLD + "err: Invalid input: " + RESET);
                    System.out.print(GREEN + BOLD + "Choose a correct index: " + RESET);
                    input = scanner.nextInt();
                }
                if(input == -1)
                    upperCount = cardsUpper;

            } else upperCount = cardsUpper;
        }

        if(cardsLower < lowerCount) {
            if(!lowerBuildings.isEmpty()) {
                System.out.println(GREEN + BOLD + "\nLower row has not enough cards to pick.\n Do you want to pick buildings from lower row?");
                System.out.println("\nEnter [0] to choose buildings or [-1] to skip --> " + RESET);
                try {
                    input = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println(RED + BOLD + "err: scanner.nextInt() does not work" + RESET);
                    System.out.print(GREEN + BOLD + "Choose a correct index: " + RESET);
                    scanner.next();
                }
                while(input != -1 && input != 0) {
                    System.out.println(RED + BOLD + "err: Invalid input" + RESET);
                    System.out.print(GREEN + BOLD + "Choose a correct index: " + RESET);
                    input = scanner.nextInt();
                }
                if(input == -1) {
                    lowerCount = cardsLower;
                    if(lowerCount == 0) {
                        System.out.println(RED + BOLD + "\nThere are no cards to pick, round skipped" + RESET);
                        sender.sendOperation(new ChooseCardOperation(
                                upperCards, lowerCards, upperBuildings_, lowerBuildings_));
                        return;
                    }
                }

            } else {
                lowerCount = cardsLower;
                if(lowerCount == 0) {
                    System.out.println(RED + BOLD + "\nThere are no cards to pick, round skipped" + RESET);
                    sender.sendOperation(new ChooseCardOperation(
                            upperCards, lowerCards, upperBuildings_, lowerBuildings_));
                    return;
                }
            }
        }

        if (upperCount > 0) {
            System.out.println(GREEN + BOLD + "\nChoose " + upperCount + " card(s) from upper row or building upper row.");
            System.out.print("Enter " + upperCount + " index/indices: " + RESET);

            while (count < upperCount) {
                try {
                    input = scanner.nextInt();
                    if (input < upperRow.size() && input >= 0)
                        upperCards.add(input);
                    else if (input < (upperRow.size() + upperBuildings.size()) && input >= upperRow.size())
                        upperBuildings_.add(input - (upperRow.size()));
                    else {
                        System.out.println(RED + BOLD + "err: Invalid index, try again" + RESET);
                        System.out.print(GREEN + BOLD + "Enter " + upperCount + " index/indices: " + RESET);
                        continue;
                    }
                    count++;
                } catch (InputMismatchException e){
                    System.out.println(RED + BOLD + "err: scanner.nextInt() does not work" + RESET);
                    System.out.print(GREEN + BOLD + "Choose a correct index: " + RESET);
                    scanner.next();
                }
            }
        }
        count = 0;
        if (lowerCount > 0) {
            System.out.println(GREEN + BOLD + "\nChoose " + lowerCount + " card(s) from lower row or building lower row.");
            System.out.print("Enter " + lowerCount + " index/indices: " + RESET);

            while (count < lowerCount) {
                try {
                    input = scanner.nextInt();
                    if (input < lowerRow.size() && input >= 0)
                        lowerCards.add(input);
                    else if (input < (lowerRow.size() + lowerBuildings.size()) && input >= lowerRow.size())
                        lowerBuildings_.add(input - (lowerRow.size()));
                    else {
                        System.out.println(RED + BOLD + "err: Invalid index, try again" + RESET);
                        System.out.print(GREEN + BOLD + "Enter " + lowerCount + " index/indices: " + RESET);
                        continue;
                    }
                    count++;
                } catch (InputMismatchException e){
                    System.out.println(RED + BOLD + "err: scanner.nextInt() does not work" + RESET);
                    System.out.print(GREEN + BOLD + "Choose a correct index: " + RESET);
                    scanner.next();
                }
            }
        }

        sender.sendOperation(new ChooseCardOperation(
                upperCards, lowerCards, upperBuildings_, lowerBuildings_));
    }

    @Override
    public void placeTotem(List<Character> freeSlots) {
        System.out.println("\nFree tiles: " + freeSlots);
        System.out.print(GREEN + BOLD + "Choose a tile letter: " + RESET);
        try {
            char letter = scanner.next().toUpperCase().charAt(0);
            while(!freeSlots.contains(letter)) {
                System.out.println(RED + BOLD + "err: Tile already occupied or invalid, choose another" + RESET);
                System.out.println("\nFree tiles: " + freeSlots);
                System.out.print(GREEN + BOLD + "Choose a tile letter: " + RESET);
                letter = scanner.next().toUpperCase().charAt(0);
            }
            sender.sendOperation(new PlaceTotemOperation(letter));
        } catch (InputMismatchException e) {
            System.out.println(RED + BOLD + "err: scanner.next() does not work" + RESET);
            System.out.print(GREEN + BOLD + "Choose a correct index: " + RESET);
            scanner.next();
        }
    }

    @Override
    public void invalidChoice(String message) {
        System.out.println(RED + BOLD + "err: " + message + RESET);
    }

    @Override
    public void showValidCards(Map<CharacterEnum, List<CharacterCard>> tribe) {
        System.out.println(MAGENTA + BOLD + "\n--- Your updated tribe ---" + RESET);
        for (Map.Entry<CharacterEnum, List<CharacterCard>> entry : tribe.entrySet()) {
            if (!entry.getValue().isEmpty())
                System.out.println("  " + entry.getKey() + ": " + entry.getValue().size());
        }
    }

    @Override
    public void showFinalScore(List<String> winners, Map< String , Integer> finalScores) {
        System.out.println(CYAN + BOLD + "\n╔══════════════════════════╗");
        System.out.println("║       GAME OVER!         ║");
        System.out.println("╚══════════════════════════╝" + RESET);

        System.out.println(YELLOW + BOLD + "Winner(s): " + RESET + winners);

        System.out.println(YELLOW + BOLD + "\n--- Final Scores ---" + RESET);
        finalScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> System.out.printf("  %-15s %3d PP%n",
                        e.getKey(), e.getValue()));

    }
    @Override
    public void updateAllTribes(List<String> names, List<Map<CharacterEnum, List<CharacterCard>>> tribes){
        for (int i = 0; i < names.size(); i++) {
            System.out.println(MAGENTA + BOLD + names.get(i) + "'s tribe:" + RESET);
            for (var entry : tribes.get(i).entrySet()) {
                if (!entry.getValue().isEmpty())
                    System.out.println("  " + entry.getKey() + ": " + entry.getValue().size());
            }
        }
    }

    @Override
    public void showPlayerDisconnected(String playerName) {
        System.out.println(RED + BOLD + "\nPlayer disconnected: " + RESET + playerName + RESET);
    }

    @Override
    public void showPlayerReconnected(String playerName) {
        System.out.println(GREEN + BOLD + "\nPlayer reconnected: " + RESET + playerName + RESET);
    }

    @Override
    public void showGameSuspended(int timeoutSeconds) {
        System.out.println(YELLOW + BOLD + "\nGame suspended! Waiting for players to reconnect... " + RESET);
        System.out.println(YELLOW + "Timeout: " + timeoutSeconds + " seconds. If no one returns, the remaining player wins." + RESET);
    }

    @Override
    public void showGameResumed() {
        System.out.println(GREEN + BOLD + "\nGame resumed! Continuing..." + RESET);
    }

    @Override
    public void showReconnectedTotem(ColorEnum totemColor) {
        System.out.println(YELLOW + "\nYour original totem was " + totemColor + RESET);
    }
}

