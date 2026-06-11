package it.polimi.ingsw.userInterface;
import it.polimi.ingsw.database.RankingRow;
import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.network.ClientSender;
import it.polimi.ingsw.network.clientInterface.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class TUIView implements ViewInterface {
    private ClientSender sender;
    private String nickname;

    private final LinkedBlockingQueue<String> inputQueue = new LinkedBlockingQueue<>();
    private static final String SUSPEND_SENTINEL = "\0SUSPEND";
    private volatile boolean inputSuspended = false;
    private ExecutorService inputExecutor = Executors.newSingleThreadExecutor(
            r -> { Thread t = new Thread(r, "input-handler"); t.setDaemon(true); return t; });

    private static final String RESET = "\033[0m";
    private static final String CYAN = "\033[0;36m";
    private static final String YELLOW = "\033[0;33m";
    private static final String GREEN = "\033[0;32m";
    private static final String RED = "\033[0;31m";
    private static final String MAGENTA = "\033[0;35m";
    private static final String BLUE = "\033[0;34m";
    private static final String BRIGHT_GRAY = "\033[0;90m";
    private static final String BOLD = "\033[1m";

    private final ExecutorService uiExecutor = Executors.newSingleThreadExecutor(
            r -> { Thread t = new Thread(r, "ui-display"); t.setDaemon(true); return t; }
    );

    private static final Object OUT_LOCK = System.out;
    private volatile String currentPrompt = null;

    /**
     * @author Giuse
     * This method re-print its prompt so it remains visible after a display event
     * has written over it. but only if an input method is currently waiting for the user.
     * Must be called while holding OUT_LOCK.
     */
    private void reprintPromptIfActive() {
        String p = currentPrompt;
        if (p != null) System.out.print("\n" + p);
    }


    public TUIView() {
        // Background thread that reads tokens from stdin and enqueues them.
        // Never touches uiExecutor or inputExecutor directly.
        Thread reader = new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (sc.hasNext()) {
                        inputQueue.put(sc.next());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "stdin-reader");
        reader.setDaemon(true);
        reader.start();
    }

    /**
     * @author Giuse
     * This method blocks until the next input is available, returning null if the game
     * is suspended (or becomes suspended while waiting).
     * Polls every 100 ms so suspension is detected promptly
     */
    private String takeInput() {
        try {
            while (true) {
                String token = inputQueue.poll(100, TimeUnit.MILLISECONDS);
                if (token == null) {
                    // timeout: re-check suspension flag
                    if (inputSuspended) return null;
                    continue;
                }
                if (SUSPEND_SENTINEL.equals(token)) return null;
                if (inputSuspended) continue; // discard tokens typed during suspension
                return token;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
    /**
     * @author Giuse
     * This method reads the next integer token, re-prompting on invalid input.
     * Returns Integer.MIN_VALUE if the game is suspended.
     */
    private int takeInt() {
        while (true) {
            String token = takeInput();
            if (token == null) return Integer.MIN_VALUE;
            try {
                return Integer.parseInt(token);
            } catch (NumberFormatException e) {
                System.out.println(RED + BOLD + "err: Invalid number, try again" + RESET);
            }
        }
    }

    /**
     * @author Giuse
     * This method is called by showGameSuspended.
     * It pauses input and wakes any blocked takeInput()
     * */
    private void suspendInput() {
        inputSuspended = true;
        inputQueue.offer(SUSPEND_SENTINEL);
    }

    /**
     * @author Giuse
     * This method is called by showGameResumed.
     * It discards anything typed during suspension and resumes
     * */
    private void resumeInput() {
        inputQueue.clear();
        inputSuspended = false;
    }

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
        synchronized (OUT_LOCK) {
            System.out.println(CYAN + BOLD + "\n=== LOBBY ===" + RESET);
            System.out.println("Players connected: " + lobby);
            System.out.println("Waiting for more players...");

        }
    }

    /**
     * Asks numPlayers during the first login
     * Delegates blocking input to inputExecutor so uiExecutor stays responsive.
     */
    @Override
    public void askNumPlayers(){
        inputExecutor.submit(this::doAskNumPlayers);
    }

    public void doAskNumPlayers() {
        int n;
        while (true) {
            synchronized (OUT_LOCK) {
                System.out.println(RED + BOLD + "----- MESOS LOGIN -----" + RESET);
                System.out.print(GREEN + BOLD + "Enter the number of players (2-5): " + RESET);
            }

            n = takeInt();

            if(n == Integer.MIN_VALUE) return; //Suspended
            if (n >= 2 && n <= 5)
                break;

            System.out.println(RED + BOLD + "err: Invalid input" + RESET);
        }
        sender.sendOperation(new NumPlayersOperation(n));
        doAskLogin();
    }

    @Override
    public void askLogin() {
        inputExecutor.submit(this::doAskLogin);
    }

    public void doAskLogin() {
        System.out.print(GREEN + BOLD + "Enter your name: " + RESET);
        String name = takeInput();
        if(name == null) return;

        synchronized (OUT_LOCK) {
            System.out.println("Available colors: " +
                    Arrays.toString(it.polimi.ingsw.enums.ColorEnum.values()));
            System.out.print(GREEN + BOLD + "Choose color: " + RESET);

        }

        String color = takeInput();
        if (color == null) return;
        color = color.toUpperCase();

        try {
            sender.sendOperation(new LoginOperation(name, ColorEnum.valueOf(color)));
            this.nickname = name;
        } catch (IllegalArgumentException e) {
            System.out.println(RED + BOLD + "err: Invalid color, try again." + RESET);
            doAskLogin();
        }
    }

    @Override
    public void showGameStart() {
        synchronized (OUT_LOCK) {
            System.out.println(YELLOW + BOLD + "\n╔═════════════════════════════╗");
            System.out.println("║        GAME STARTED!        ║");
            System.out.println("╚═════════════════════════════╝" + RESET);
        }
    }

    @Override
    public void updateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow) {
        synchronized (OUT_LOCK) {
            System.out.println(BRIGHT_GRAY + BOLD + "\n--- Upper Row ---" + RESET);
            for (int i = 0; i < upperRow.size(); i++)
                System.out.println("  [" + i + "] " + upperRow.get(i));

            if (!buildingUpperRow.isEmpty()) {
                System.out.println(BRIGHT_GRAY + BOLD + "  \nBuildings Upper Row:" + RESET);
                for (int i = 0; i < buildingUpperRow.size(); i++)
                    System.out.println("  [" + (i + upperRow.size()) + "] " + buildingUpperRow.get(i));
            }
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
        synchronized (OUT_LOCK) {
            System.out.println(BRIGHT_GRAY + BOLD + "\n--- Offer Track ---" + RESET);
            for (OfferTile tile : offerTrack) {
                String occupant = tile.getFreeOfferTile() ? "free" : tile.getOccupant().getName();

                System.out.printf("  [%c] ↑%d ↓%d  → %s%n",
                        tile.getLetter(), tile.getCountUpperArrow(),
                        tile.getCountLowerArrow(), occupant);
            }
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
    public void updateAllPlayers(List<String> names, List<Integer> foods, List<Integer> pps,
                                 List<Map<CharacterEnum, List<String>>> tribeDesc,
                                 List<List<String>> buildingDesc) {
        synchronized (OUT_LOCK){
            System.out.println(BLUE + BOLD + "\n============= PLAYERS STATUS =============" + RESET);
            for (int i = 0; i < names.size(); i++) {
                System.out.printf(MAGENTA + BOLD + "  %-15s  food: %2d   PP: %3d%n" + RESET,
                        names.get(i), foods.get(i), pps.get(i));

                System.out.printf(CYAN + BOLD + "  %s's tribe\n" + RESET, names.get(i));
                Map<CharacterEnum, List<String>> tribe = tribeDesc.get(i);
                for (CharacterEnum type : CharacterEnum.values()) {
                    List<String> cards = tribe.get(type);
                    if (cards != null && !cards.isEmpty()) {
                        for (String card : cards)
                            System.out.println("      - " + card);
                    }
                }
                System.out.printf(CYAN + BOLD + "  %s's buildings\n" + RESET, names.get(i));
                List<String> buildings = buildingDesc.get(i);
                if (!buildings.isEmpty())
                    for(String building : buildings)
                        System.out.println("      - " + building);
                System.out.print("\n");
            }
            System.out.println(BLUE + BOLD + "=========================================" + RESET);
            reprintPromptIfActive();
        }
    }

    @Override
    public void updateRound(int currentRound) {
        System.out.printf(BLUE + BOLD + "\n\n ============= ROUND %2d / 10 =============\n" + RESET, currentRound);
    }

    @Override
    public void selectCard(int upperCount, int lowerCount, int cardsUpper, int cardsLower,
                           List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> upperBuildings, List<BuildingCard> lowerBuildings){
        inputExecutor.submit(() -> doSelectCard(
                upperCount, lowerCount, cardsUpper, cardsLower,
                upperRow, lowerRow, upperBuildings, lowerBuildings));
    }


    public void doSelectCard(int upperCount, int lowerCount, int cardsUpper, int cardsLower,
                             List<TribeCard> upperRow, List<TribeCard> lowerRow,
                             List<BuildingCard> upperBuildings, List<BuildingCard> lowerBuildings) {
        List<Integer> upperCards = new ArrayList<>();
        List<Integer> lowerCards = new ArrayList<>();
        List<Integer> upperBuildings_ = new ArrayList<>();
        List<Integer> lowerBuildings_ = new ArrayList<>();

        //upper row hasn't got enough tribe cards -> offer buildings
        if (cardsUpper < upperCount) {
            if (!upperBuildings.isEmpty()) {
                synchronized (OUT_LOCK) {
                    String notEnoughUpperRow = GREEN + BOLD + "\nUpper row has not enough cards to pick." +
                            "\n Do you want to pick buildings from upper row?";
                    String chooseBuildingOrSkip = "\nEnter [0] to choose buildings or [-1] to skip --> " + RESET;

                    System.out.println(notEnoughUpperRow);
                    System.out.println(chooseBuildingOrSkip);

                    currentPrompt = notEnoughUpperRow + "\n" + chooseBuildingOrSkip;
                }

                int input;
                do {
                    input = takeInt();
                    if (input == Integer.MIN_VALUE) return;
                    if (input != -1 && input != 0)
                        System.out.print(RED + BOLD + "err: Invalid input. " + RESET +
                                GREEN + BOLD + "Choose a correct index: " + RESET);
                } while (input != -1 && input != 0);
                if (input == -1) upperCount = cardsUpper;
            } else {
                upperCount = cardsUpper;
            }
        }

        //lower row: not enough tribe cards -> offer buildings
        if (cardsLower < lowerCount) {
            if (!lowerBuildings.isEmpty()) {
                synchronized (OUT_LOCK) {
                    String notEnoughLowerRow = GREEN + BOLD + "\nLower row has not enough cards to pick." +
                            "\n Do you want to pick buildings from lower row?";
                    String chooseBuildingOrSkip = "\nEnter [0] to choose buildings or [-1] to skip --> " + RESET;

                    System.out.println(notEnoughLowerRow);
                    System.out.println(chooseBuildingOrSkip);

                    currentPrompt = notEnoughLowerRow + "\n" + chooseBuildingOrSkip;
                }
                int input;
                do {
                    input = takeInt();
                    if (input == Integer.MIN_VALUE) return;
                    if (input != -1 && input != 0)
                        System.out.print(RED + BOLD + "err: Invalid input. " + RESET +
                                "\n" + GREEN + BOLD + "Choose a correct index: " + RESET);
                } while (input != -1 && input != 0);
                if (input == -1) {
                    lowerCount = cardsLower;
                    if (lowerCount == 0) {
                        System.out.println(RED + BOLD + "\nThere are no cards to pick, round skipped" + RESET);
                        sender.sendOperation(new ChooseCardOperation(
                                upperCards, lowerCards, upperBuildings_, lowerBuildings_));
                        return;
                    }
                }
            } else {
                lowerCount = cardsLower;
                if (lowerCount == 0) {
                    System.out.println(RED + BOLD + "\nThere are no cards to pick, round skipped" + RESET);
                    sender.sendOperation(new ChooseCardOperation(
                            upperCards, lowerCards, upperBuildings_, lowerBuildings_));
                    return;
                }
            }
        }

        //pick from upper row
        if (upperCount > 0) {
            synchronized (OUT_LOCK) {
                String chooseUpperRowsCards = GREEN + BOLD + "\nChoose " + upperCount +
                        " card(s) from upper row or building upper row.";
                System.out.println(chooseUpperRowsCards);

                String enterIndexUpper = "Enter " + upperCount + " index/indices: " + RESET;
                System.out.print(enterIndexUpper);

                currentPrompt = chooseUpperRowsCards + "\n" +  enterIndexUpper;
            }
            int picked = 0;
            while (picked < upperCount) {
                int input = takeInt();
                if (input == Integer.MIN_VALUE) return;
                if (input >= 0 && input < upperRow.size()) {
                    upperCards.add(input); picked++;
                } else if (input >= upperRow.size() && input < upperRow.size() + upperBuildings.size()) {
                    upperBuildings_.add(input - upperRow.size()); picked++;
                } else {
                    System.out.println(RED + BOLD + "err: Invalid index, try again" + RESET);
                    System.out.print(GREEN + BOLD + "Enter " + upperCount + " index/indices: " + RESET);
                }
            }
        }

        //pick from lower row
        if (lowerCount > 0) {
            synchronized (OUT_LOCK) {
                String chooseLowerRowsCards = GREEN + BOLD + "\nChoose " + lowerCount +
                        " card(s) from lower row or building lower row.";
                System.out.println(chooseLowerRowsCards);

                String enterIndexLower = "Enter " + lowerCount + " index/indices: " + RESET;
                System.out.print(enterIndexLower);

                currentPrompt = chooseLowerRowsCards + "\n" + enterIndexLower;
            }
            int picked = 0;
            while (picked < lowerCount) {
                int input = takeInt();
                if (input == Integer.MIN_VALUE) return;
                if (input >= 0 && input < lowerRow.size()) {
                    lowerCards.add(input); picked++;
                } else if (input >= lowerRow.size() && input < lowerRow.size() + lowerBuildings.size()) {
                    lowerBuildings_.add(input - lowerRow.size()); picked++;
                } else {
                    System.out.println(RED + BOLD + "err: Invalid index, try again" + RESET);
                    System.out.print(GREEN + BOLD + "Enter " + lowerCount + " index/indices: " + RESET);
                }
            }
        }

        currentPrompt = null;
        sender.sendOperation(new ChooseCardOperation(
                upperCards, lowerCards, upperBuildings_, lowerBuildings_));
    }

    @Override
    public void placeTotem(List<Character> freeSlots) {
        inputExecutor.submit(() -> doPlaceTotem(freeSlots));

    }

    public void doPlaceTotem(List<Character> freeSlots) {
        try{
            while (true) {
                synchronized (OUT_LOCK) {
                    String freeTiles = "\nFree tiles: " + freeSlots;
                    String chooseATile = GREEN + BOLD + "Choose a tile letter: " + RESET;

                    System.out.println(freeTiles);
                    System.out.print(chooseATile);

                    currentPrompt = freeTiles + "\n" + chooseATile;

                }

                String token = takeInput();
                if (token == null) return; // suspended
                char letter = token.toUpperCase().charAt(0);
                if (freeSlots.contains(letter)) {
                    sender.sendOperation(new PlaceTotemOperation(letter));
                    return;
                }
                System.out.println(RED + BOLD + "err: Tile already occupied or invalid, choose another" + RESET);
            }
        }finally {
            currentPrompt = null;
        }

    }

    @Override
    public void invalidChoice(String message) {
        System.out.println(RED + BOLD + "err: " + message + RESET);
    }

    @Override
    public void showFinalScore(List<String> winners, Map< String , Integer> finalScores) {
        synchronized (OUT_LOCK) {
            System.out.println(YELLOW + BOLD + "\n╔════════════════════════════╗");
            System.out.println("║         GAME OVER!         ║");
            System.out.println("╚════════════════════════════╝" + RESET);

            System.out.println(YELLOW + BOLD + "Winner(s): " + RESET + winners);

            System.out.println(YELLOW + BOLD + "\n--- Final Scores ---" + RESET);
            finalScores.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(e -> System.out.printf("  %-15s %3d PP%n",
                            e.getKey(), e.getValue()));

        }
    }

    @Override
    public void showLeaderboard(List<RankingRow> ranking, Map<String, Integer> playersPosition){
        if (ranking != null && !ranking.isEmpty()) {
            synchronized (OUT_LOCK) {
                System.out.println(BLUE + BOLD + "\n--- Global Leaderboard (" +
                        ranking.getFirst().getNumPlayers() + " players) ---" + RESET);
                System.out.printf("  %-4s %-15s %6s  %s%n", "Rank", "Player", "totalWins", "totalScore");
                System.out.println("  " + "-".repeat(46));
                for (RankingRow row : ranking) {
                    String line = String.format("  %-4s %-15s %4d wins %4d PP %n",
                            "#" + row.getPosition(), row.getNickname(),
                            row.getTotalWin(), row.getScore());
                    if(row.getNickname().equals(nickname)){
                        System.out.println(MAGENTA + BOLD + line + RESET);
                    }else {
                        System.out.println(line);
                    }
                }
            }
        }
    }


    @Override
    public void showPlayerDisconnected(String playerName) {
        System.out.println(RED + BOLD + "\nPlayer disconnected: " + RESET + playerName + RESET);
        reprintPromptIfActive();
    }

    @Override
    public void showServerCrashed() {
        synchronized (OUT_LOCK) {
            System.out.println(RED + BOLD + "\nConnection to server lost. Attempting to reconnect..." + RESET);
        }
    }

    @Override
    public void showPlayerReconnected(String playerName) {
        System.out.println(GREEN + BOLD + "\nPlayer reconnected: " + RESET + playerName + RESET);
        reprintPromptIfActive();
    }

    @Override
    public void showGameSuspended(int timeoutSeconds) {
        //The server will re-send the input request once the game resumes.
        suspendInput();
        synchronized (OUT_LOCK) {
            System.out.println(YELLOW + BOLD + "\nGame suspended! Waiting for players to reconnect... " + RESET);
            System.out.println(YELLOW + "Timeout: " + timeoutSeconds + " seconds. If no one returns, the remaining player wins." + RESET);
        }
    }

    @Override
    public void showGameResumed() {
        resumeInput();
        System.out.println(GREEN + BOLD + "\nGame resumed! Continuing..." + RESET);
        reprintPromptIfActive();
    }

    @Override
    public void showReconnectedTotem(ColorEnum totemColor) {
        System.out.println(YELLOW + "\nYour original totem was " + totemColor + RESET);
    }

    @Override
    public void showWaitingForRecovery(int playersStillNeeded) {
        System.out.println(YELLOW + BOLD + "\nServer recovered! Waiting for "
                + playersStillNeeded + " more player(s) to reconnect..." + RESET);
    }

    // blocks input threads when recovery
    public synchronized void resetInputState() {
        if (inputExecutor != null && !inputExecutor.isShutdown()) {
            inputExecutor.shutdownNow();
        }

        this.inputExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "input-handler");
            t.setDaemon(true);
            return t;
        });
        inputQueue.clear();
        inputSuspended = false;
        currentPrompt = null;

        System.out.println();
    }

}

