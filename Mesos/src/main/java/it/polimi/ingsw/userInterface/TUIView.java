package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.enums.CharacterEnum;
import it.polimi.ingsw.enums.ColorEnum;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.network.ClientViewSocket;
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
            System.out.println("  Buildings:");
            for (int i = 0; i < buildingUpperRow.size(); i++)
                System.out.println("  [B" + i + "] " + buildingUpperRow.get(i));
        }

        System.out.println("\n--- Lower Row ---");
        for (int i = 0; i < lowerRow.size(); i++)
            System.out.println("  [" + i + "] " + lowerRow.get(i));

        if (!buildingLowerRow.isEmpty()) {
            System.out.println("  Buildings:");
            for (int i = 0; i < buildingLowerRow.size(); i++)
                System.out.println("  [B" + i + "] " + buildingLowerRow.get(i));
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

   /* @Override
    public void selectCard(int upperCount, int lowerCount, List<BuildingCard> upperBuildings, List<BuildingCard> lowerBuildings) {
        List<Integer> upperCards = new ArrayList<>();
        List<Integer> lowerCards = new ArrayList<>();
        List<Integer> upperBuildings_ = new ArrayList<>();
        List<Integer> lowerBuildings_ = new ArrayList<>();

        if (upperCount > 0) {
            System.out.println("Choose " + upperCount + " card(s) from upper row.");
            System.out.print("Enter " + upperCount + " index/indices: ");
            for (int i = 0; i < upperCount; i++)
                upperCards.add(scanner.nextInt());

            if (!upperBuildings.isEmpty()) {
                System.out.println("Upper buildings available:");
                for (int i = 0; i < upperBuildings.size(); i++)
                    System.out.printf("  [%d] cost: %d food   PP: %d%n",
                            i,
                            upperBuildings.get(i).getBaseFC(),
                            upperBuildings.get(i).getBasePP());
                System.out.println("  [-1] Skip");
                System.out.print("Choose building index: ");
                int idx = scanner.nextInt();
                if (idx >= 0 && idx < upperBuildings.size())
                    upperBuildings_.add(idx);
            }
        }

        if (lowerCount > 0) {
            System.out.println("Choose " + lowerCount + " card(s) from lower row.");
            System.out.print("Enter " + lowerCount + " index/indices: ");
            for (int i = 0; i < lowerCount; i++)
                lowerCards.add(scanner.nextInt());

            if (!lowerBuildings.isEmpty()) {
                System.out.println("Lower buildings available:");
                for (int i = 0; i < lowerBuildings.size(); i++)
                    System.out.printf("  [%d] cost: %d food   PP: %d%n",
                            i,
                            lowerBuildings.get(i).getBaseFC(),
                            lowerBuildings.get(i).getBasePP());
                System.out.println("  [-1] Skip");
                System.out.print("Choose building index: ");
                int idx = scanner.nextInt();
                if (idx >= 0 && idx < lowerBuildings.size())
                    lowerBuildings_.add(idx);
            }
        }

        sender.sendOperation(new ChooseCardOperation(
                upperCards, lowerCards, upperBuildings_, lowerBuildings_));
    }*/

    @Override
    public void selectCard(int upperCount, int lowerCount,
                           List<BuildingCard> upperBuildings, List<BuildingCard> lowerBuildings) {
        List<Integer> upperCards = new ArrayList<>();
        List<Integer> lowerCards = new ArrayList<>();
        List<Integer> upperBldgs = new ArrayList<>();
        List<Integer> lowerBldgs = new ArrayList<>();

        System.out.println("\n╔═══════════════════════════════╗");
        System.out.println("║         CHOOSE CARDS          ║");
        System.out.println("╚═══════════════════════════════╝");

        // === UPPER ROW ===
        int upperBuildOffset = upperCount;
        int totalUpper = upperCount + upperBuildings.size();

        System.out.println("UPPER ROW (indices 0 to " + (totalUpper - 1) + "):");
        for (int i = 0; i < upperCount; i++)
            System.out.printf("  [%d] Tribe Card%n", i);
        for (int i = 0; i < upperBuildings.size(); i++) {
            BuildingCard b = upperBuildings.get(i);
            System.out.printf("  [%d] Building: %s (Cost: %d)%n", upperBuildOffset + i, b, b.getBaseFC());
        }

        System.out.print("Enter UPPER indices (space-separated, -1 to skip): ");
        parseRowInput(scanner, upperCount, upperBuildOffset, upperBuildings.size(), upperCards, upperBldgs);

        // === LOWER ROW ===
        int lowerBuildOffset = lowerCount;
        int totalLower = lowerCount + lowerBuildings.size();

        System.out.println("\nLOWER ROW (indices 0 to " + (totalLower - 1) + "):");
        for (int i = 0; i < lowerCount; i++)
            System.out.printf("  [%d] Tribe Card%n", i);
        for (int i = 0; i < lowerBuildings.size(); i++) {
            BuildingCard b = lowerBuildings.get(i);
            System.out.printf("  [%d] Building: %s (Cost: %d)%n", lowerBuildOffset + i, b, b.getBaseFC());
        }

        System.out.print("Enter LOWER indices (space-separated, -1 to skip): ");
        parseRowInput(scanner, lowerCount, lowerBuildOffset, lowerBuildings.size(), lowerCards, lowerBldgs);

        // Invia al server
        sender.sendOperation(new ChooseCardOperation(upperCards, lowerCards, upperBldgs, lowerBldgs));
    }

    /**
     * Helper per parsare gli input e applicare la sottrazione dell'offset come richiesto.
     * Tribe cards: 0 .. n-1
     * Buildings:   n .. n+m-1 → sottraendo n ottieni l'indice corretto per la lista buildings.
     */
    private void parseRowInput(Scanner sc, int tribeCount, int buildOffset, int buildSize,
                               List<Integer> tribeList, List<Integer> buildList) {
        if (sc.hasNextLine()) sc.nextLine(); // pulisce buffer residuo
        String line = sc.nextLine().trim();

        if (line.equals("-1")) return;
        String[] tokens = line.split("\\s+");

        for (String token : tokens) {
            try {
                int idx = Integer.parseInt(token);

                if (idx >= 0 && idx < tribeCount) {
                    tribeList.add(idx);
                } else if (idx >= buildOffset && idx < buildOffset + buildSize) {
                    buildList.add(idx - buildOffset); // Applica la tua formula: x - (n)
                } else {
                    System.out.println("[!] Ignored invalid index: " + idx);
                }
            } catch (NumberFormatException e) {
                // ignora token non numerici
            }
        }
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