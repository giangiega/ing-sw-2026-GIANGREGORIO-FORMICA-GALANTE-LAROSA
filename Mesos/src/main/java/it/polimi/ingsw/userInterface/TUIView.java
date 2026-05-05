package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.network.socket.ClientViewSocket;
import it.polimi.ingsw.network.clientInterface.*;

import java.util.*;

public class TUIView implements ViewInterface {
    private final Scanner scanner = new Scanner(System.in);
    private ClientViewSocket sender;
    private final Map<String, int[]> playersStatus = new LinkedHashMap<>();

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
        askLogin();
    }

    @Override
    public void askLogin() {
        System.out.print("Enter your name: ");
        String name = scanner.next();

        System.out.println("Available colors: " +
                Arrays.toString(it.polimi.ingsw.enums.ColorEnum.values()));
        System.out.print("Choose color: ");
        String color = scanner.next().toUpperCase();

        try {
            sender.sendOperation(new LoginOperation(name, ColorEnum.valueOf(color)));
        } catch (IllegalArgumentException e) {
            System.out.println("[!] Invalid color, try again.");
            askLogin();
        }
    }

    @Override
    public void showGameStart() {
        System.out.println("\n╔═════════════════════════════╗");
        System.out.println("║        GAME STARTED!        ║");
        System.out.println("╚═════════════════════════════╝");
    }

    @Override
    public void updateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow) {
        System.out.println("\n--- Upper Row ---");
        for (int i = 0; i < upperRow.size(); i++)
            System.out.println("  [" + i + "] " + upperRow.get(i));

        if (!buildingUpperRow.isEmpty()) {
            System.out.println("  \nBuildings:");
            for (int i = 0; i < buildingUpperRow.size(); i++)
                System.out.println("  [" + (i + upperRow.size()) + "] " + buildingUpperRow.get(i));
        }

        System.out.println("\n--- Lower Row ---");
        for (int i = 0; i < lowerRow.size(); i++)
            System.out.println("  [" + i + "] " + lowerRow.get(i));

        if (!buildingLowerRow.isEmpty()) {
            System.out.println("  \nBuildings:");
            for (int i = 0; i < buildingLowerRow.size(); i++)
                System.out.println("  [" + (i + lowerRow.size()) + "] " + buildingLowerRow.get(i));
        }
    }

    @Override
    public void updateOfferTrack(List<OfferTile> offerTrack) {
        System.out.println("\n--- Offer Track ---");
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
        sb.append("--- Turn Order ---\n");
        for (int i = 0; i < turnOrder.getSlots().size(); i++) {
            Player p = turnOrder.getSlots().get(i);
            if (p != null)
                sb.append(String.format("  %d. [%s] %s%n",
                        i + 1,
                        p.getTotemColor().name(),
                        p.getName()));
            else
                sb.append(String.format("  %d. [ empty ]%n", i + 1));
        }
        System.out.println(sb.toString());

    }

    @Override
    public void updatePlayer(String name, int food, int prestigePoints, Map<CharacterEnum,List<CharacterCard>> tribe) {
        playersStatus.put(name, new int[]{food, prestigePoints});
        System.out.println("\n============= PLAYERS STATUS =============");
        for (Map.Entry<String, int[]> entry : playersStatus.entrySet()) {
            System.out.printf("  %-15s  food: %2d   PP: %3d%n",
                    entry.getKey(),
                    entry.getValue()[0],
                    entry.getValue()[1]);
        }
        System.out.println("==========================================");
    }

    @Override
    public void selectCard(int upperCount, int lowerCount, List<TribeCard> upperRow,
                           List<TribeCard> lowerRow, List<BuildingCard> upperBuildings,
                           List<BuildingCard> lowerBuildings) {
        List<Integer> upperCards = new ArrayList<>();
        List<Integer> lowerCards = new ArrayList<>();
        List<Integer> upperBuildings_ = new ArrayList<>();
        List<Integer> lowerBuildings_ = new ArrayList<>();
        int input = 0;
        int count = 0;

        if (upperCount > 0) {
            /*if (!upperBuildings.isEmpty()) {
                System.out.println("Upper buildings available:");
                for (int i = 0; i < upperBuildings.size(); i++)
                    System.out.printf("  [%d] cost: %d food   PP: %d%n",
                            (i + upperRow.size()),
                            upperBuildings.get(i).getBaseFC(),
                            upperBuildings.get(i).getBasePP());
            }*/

            System.out.println("\nChoose " + upperCount + " card(s) from upper row or building upper row.");
            System.out.print("Enter " + upperCount + " index/indices: ");

            while (count < upperCount) {
                try {
                    input = scanner.nextInt();
                    if (input < upperRow.size())
                        upperCards.add(input);
                    else if (input < (upperRow.size() + upperBuildings.size()))
                        upperBuildings_.add(input - (upperRow.size() + 1));
                    count++;
                }catch (InputMismatchException e){
                    System.out.println("Player hasn't food to take this building");
                }
            }
        }
        count = 0;
        if (lowerCount > 0) {
            /*if (!lowerBuildings.isEmpty()) {
                System.out.println("Lower buildings available:");
                for (int i = 0; i < lowerBuildings.size(); i++)
                    System.out.printf("  [%d] cost: %d food / PP: %d%n",
                            (i + lowerRow.size()),
                            lowerBuildings.get(i).getBaseFC(),
                            lowerBuildings.get(i).getBasePP());
            }*/

            System.out.println("\nChoose " + lowerCount + " card(s) from lower row or building lower row.");
            System.out.print("Enter " + lowerCount + " index/indices: ");

            while (count < lowerCount) {
                try {
                    input = scanner.nextInt();
                    if (input < lowerRow.size())
                        lowerCards.add(input);
                    else if (input < (lowerRow.size() + lowerBuildings.size()))
                        lowerBuildings_.add(input - (lowerRow.size() + 1));
                    count++;
                } catch (InputMismatchException e){
                    System.out.println("Player hasn't food to take this building");
                }
            }
        }

        sender.sendOperation(new ChooseCardOperation(
                upperCards, lowerCards, upperBuildings_, lowerBuildings_));
    }

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
    public void showValidCards(Map<CharacterEnum, List<CharacterCard>> tribe) {
        System.out.println("\n--- Your updated tribe ---");
        for (Map.Entry<CharacterEnum, List<CharacterCard>> entry : tribe.entrySet()) {
            if (!entry.getValue().isEmpty())
                System.out.println("  " + entry.getKey() + ": " + entry.getValue().size());
        }
    }

    @Override
    public void showFinalScore(List<String> winners, Map< String , Integer> finalScores) {
        System.out.println("\n╔══════════════════════════╗");
        System.out.println("║       GAME OVER!         ║");
        System.out.println("╚══════════════════════════╝");

        System.out.println("Winner(s): " + winners);

        System.out.println("\n--- Final Scores ---");
        finalScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> System.out.printf("  %-15s %3d PP%n",
                        e.getKey(), e.getValue()));

    }
}

/* valutare menù a tendina per info altri giocatori e per funzionamento carte */