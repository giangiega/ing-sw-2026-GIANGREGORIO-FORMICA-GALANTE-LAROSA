package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.network.clientInterface.LoginOperation;
import it.polimi.ingsw.network.clientInterface.NumPlayersOperation;
import it.polimi.ingsw.network.ClientSender;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class GUIView implements ViewInterface {
    private Stage primaryStage;
    private ClientSender sender;

    private static final double CARD_W = 75;
    private static final double CARD_H = 110;

    private HBox upperRowPane;
    private HBox lowerRowPane;
    private HBox buildingUpperPane;
    private HBox buildingLowerPane;
    private HBox offerTrackPane;
    private VBox playersStatus;
    private Label roundLabel;
    private VBox turnOrderPane;
    private HBox actionBar;

    /**
     * CountDownLatch saves GUI from race condition between main Thread and JavaFX Thread
     * latch.countDown() moves 1-->0
     * latch.await() makes the main Thread wait until count down is 0
     */
    public GUIView() {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.startup(() -> {
            buildStage();
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


    }

    private void buildStage() {
        primaryStage = new Stage();
        primaryStage.setTitle("Mesos");
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public Consumer<Runnable> getUIDispatcher() {
        return Platform::runLater;
    }

    @Override
    public void init(ClientSender sender) {
        this.sender = sender;
    }

    @Override
    public void askNumPlayers() {
        Platform.runLater(() -> showLoginScene(true));
    }

    @Override
    public void askLogin() {
        Platform.runLater(() -> showLoginScene(false));
    }

    private void showLoginScene(boolean isFirst) {
        var bgUrl = getClass().getResource("/images/screen/login_screen.png");
        ImageView background = new ImageView(new Image(bgUrl.toExternalForm()));
        background.setFitWidth(1260);
        background.setFitHeight(720);
        background.setPreserveRatio(false);

        // --- Form ---
        VBox form = new VBox(12);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setMaxWidth(300);
        form.setStyle(
                "-fx-background-color: rgba(0,0,0,0.40);" +
                        "-fx-background-radius: 10;"
        );

        VBox numBox = new VBox(6);
        numBox.setVisible(isFirst);
        numBox.setManaged(isFirst); // se non visibile, non occupa spazio
        Label numLabel = new Label("Numero di giocatori (2–5):");
        numLabel.setStyle("-fx-text-fill: #f5e6c8;");
        TextField numField = new TextField();
        numField.setPromptText("es. 3");
        numField.setMaxWidth(200);
        numBox.getChildren().addAll(numLabel, numField);

        // --- Nome ---
        Label nameLabel = new Label("Il tuo nome:");
        nameLabel.setStyle("-fx-text-fill: #f5e6c8;");
        TextField nameField = new TextField();
        nameField.setPromptText("es. Marco");
        nameField.setMaxWidth(200);

        // --- Colore: uno RadioButton per ogni ColorEnum ---
        Label colorLabel = new Label("Scegli il colore del tuo totem:");
        colorLabel.setStyle("-fx-text-fill: #f5e6c8;");
        ToggleGroup colorGroup = new ToggleGroup();
        VBox colorBox = new VBox(4);
        for (ColorEnum c : ColorEnum.values()) {
            RadioButton rb = new RadioButton(c.name());
            rb.setToggleGroup(colorGroup);
            rb.setUserData(c);      // salviamo l'enum sul bottone
            rb.setStyle("-fx-text-fill: #f5e6c8;");
            colorBox.getChildren().add(rb);
        }

        // --- Messaggio di errore ---
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12;");
        errorLabel.setWrapText(true);

        // --- Bottone Accedi ---
        Button confirmBtn = new Button("Accedi");
        confirmBtn.setStyle(
                "-fx-background-color: #c0392b; -fx-text-fill: white;" +
                        "-fx-font-size: 14; -fx-padding: 8 24; -fx-background-radius: 6;"
        );
        confirmBtn.setOnAction(e -> handleLogin(
                isFirst, numField, nameField, colorGroup, errorLabel
        ));

        form.getChildren().addAll(numBox,
                nameLabel, nameField,
                colorLabel, colorBox,
                errorLabel, confirmBtn
        );

        Region spacer = new Region();
        spacer.setPrefHeight(243); // 45% di 540

// --- VBox verticale: spacer sopra, form sotto ---
        VBox verticalLayout = new VBox();
        verticalLayout.setAlignment(Pos.TOP_CENTER);
        verticalLayout.getChildren().addAll(spacer, form);

// --- StackPane: sfondo + layout sovrapposti ---
        StackPane root = new StackPane(background, verticalLayout);

        Scene scene = new Scene(root, 960, 540);
        primaryStage.setScene(scene);

    }

    private void handleLogin(boolean isFirst, TextField numField,
                             TextField nameField, ToggleGroup colorGroup,
                             Label errorLabel) {
        String name = nameField.getText().trim();
        Toggle selectedColor = colorGroup.getSelectedToggle();

        // Validazione
        if (name.isEmpty()) {
            errorLabel.setText("Inserisci il tuo nome.");
            return;
        }
        if (selectedColor == null) {
            errorLabel.setText("Seleziona un colore.");
            return;
        }

        if (isFirst) {
            String numText = numField.getText().trim();
            int num;
            try {
                num = Integer.parseInt(numText);
                if (num < 2 || num > 5) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                errorLabel.setText("Inserisci un numero tra 2 e 5.");
                return;
            }
            sender.sendOperation(new NumPlayersOperation(num));
        }

        ColorEnum color = (ColorEnum) selectedColor.getUserData();
        sender.sendOperation(new LoginOperation(name, color));
        // Dopo l'invio, il server risponderà con LoggedEvent → showLobby()
    }

    @Override
    public void showLobby(List<String> lobby) {
        Platform.runLater(() -> {
            // Per ora: mostra un testo di attesa
            Label waiting = new Label("In attesa degli altri giocatori...\nConnessi: " + lobby);
            waiting.setStyle("-fx-font-size: 18; -fx-text-fill: white;");
            StackPane root = new StackPane(waiting);
            root.setStyle("-fx-background-color: #2c1810;");
            primaryStage.setScene(new Scene(root, 960, 540));
        });
    }

   @Override
   public void showGameStart() {
       Platform.runLater(() -> {
           // ── BACKGROUND ──────────────────────────────────────────
           var bgUrl = getClass().getResource("/images/screen/background.png");
           ImageView background = new ImageView(new Image(bgUrl.toExternalForm()));
           background.setFitWidth(1280);
           background.setFitHeight(720);
           background.setPreserveRatio(false);

           // ── PANNELLO DESTRO (fisso) ──────────────────────────────
           roundLabel = new Label("Round 1");
           roundLabel.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 15; -fx-font-weight: bold;");

           Label eraLabel = new Label("Era I");
           eraLabel.setStyle("-fx-text-fill: #e8c46a; -fx-font-size: 13;");
           // 1. CAMBIAMO I TIPI DI PANE
           // Turn Order: ora VBox per essere verticale
           turnOrderPane = new VBox(8);
           turnOrderPane.setPadding(new Insets(10));
           turnOrderPane.setStyle("-fx-background-color: rgba(20,10,5,0.40); -fx-background-radius: 8;");

           // Offer Track: ora HBox per essere orizzontale
           offerTrackPane = new HBox(10);
           offerTrackPane.setPadding(new Insets(10));
           offerTrackPane.setStyle("-fx-background-color: rgba(20,10,5,0.60); -fx-background-radius: 8;");

           // Righe carte (già dichiarate nel tuo codice)
           upperRowPane = new HBox(8);
           lowerRowPane = new HBox(8);
           buildingUpperPane = new HBox(8);
           buildingLowerPane = new HBox(8);

           // 2. ASSEMBLLAGGIO RIGHE
           // Riga Superiore: Tribe Upper + Building Upper
           HBox topRow = new HBox(15, upperRowPane, buildingUpperPane);
           topRow.setAlignment(Pos.CENTER_LEFT);

           // Riga Centrale: Turn Order (Sinistra) + Offer Track (Centro)
           // Creiamo una piccola VBox per il Turn Order con la sua etichetta
           VBox turnOrderSection = new VBox(5,
                   new Label("ORDINE:") {{ setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 10;"); }},
                   turnOrderPane
           );
           HBox middleRow = new HBox(15, turnOrderSection, offerTrackPane);
           middleRow.setAlignment(Pos.CENTER_LEFT);

           // Riga Inferiore: Tribe Lower + Building Lower
           HBox bottomRow = new HBox(15, lowerRowPane, buildingLowerPane);
           bottomRow.setAlignment(Pos.CENTER_LEFT);

           // 3. AREA CARTE TOTALE
           VBox cardArea = new VBox(20, topRow, middleRow, bottomRow);
           cardArea.setPadding(new Insets(20));

           ScrollPane cardScroll = new ScrollPane(cardArea);
           cardScroll.setFitToWidth(true);
           cardScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
           HBox.setHgrow(cardScroll, Priority.ALWAYS);

           // 4. PANNELLO DESTRO (Senza TurnOrder, solo Status)
           playersStatus = new VBox(10);
           ScrollPane playersScroll = new ScrollPane(playersStatus);
           playersScroll.setFitToWidth(true);
           playersScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

           VBox rightPanel = new VBox(14,
                   eraLabel, roundLabel,
                   new Separator(),
                   new Label("GIOCATORI:") {{ setStyle("-fx-text-fill: #f5e6c8; -fx-font-weight: bold;"); }},
                   playersScroll
           );
           rightPanel.setPadding(new Insets(16));
           rightPanel.setPrefWidth(280);
           rightPanel.setStyle("-fx-background-color: rgba(20,10,5,0.75);");

           actionBar = new HBox(10);
           actionBar.setPadding(new Insets(8, 16, 8, 16));
           actionBar.setStyle("-fx-background-color: rgba(180,40,20,0.85);");
           actionBar.setVisible(false);

           HBox mainArea = new HBox(cardScroll, rightPanel);
           VBox rootLayout = new VBox(mainArea, actionBar);
           VBox.setVgrow(mainArea, Priority.ALWAYS);
           StackPane sceneRoot = new StackPane(background, rootLayout);
           primaryStage.setScene(new Scene(sceneRoot, 1280, 720));
       });
   }

    /**
     * @author Daniele
     * @param upperRow
     * @param lowerRow
     * @param buildingUpperRow
     * @param buildingLowerRow
     * this method will update the cardrows every time the board change
     */
    @Override
    public void updateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow) {
        Platform.runLater(() -> {
            if(upperRowPane == null) return; // quando la board non è pronta
            renderTribeRow(upperRowPane, upperRow, false);
            renderTribeRow(lowerRowPane, lowerRow, false);
            renderBuildingRow(buildingUpperPane, buildingUpperRow, false);
            renderBuildingRow(buildingLowerPane, buildingLowerRow, false);

        });

    }

    @Override
    public void updateOfferTrack(List<OfferTile> offerTrack) {
        Platform.runLater(() -> {
            if(offerTrackPane == null) return; // quando la board non è pronta

            offerTrackPane.getChildren().clear();
            for (OfferTile tile : offerTrack) {
                offerTrackPane.getChildren().add(buildOfferTileView(tile));
            }
        });

    }
    private VBox buildOfferTileView(OfferTile tile) {
        String arrows = "↑" + tile.getCountUpperArrow() + "  ↓" + tile.getCountLowerArrow();
        Label infoLabel = new Label("[" + tile.getLetter() + "]  " + arrows);
        infoLabel.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 12; -fx-font-weight: bold;");

        String occupantText = tile.getFreeOfferTile()
                ? "— libera —"
                : tile.getOccupant().getName();
        Label occupantLabel = new Label(occupantText);
        occupantLabel.setStyle(
                tile.getFreeOfferTile()
                        ? "-fx-text-fill: #888; -fx-font-size: 11;"
                        : "-fx-text-fill: #e8c46a; -fx-font-size: 11; -fx-font-weight: bold;"
        );

        VBox tileBox = new VBox(2, infoLabel, occupantLabel);
        tileBox.setPadding(new Insets(6, 10, 6, 10));
        tileBox.setStyle(
                tile.getFreeOfferTile()
                        ? "-fx-background-color: #3d2010; -fx-background-radius: 6;"
                        : "-fx-background-color: #5a2d0c; -fx-background-radius: 6;"
        );
        return tileBox;
    }

    private ImageView icon(String name) {
        String path = "/images/icon/" + name + ".png";
        var url = getClass().getResource(path);

        if (url == null) {
            System.err.println("Icona non trovata: " + path);
            return new ImageView(); // Ritorna un'ImageView vuota
        }

        ImageView iv = new ImageView(new Image(url.toExternalForm()));
        iv.setFitWidth(20);
        iv.setFitHeight(20);
        iv.setPreserveRatio(true);
        return iv;
    }

    @Override
    public void updateAllPlayers(List<String> names, List<Integer> foods, List<Integer> pps) {
        Platform.runLater(() -> {
            if(playersStatus == null) return; // quando la board non è pronta

            playersStatus.getChildren().clear();
            for (int i = 0; i < names.size(); i++) {
                Label nameLabel = new Label(names.get(i));
                nameLabel.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 13; -fx-font-weight: bold;");

                Label foodCount = new Label(" " + foods.get(i));
                foodCount.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 12;");

                Label ppCount = new Label(" " + pps.get(i));
                ppCount.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 12;");

                HBox stats = new HBox(6, icon("food"), foodCount, icon("PP"), ppCount);
                stats.setAlignment(Pos.CENTER_LEFT);

                VBox playerBox = new VBox(4, nameLabel, stats);
                playerBox.setPadding(new Insets(8));
                playerBox.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 6;");

                playersStatus.getChildren().add(playerBox);
            }
        });
    }

    @Override
    public void updateRound(int currentRound) {
        Platform.runLater(() -> {
            if(roundLabel == null) return; // quando la board non è pronta

            roundLabel.setText("Round " + currentRound);
        });

    }

    @Override
    public void updateTurnOrder(TurnOrderTile turnOrder) {
        Platform.runLater(() -> {
            if(turnOrderPane == null) return; // quando la board non è pronta

            turnOrderPane.getChildren().clear();

            List<Player> order = turnOrder.getOrder();
            for (int i = 0; i < order.size(); i++) {
                Player p = order.get(i);
                Label slot = new Label((i + 1) + ". " + p.getName());
                slot.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 11;");
                turnOrderPane.getChildren().add(slot);
            }
        });

    }

    @Override
    public void selectCard(int upperCount, int lowerCount, int cardsUpper, int cardsLower,
                           List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow) {

    }

    @Override
    public void placeTotem(List<Character> freeSlots) {

    }

    @Override
    public void invalidChoice(String message) {

    }

    @Override
    public void showFinalScore(List<String> winners, Map< String , Integer> finalScores) {

    }

    @Override
    public void showValidCards(Map<CharacterEnum, List<CharacterCard>> tribe) {

    }

    /**
     * @author daniele
     * The following 3 methods are used to render the card face , with the addition of a hand that follows the mouse movement
     * @param image
     * @return
     */
    private ImageView cardImage(String image){
        String path = "/images/cards/"+image+"_front.png";
        var url = getClass().getResource(path);
        if (url == null) {
            System.err.println("Immagine non trovata: " + path);
            return new ImageView();
        }
        ImageView iv = new ImageView(new Image(url.toExternalForm()));
        iv.setFitWidth(CARD_W);
        iv.setFitHeight(CARD_H);
        iv.setPreserveRatio(true);
        return iv;
    }

    private void renderTribeRow(HBox pane, List<TribeCard> cards, boolean clickable) {
        pane.getChildren().clear();
        for (TribeCard card : cards) {
            ImageView iv = cardImage(card.getImage());
            if (clickable) {
                iv.setStyle("-fx-cursor: hand;");
                iv.setOnMouseEntered(e -> iv.setOpacity(0.7));
                iv.setOnMouseExited(e  -> iv.setOpacity(1.0));
            }
            pane.getChildren().add(iv);
        }
    }
    private void renderBuildingRow(HBox pane, List<BuildingCard> cards, boolean clickable) {
        pane.getChildren().clear();
        for (BuildingCard card : cards) {
            ImageView iv = cardImage(card.getImage());
            if (clickable) {
                iv.setStyle("-fx-cursor: hand;");
                iv.setOnMouseEntered(e -> iv.setOpacity(0.7));
                iv.setOnMouseExited(e -> iv.setOpacity(1.0));
            }
            pane.getChildren().add(iv);
        }
    }
}
