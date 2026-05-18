package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.network.clientInterface.ChooseCardOperation;
import it.polimi.ingsw.network.clientInterface.LoginOperation;
import it.polimi.ingsw.network.clientInterface.NumPlayersOperation;
import it.polimi.ingsw.network.ClientSender;
import it.polimi.ingsw.network.clientInterface.PlaceTotemOperation;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;



public class GUIView implements ViewInterface {
    private Stage primaryStage;
    private ClientSender sender;

    private static final double CARD_W = 115;
    private static final double CARD_H = 168;

    private static final Map<CharacterEnum, String> CHAR_ICONS = Map.of(
            CharacterEnum.HUNTER,   "hunter",
            CharacterEnum.GATHERER, "gatherer",
            CharacterEnum.SHAMAN,   "shaman",
            CharacterEnum.ARTIST,   "artist",
            CharacterEnum.BUILDER,  "builder",
            CharacterEnum.INVENTOR, "inventor"
    );

    private final Map<String, Map<CharacterEnum, List<CharacterCard>>> allTribesData = new HashMap<>();


    private HBox upperRowPane;
    private HBox lowerRowPane;
    private HBox buildingUpperPane;
    private HBox buildingLowerPane;
    private HBox offerTrackPane;
    private VBox playersStatus;
    private Label roundLabel;
    private VBox turnOrderPane;
    private HBox actionBar;
    private int pendingUpperCount = 0;
    private int pendingLowerCount = 0;
    private final List<Integer> selectedUpperIndices = new ArrayList<>();
    private final List<Integer> selectedLowerIndices = new ArrayList<>();
    private final List<Integer> selectedBuildingUpperIndices = new ArrayList<>();
    private final List<Integer> selectedBuildingLowerIndices = new ArrayList<>();
    private int totalPlayers = 0;
    private Label loginErrorLabel;
    private String pendingLoginError = null;
    private HBox ownTribePane;
    private Accordion otherTribesAccordion;
    private StackPane sceneRoot;
    private String myName;
    private Map<CharacterEnum, List<CharacterCard>> myTribe = new HashMap<>();
    private int myFood = 0;

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
        primaryStage.setMinWidth(960);
        primaryStage.setMinHeight(600);
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
        background.setFitWidth(1280);
        background.setFitHeight(720);
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(primaryStage.widthProperty());
        background.fitHeightProperty().bind(primaryStage.heightProperty());

        // --- Form ---
        VBox form = new VBox(12);
        form.setAlignment(Pos.CENTER);
        form.setFillWidth(true);
        form.setPadding(new Insets(20));
        form.setMaxWidth(300);
        form.setStyle(
                "-fx-background-color: rgba(0,0,0,0.40);" +
                        "-fx-background-radius: 10;"
        );

        VBox numBox = new VBox(6);
        numBox.setVisible(isFirst);
        numBox.setManaged(isFirst);
        numBox.setAlignment(Pos.CENTER);
        Label numLabel = new Label("Number of players (2–5):");
        numLabel.setStyle("-fx-text-fill: #f5e6c8;");
        TextField numField = new TextField();
        numField.setPromptText("es. 3");
        numField.setMaxWidth(50);
        numBox.getChildren().addAll(numLabel, numField);

        // Name
        Label nameLabel = new Label("Name:");
        nameLabel.setMaxWidth(200);
        nameLabel.setAlignment(Pos.CENTER_LEFT);
        nameLabel.setStyle("-fx-text-fill: #f5e6c8;");
        TextField nameField = new TextField();
        nameField.setPromptText("es. Matteo");
        nameField.setMaxWidth(200);

        // ColorEnum
        Label colorLabel = new Label("Choose your totem color:");
        colorLabel.setStyle("-fx-text-fill: #f5e6c8;");
        ToggleGroup colorGroup = new ToggleGroup();
        VBox colorBox = new VBox(4);
        colorBox.setAlignment(Pos.CENTER_LEFT);
        for (ColorEnum c : ColorEnum.values()) {
            RadioButton rb = new RadioButton(c.name());
            rb.setToggleGroup(colorGroup);
            rb.setUserData(c);
            rb.setStyle("-fx-text-fill: #f5e6c8;");
            colorBox.getChildren().add(rb);
        }
        HBox centeredColorBox = new HBox(colorBox);
        centeredColorBox.setAlignment(Pos.CENTER);

        // error
        loginErrorLabel = new Label();
        loginErrorLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12;");
        loginErrorLabel.setWrapText(true);
        if (pendingLoginError != null) {
            loginErrorLabel.setText("⚠  " + pendingLoginError);
            loginErrorLabel.setVisible(true);
            loginErrorLabel.setManaged(true);
            pendingLoginError = null; // consumato
        } else {
            loginErrorLabel.setVisible(false);
            loginErrorLabel.setManaged(false);
        }

        // login button
        Button confirmBtn = new Button("Login");
        confirmBtn.setStyle(
                "-fx-background-color: #c0392b; -fx-text-fill: white;" +
                        "-fx-font-size: 14; -fx-padding: 8 24; -fx-background-radius: 6;"
        );
        confirmBtn.setOnAction(e -> handleLogin(
                isFirst, numField, nameField, colorGroup, loginErrorLabel
        ));

        form.getChildren().addAll(numBox, nameLabel, nameField, colorLabel,
                centeredColorBox, loginErrorLabel, confirmBtn);

        Region spacer = new Region();
        spacer.setPrefHeight(288);

        VBox verticalLayout = new VBox();
        verticalLayout.setAlignment(Pos.TOP_CENTER);
        verticalLayout.getChildren().addAll(spacer, form);

        StackPane root = new StackPane(background, verticalLayout);

        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setScene(scene);

    }

    private void handleLogin(boolean isFirst, TextField numField,
                             TextField nameField, ToggleGroup colorGroup,
                             Label errorLabel) {
        String name = nameField.getText().trim();
        Toggle selectedColor = colorGroup.getSelectedToggle();

        // Validazione
        if (name.isEmpty()) {
            errorLabel.setText("Insert your name.");
            return;
        }
        if (selectedColor == null) {
            errorLabel.setText("Choose a color.");
            return;
        }

        if (isFirst) {
            String numText = numField.getText().trim();
            int num;
            try {
                num = Integer.parseInt(numText);
                if (num < 2 || num > 5) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                errorLabel.setText("Insert a number between to 2 and 5.");
                return;
            }
            sender.sendOperation(new NumPlayersOperation(num));
        }
        this.myName = name;
        ColorEnum color = (ColorEnum) selectedColor.getUserData();
        sender.sendOperation(new LoginOperation(name, color));
        // Dopo l'invio, il server risponderà con LoggedEvent → showLobby()
    }

    @Override
    public void showLobby(List<String> lobby) {
        Platform.runLater(() -> {
            var bgUrl = getClass().getResource("/images/screen/login_screen.png");
            ImageView background = new ImageView(new Image(bgUrl.toExternalForm()));
            background.setPreserveRatio(false);
            background.fitWidthProperty().bind(primaryStage.widthProperty());
            background.fitHeightProperty().bind(primaryStage.heightProperty());

            Label title = new Label("Waiting for players...");
            title.setStyle(
                    "-fx-text-fill: #f5e6c8; -fx-font-size: 22; -fx-font-weight: bold;"
            );

            VBox playerList = new VBox(10);
            playerList.setAlignment(Pos.CENTER);
            for (String name : lobby) {
                Label playerLabel = new Label("✔  " + name);
                playerLabel.setStyle(
                        "-fx-text-fill: #e8c46a; -fx-font-size: 16;"
                );
                playerList.getChildren().add(playerLabel);
            }

            Label counter = new Label(lobby.size() + " connected");
            counter.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13;");

            VBox content = new VBox(20, title, playerList, counter);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(30));
            content.setMaxWidth(300);
            content.setMaxHeight(250);
            content.setStyle(
                    "-fx-background-color: rgba(0,0,0,0.55);" +
                            "-fx-background-radius: 12;"
            );

            StackPane root = new StackPane(background, content);
            primaryStage.setScene(new Scene(root, primaryStage.getWidth(), primaryStage.getHeight()));
        });
    }

    @Override
    public void showGameStart() {
        Platform.runLater(() -> {

            // ── BACKGROUND ──────────────────────────────────────────────
            var bgUrl = getClass().getResource("/images/screen/background.png");
            ImageView background = new ImageView(new Image(bgUrl.toExternalForm()));
            background.setPreserveRatio(false);
            background.fitWidthProperty().bind(primaryStage.widthProperty());
            background.fitHeightProperty().bind(primaryStage.heightProperty());

            // ── RIGHE CARTE ─────────────────────────────────────────────
            upperRowPane = new HBox(12);
            upperRowPane.setAlignment(Pos.CENTER_LEFT);

            lowerRowPane = new HBox(10);
            lowerRowPane.setAlignment(Pos.CENTER);

            buildingUpperPane = new HBox(10);
            buildingUpperPane.setAlignment(Pos.CENTER_LEFT);

            buildingLowerPane = new HBox(10);
            buildingLowerPane.setAlignment(Pos.CENTER_LEFT);

            // building in colonna a destra della upperRow
            VBox buildingRows = new VBox(10, buildingUpperPane, buildingLowerPane);
            buildingRows.setAlignment(Pos.TOP_LEFT);

            HBox topRow = new HBox(20, upperRowPane, buildingRows);
            topRow.setAlignment(Pos.CENTER);

            // ── OFFER TRACK ─────────────────────────────────────────────
            offerTrackPane = new HBox(14);
            offerTrackPane.setAlignment(Pos.CENTER);
            offerTrackPane.setPadding(new Insets(10));

            // ── TURN ORDER ──────────────────────────────────────────────
            turnOrderPane = new VBox(8);
            turnOrderPane.setAlignment(Pos.CENTER);
            turnOrderPane.setPadding(new Insets(10, 16, 10, 16));
            turnOrderPane.setStyle(
                    "-fx-background-color: rgba(20,10,5,0.60);" +
                            "-fx-background-radius: 8;"
            );

            Label turnOrderTitle = new Label("TURN ORDER");
            turnOrderTitle.setStyle(
                    "-fx-text-fill: #e8c46a; -fx-font-size: 11; -fx-font-weight: bold;"
            );
            VBox turnOrderSection = new VBox(6, turnOrderTitle, turnOrderPane);
            turnOrderSection.setAlignment(Pos.CENTER);

            // ── RIGA CENTRALE: turn order + offer track ──────────────────
            HBox middleRow = new HBox(30, turnOrderSection, offerTrackPane);
            middleRow.setAlignment(Pos.CENTER);
            middleRow.setPadding(new Insets(8, 0, 8, 0));

            // ── SEZIONE PROPRIA TRIBÙ ────────────────────────────────────
            ownTribePane = new HBox(12);
            ownTribePane.setAlignment(Pos.CENTER_LEFT);
            ownTribePane.setPadding(new Insets(6));

            ScrollPane ownTribeScroll = new ScrollPane(ownTribePane);
            ownTribeScroll.setFitToHeight(true);
            ownTribeScroll.setPrefHeight(CARD_H + 40);
            ownTribeScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            ownTribeScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            ownTribeScroll.setStyle(
                    "-fx-background: transparent; -fx-background-color: transparent;"
            );

            Label ownTribeTitle = new Label("YOUR TRIBE");
            ownTribeTitle.setStyle(
                    "-fx-text-fill: #e8c46a; -fx-font-size: 12; -fx-font-weight: bold;"
            );

            VBox ownTribeSection = new VBox(4, ownTribeTitle, ownTribeScroll);
            ownTribeSection.setPadding(new Insets(8, 16, 4, 16));
            ownTribeSection.setStyle(
                    "-fx-background-color: rgba(0,0,0,0.35); -fx-background-radius: 8;"
            );

            // ── ACCORDION TRIBÙ ALTRI GIOCATORI ──────────────────────────
            otherTribesAccordion = new Accordion();
            otherTribesAccordion.setStyle("-fx-background-color: transparent;");

            VBox tribeArea = new VBox(8, ownTribeSection, otherTribesAccordion);
            tribeArea.setPadding(new Insets(8, 0, 0, 0));

            // ── CARD AREA COMPLETA ───────────────────────────────────────
            VBox cardArea = new VBox(16, topRow, middleRow, lowerRowPane, tribeArea);
            cardArea.setAlignment(Pos.CENTER);
            cardArea.setPadding(new Insets(20));

            ScrollPane cardScroll = new ScrollPane(cardArea);
            cardScroll.setFitToWidth(true);
            cardScroll.setFitToHeight(false);
            cardScroll.setStyle(
                    "-fx-background: transparent; -fx-background-color: transparent;"
            );
            HBox.setHgrow(cardScroll, Priority.ALWAYS);

            // ── PANNELLO DESTRO ──────────────────────────────────────────
            roundLabel = new Label("Round 1");
            roundLabel.setStyle(
                    "-fx-text-fill: #f5e6c8; -fx-font-size: 15; -fx-font-weight: bold;"
            );

            Label eraLabel = new Label("Era I");
            eraLabel.setStyle("-fx-text-fill: #e8c46a; -fx-font-size: 13;");

            playersStatus = new VBox(10);

            ScrollPane playersScroll = new ScrollPane(playersStatus);
            playersScroll.setFitToWidth(true);
            playersScroll.setStyle(
                    "-fx-background: transparent; -fx-background-color: transparent;"
            );
            VBox.setVgrow(playersScroll, Priority.ALWAYS);

            Label playersTitle = new Label("PLAYERS:");
            playersTitle.setStyle(
                    "-fx-text-fill: #f5e6c8; -fx-font-weight: bold;"
            );

            VBox rightPanel = new VBox(12,
                    eraLabel,
                    roundLabel,
                    new Separator(),
                    playersTitle,
                    playersScroll
            );
            rightPanel.setPadding(new Insets(12));
            rightPanel.setPrefWidth(180);
            rightPanel.setMinWidth(180);
            rightPanel.setMaxWidth(180);
            rightPanel.setStyle("-fx-background-color: rgba(20,10,5,0.80);");

            // ── ACTION BAR ───────────────────────────────────────────────
            actionBar = new HBox(10);
            actionBar.setPadding(new Insets(8, 16, 8, 16));
            actionBar.setAlignment(Pos.CENTER);
            actionBar.setStyle("-fx-background-color: rgba(180,40,20,0.85);");
            actionBar.setVisible(false);
            actionBar.setManaged(false);

            // ── ASSEMBLY ─────────────────────────────────────────────────
            HBox mainArea = new HBox(cardScroll, rightPanel);
            VBox.setVgrow(mainArea, Priority.ALWAYS);

            VBox rootLayout = new VBox(mainArea, actionBar);
            sceneRoot = new StackPane(background, rootLayout);

            primaryStage.setScene(new Scene(sceneRoot, 1280, 720));
        });
    }

    private void showToast(String message) {
        System.out.println("showToast called, sceneRoot: " + sceneRoot);
        if (sceneRoot == null) return;

        Label toast = new Label("⚠  " + message);
        toast.setStyle(
                "-fx-background-color: rgba(170,20,20,0.93);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12 24;" +
                        "-fx-background-radius: 8;"
        );

        StackPane.setAlignment(toast, Pos.TOP_CENTER);
        StackPane.setMargin(toast, new Insets(20,0,0,0));
        sceneRoot.getChildren().add(toast);
        System.out.println("Toast aggiunto, figli in sceneRoot: " + sceneRoot.getChildren().size());

        new Thread(() -> {
            try { Thread.sleep(3500); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> sceneRoot.getChildren().remove(toast));
        }).start();
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
            if(offerTrackPane == null) return;

            offerTrackPane.getChildren().clear();
            for (OfferTile tile : offerTrack) {
                offerTrackPane.getChildren().add(buildOfferTileView(tile));
            }
        });

    }
    /**
     * Creates the view for a single offer tile (building or character).
     * Positions the player totem precisely inside the white box feature at the bottom of the card.
     */
    private VBox buildOfferTileView(OfferTile tile) {
        String tilePath = "/images/tiles/" + tile.getLetter() + "_front.png";
        var tileUrl = getClass().getResource(tilePath);

        ImageView tileImg;
        if (tileUrl != null) {
            tileImg = new ImageView(new Image(tileUrl.toExternalForm()));
            tileImg.setFitWidth(110);
            tileImg.setFitHeight(150);
            tileImg.setPreserveRatio(false);
        } else {
            System.err.println("Tile not found: " + tilePath);
            tileImg = new ImageView();
        }

        Pane overlay = new Pane();
        overlay.setPrefSize(110, 150);

        if (!tile.getFreeOfferTile() && tile.getOccupant() != null) {
            String totemPath = "/images/icon/" + tile.getOccupant().getTotemColor().name().toLowerCase() + ".png";
            var totemUrl = getClass().getResource(totemPath);
            if (totemUrl != null) {
                ImageView totem = new ImageView(new Image(totemUrl.toExternalForm()));

                double totemSize = 42;
                totem.setFitWidth(totemSize);
                totem.setFitHeight(totemSize);
                totem.setPreserveRatio(true);
                totem.setLayoutX(44);
                totem.setLayoutY(7);
                overlay.getChildren().add(totem);
            }
        }

        StackPane tileStack = new StackPane(tileImg, overlay);
        tileStack.setPrefSize(110, 150);

        Label occupantLabel = new Label(
                tile.getFreeOfferTile() ? "free" : tile.getOccupant().getName()
        );
        occupantLabel.setStyle(
                tile.getFreeOfferTile()
                        ? "-fx-text-fill: #888; -fx-font-size: 10;"
                        : "-fx-text-fill: #e8c46a; -fx-font-size: 10; -fx-font-weight: bold;"
        );
        occupantLabel.setMaxWidth(65);
        occupantLabel.setWrapText(true);
        occupantLabel.setAlignment(Pos.CENTER);

        VBox tileBox = new VBox(4, tileStack, occupantLabel);
        tileBox.setAlignment(Pos.CENTER);
        tileBox.setPadding(new Insets(6));
        tileBox.setStyle(
                tile.getFreeOfferTile()
                        ? "-fx-background-color: rgba(20,10,5,0.30); -fx-background-radius: 8;"
                        : "-fx-background-color: rgba(90,45,12,0.70); -fx-background-radius: 8;"
        );
        tileBox.setUserData(tile);
        return tileBox;
    }

    private ImageView icon(String name) {
        String path = "/images/icon/" + name + ".png";
        var url = getClass().getResource(path);

        if (url == null) {
            System.err.println("Icon not found: " + path);
            return new ImageView(); // Ritorna un'ImageView vuota
        }

        ImageView iv = new ImageView(new Image(url.toExternalForm()));
        iv.setFitWidth(20);
        iv.setFitHeight(20);
        iv.setPreserveRatio(true);
        return iv;
    }

    @Override
    public void updateAllPlayers(List<String> names, List<Integer> foods, List<Integer> pps,
                                 List<Map<CharacterEnum, List<String>>> tribeDesc,
                                 List<List<String>> buildingDesc) {
        Platform.runLater(() -> {
            if(playersStatus == null) return;

            if(totalPlayers == 0) totalPlayers = names.size();
            playersStatus.getChildren().clear();
            for (int i = 0; i < names.size(); i++) {
                if (names.get(i).equals(myName)) {
                    myFood = foods.get(i);
                }
                Label nameLabel = new Label(names.get(i));
                nameLabel.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 13; -fx-font-weight: bold;");

                Label foodCount = new Label(" " + foods.get(i));
                foodCount.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 12;");

                Label ppCount = new Label(" " + pps.get(i));
                ppCount.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 12;");

                HBox stats = new HBox(6, icon("food"), foodCount, icon("PP"), ppCount);
                stats.setAlignment(Pos.CENTER_LEFT);

                VBox playerBox = new VBox(4, nameLabel, stats);
                playerBox.setUserData(names.get(i));
                playerBox.setPadding(new Insets(8));
                playerBox.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-background-radius: 6;");

                playersStatus.getChildren().add(playerBox);
            }
        });
    }

    @Override
    public void updateAllTribes(List<String> names,
                                List<Map<CharacterEnum, List<CharacterCard>>> tribes) {
        Platform.runLater(() -> {
            allTribesData.clear();
            for (int i = 0; i < names.size(); i++) {
                allTribesData.put(names.get(i), tribes.get(i));
            }
            refreshPlayerButtons(names);
        });
    }

    private void refreshPlayerButtons(List<String> names) {
        if (playersStatus == null) return;
        for (var node : playersStatus.getChildren()) {
            if (node instanceof VBox playerBox && playerBox.getUserData() instanceof String name) {
                if (playerBox.getChildren().size() > 2) continue;
                if (!name.equals(myName) && allTribesData.containsKey(name)) {
                    Button viewBtn = new Button("👁 View tribe");
                    viewBtn.setStyle(
                            "-fx-background-color: rgba(200,150,0,0.5); -fx-text-fill: #f5e6c8;" +
                                    "-fx-font-size: 10; -fx-padding: 3 8; -fx-background-radius: 4; -fx-cursor: hand;"
                    );
                    String captureName = name;
                    viewBtn.setOnAction(e -> showTribePopup(captureName, allTribesData.get(captureName)));
                    playerBox.getChildren().add(viewBtn);
                }
            }
        }
    }

    private void showTribePopup(String playerName, Map<CharacterEnum, List<CharacterCard>> tribe) {
        if (sceneRoot == null) return;
        Region dim = new Region();
        dim.setStyle("-fx-background-color: rgba(0,0,0,0.75);");

        Label title = new Label(playerName + "'s Tribe");
        title.setStyle(
                "-fx-text-fill: #e8c46a; -fx-font-size: 18; -fx-font-weight: bold;"
        );

        HBox tribeContent = new HBox(20);
        tribeContent.setAlignment(Pos.TOP_LEFT);
        tribeContent.setPadding(new Insets(12));

        for (CharacterEnum type : CharacterEnum.values()) {
            List<CharacterCard> cards = tribe.get(type);
            if (cards == null || cards.isEmpty()) continue;

            ImageView typeIcon = icon(CHAR_ICONS.getOrDefault(type, type.name().toLowerCase()));
            typeIcon.setFitWidth(32);
            typeIcon.setFitHeight(32);

            Label countLabel = new Label("×" + cards.size());
            countLabel.setStyle("-fx-text-fill: #e8c46a; -fx-font-size: 12;");

            VBox col = new VBox(6, typeIcon, countLabel);
            col.setAlignment(Pos.TOP_CENTER);

            for (CharacterCard card : cards) {
                ImageView img = cardImage(card.getImage());
                img.setFitWidth(90);
                img.setFitHeight(130);
                col.getChildren().add(img);
            }
            tribeContent.getChildren().add(col);
        }

        ScrollPane scroll = new ScrollPane(tribeContent);
        scroll.setFitToHeight(true);
        scroll.setPrefHeight(420);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Button closeBtn = new Button("✕  Close");
        closeBtn.setStyle(
                "-fx-background-color: #c0392b; -fx-text-fill: white;" +
                        "-fx-font-size: 13; -fx-padding: 6 20;" +
                        "-fx-background-radius: 6; -fx-cursor: hand;"
        );

        VBox popup = new VBox(16, title, scroll, closeBtn);
        popup.setAlignment(Pos.TOP_CENTER);
        popup.setPadding(new Insets(24));
        popup.setMaxWidth(860);
        popup.setStyle(
                "-fx-background-color: rgba(20,10,5,0.97);" +
                        "-fx-background-radius: 12;"
        );

        StackPane overlay = new StackPane(dim, popup);
        overlay.setAlignment(Pos.CENTER);

        closeBtn.setOnAction(e -> sceneRoot.getChildren().remove(overlay));
        dim.setOnMouseClicked(e -> sceneRoot.getChildren().remove(overlay));

        sceneRoot.getChildren().add(overlay);
    }


    @Override
    public void updateRound(int currentRound) {
        Platform.runLater(() -> {
            if(roundLabel == null) return;

            roundLabel.setText("Round " + currentRound);
        });

    }

    @Override
    public void updateTurnOrder(TurnOrderTile turnOrder) {
        Platform.runLater(() -> {
            if (turnOrderPane == null) return;
            turnOrderPane.getChildren().clear();

            List<Player> slots = turnOrder.getSlots();
            int numSlots = slots.size();

            if (totalPlayers == 0 && numSlots == 0) {
                return;

            }
            int tileGraphicSize = (totalPlayers > 0) ? totalPlayers : numSlots;
            double tileW = 100;
            double tileH = 180;

            String tilePath = "/images/tiles/turnOrderTile_" + tileGraphicSize + ".png";
            var tileUrl = getClass().getResource(tilePath);
            if (tileUrl == null) {
                System.err.println("TurnOrder tile non trovata: " + tilePath);
                return;
            }
            ImageView tileImg = new ImageView(new Image(tileUrl.toExternalForm()));

            tileImg.setFitWidth(tileW);
            tileImg.setFitHeight(tileH);
            tileImg.setPreserveRatio(false);

            double[] yCoordinates = switch (tileGraphicSize) {
                case 2 -> new double[]{50, 85};
                case 3 -> new double[]{43, 78, 111};
                case 4 -> new double[]{36, 70, 103, 136};
                case 5 -> new double[]{26, 57, 90, 122, 154};
                default -> new double[tileGraphicSize];
            };

            Pane overlay = new Pane();
            Pane namesPane = new Pane();
            overlay.setPrefSize(tileW, tileH);
            namesPane.setPrefWidth(80);
            namesPane.setPrefHeight(tileH);

            for (int slot = 0; slot < tileGraphicSize; slot++) {

                if (slot >= yCoordinates.length) break;
                double slotY = yCoordinates[slot];
                Player p = (slot < numSlots) ? slots.get(slot) : null;

                if (p != null) {
                    String totemPath = "/images/icon/" + p.getTotemColor().name().toLowerCase() + ".png";
                    var totemUrl = getClass().getResource(totemPath);
                    if (totemUrl != null) {
                        ImageView totem = new ImageView(new Image(totemUrl.toExternalForm()));
                        double totemSize = 30;
                        totem.setFitWidth(totemSize);
                        totem.setFitHeight(totemSize);
                        totem.setPreserveRatio(true);
                        totem.setLayoutX(10);
                        totem.setLayoutY(slotY - totemSize / 2);
                        overlay.getChildren().add(totem);
                    }
                    Label nameLabel = new Label((slot + 1) + ". " + p.getName());
                    nameLabel.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 12;");
                    nameLabel.setLayoutY(slotY - 8);
                    namesPane.getChildren().add(nameLabel);
                } else {
                    Label empty = new Label((slot + 1) + ". —");
                    empty.setStyle("-fx-text-fill: #555; -fx-font-size: 12;");
                    empty.setLayoutY(slotY - 8);
                    namesPane.getChildren().add(empty);
                }
            }

            StackPane tileWithTotems = new StackPane(tileImg, overlay);
            tileWithTotems.setPrefSize(tileW, tileH);

            HBox fullTurnOrder = new HBox(10, namesPane, tileWithTotems);
            fullTurnOrder.setAlignment(Pos.CENTER_LEFT);
            turnOrderPane.getChildren().add(fullTurnOrder);
        });
    }
        @Override
        public void selectCard(int upperCount, int lowerCount, int cardsUpper, int cardsLower,
                           List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow) {
        Platform.runLater(() -> {
            if (upperRowPane == null) return;

            pendingUpperCount = upperCount;
            pendingLowerCount = lowerCount;
            selectedUpperIndices.clear();
            selectedLowerIndices.clear();
            selectedBuildingUpperIndices.clear();
            selectedBuildingLowerIndices.clear();
            renderTribeRowSelectable(upperRowPane, upperRow, selectedUpperIndices, upperCount);
            renderTribeRowSelectable(lowerRowPane, lowerRow, selectedLowerIndices, lowerCount);
            renderBuildingRowSelectable(buildingUpperPane, buildingUpperRow, selectedBuildingUpperIndices);
            renderBuildingRowSelectable(buildingLowerPane, buildingLowerRow, selectedBuildingLowerIndices);
            actionBar.getChildren().clear();
            Label msg = new Label(
                    "Select " + upperCount + " from upper row  |  " + lowerCount + " from lower row"
            );
            msg.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 14;");
            actionBar.getChildren().add(msg);
            actionBar.setVisible(true);
            actionBar.setManaged(true);
        });
    }

    private void renderTribeRowSelectable(HBox pane, List<TribeCard> cards,
                                          List<Integer> selectedIndices, int maxSelectable) {
        pane.getChildren().clear();
        for (int i = 0; i < cards.size(); i++) {
            final int index = i;
            ImageView iv = cardImage(cards.get(i).getImage());

            if (maxSelectable > 0) {
                iv.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, gold, 6, 0.3, 0, 0);");
                iv.setOnMouseEntered(e -> { if (!selectedIndices.contains(index)) iv.setOpacity(0.75); });
                iv.setOnMouseExited(e  -> { if (!selectedIndices.contains(index)) iv.setOpacity(1.0); });
                iv.setOnMouseClicked(e -> {
                    handleCardSelection(iv, index, selectedIndices, maxSelectable);
                    tryConfirmSelection();
                });
            }
            pane.getChildren().add(iv);
        }
    }

    private void renderBuildingRowSelectable(HBox pane, List<BuildingCard> cards,
                                             List<Integer> selectedIndices) {
        pane.getChildren().clear();
        for (int i = 0; i < cards.size(); i++) {
            final int index = i;
            ImageView iv = cardImage(cards.get(i).getImage());

            iv.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, gold, 6, 0.3, 0, 0);");
            iv.setOnMouseEntered(e -> { if (!selectedIndices.contains(index)) iv.setOpacity(0.75); });
            iv.setOnMouseExited(e  -> { if (!selectedIndices.contains(index)) iv.setOpacity(1.0); });
            iv.setOnMouseClicked(e -> {
                handleCardSelection(iv, index, selectedIndices, Integer.MAX_VALUE);
                tryConfirmSelection();
            });
            pane.getChildren().add(iv);
        }
    }

    private void handleCardSelection(ImageView iv, int index,
                                     List<Integer> selectedIndices, int maxSelectable) {
        if (selectedIndices.contains(index)) {
            selectedIndices.remove((Integer) index);
            iv.setOpacity(1.0);
            iv.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, gold, 6, 0.3, 0, 0);");
        } else if (selectedIndices.size() < maxSelectable) {
            selectedIndices.add(index);
            iv.setOpacity(0.5);
            iv.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, #00ff88, 10, 0.6, 0, 0);");
        }
    }

    private void tryConfirmSelection() {
        if (selectedUpperIndices.size() == pendingUpperCount
                && selectedLowerIndices.size() == pendingLowerCount) {
            sender.sendOperation(new ChooseCardOperation(
                    new ArrayList<>(selectedUpperIndices),
                    new ArrayList<>(selectedLowerIndices),
                    new ArrayList<>(selectedBuildingUpperIndices),
                    new ArrayList<>(selectedBuildingLowerIndices)
            ));
            actionBar.setVisible(false);
            // rimuovi i click handler dalle righe
            clearRowHandlers(upperRowPane);
            clearRowHandlers(lowerRowPane);
            clearRowHandlers(buildingUpperPane);
            clearRowHandlers(buildingLowerPane);
        }
    }

    private void clearRowHandlers(HBox pane) {
        for (var node : pane.getChildren()) {
            node.setOnMouseClicked(null);
            node.setOnMouseEntered(null);
            node.setOnMouseExited(null);
            node.setOpacity(1.0);
            node.setStyle("");
        }
    }


    @Override
    public void placeTotem(List<Character> freeSlots) {
        Platform.runLater(() -> {
            if (offerTrackPane == null) return;

            actionBar.getChildren().clear();
            Label msg = new Label("Choose an offer tile to place your totem ");
            msg.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 14;");
            actionBar.getChildren().add(msg);
            actionBar.setVisible(true);
            actionBar.setManaged(true);

            for (var node : offerTrackPane.getChildren()) {
                if (node instanceof VBox tileBox) {
                    OfferTile tile = (OfferTile) tileBox.getUserData();
                    char letter = tile.getLetter();
                    if (freeSlots.contains(letter)) {
                        tileBox.setStyle(
                                "-fx-background-color: rgba(200,150,0,0.75);" +
                                        "-fx-background-radius: 8; -fx-cursor: hand;"
                        );
                        tileBox.setOnMouseEntered(e -> tileBox.setOpacity(0.75));
                        tileBox.setOnMouseExited(e  -> tileBox.setOpacity(1.0));
                        tileBox.setOnMouseClicked(e -> {
                            sender.sendOperation(new PlaceTotemOperation(letter));
                            actionBar.setVisible(false);
                            clearOfferTrackHandlers();
                        });
                    }
                }
            }
        });
    }

    private void clearOfferTrackHandlers() {
        for (var node : offerTrackPane.getChildren()) {
            if (node instanceof VBox tileBox) {
                tileBox.setOnMouseClicked(null);
                tileBox.setOnMouseEntered(null);
                tileBox.setOnMouseExited(null);
                tileBox.setOpacity(1.0);

                if (tileBox.getUserData() instanceof OfferTile tile) {
                    tileBox.setStyle(
                            tile.getFreeOfferTile()
                                    ? "-fx-background-color: rgba(20,10,5,0.30); -fx-background-radius: 8;"
                                    : "-fx-background-color: rgba(90,45,12,0.70); -fx-background-radius: 8;"
                    );
                }
            }
        }
    }

    @Override
    public void invalidChoice(String message) {
        Platform.runLater(() -> {
            if (actionBar == null) {
                pendingLoginError = message;
                if (loginErrorLabel != null) {
                    loginErrorLabel.setText("⚠  " + message);
                    loginErrorLabel.setVisible(true);
                    loginErrorLabel.setManaged(true);
                }
                return;
            }
            showToast(message);
        });
    }

    @Override
    public void showFinalScore(List<String> winners, Map< String , Integer> finalScores) {

    }


    @Override
    public void showValidCards(Map<CharacterEnum, List<CharacterCard>> tribe) {
        this.myTribe = tribe;
        Platform.runLater(() -> {
            if (ownTribePane == null) return;
            ownTribePane.getChildren().clear();

            for (CharacterEnum type : CharacterEnum.values()) {
                List<CharacterCard> cards = tribe.get(type);
                if (cards == null || cards.isEmpty()) continue;

                ImageView typeIcon = icon(CHAR_ICONS.getOrDefault(type, type.name().toLowerCase()));
                Label countLabel = new Label("×" + cards.size());
                countLabel.setStyle("-fx-text-fill: #e8c46a; -fx-font-size: 11;");
                VBox header = new VBox(2, typeIcon, countLabel);
                header.setAlignment(Pos.CENTER);

                VBox cardColumn = new VBox(4);
                cardColumn.setAlignment(Pos.TOP_CENTER);
                for (CharacterCard card : cards) {
                    cardColumn.getChildren().add(cardImage(card.getImage()));
                }

                VBox typeGroup = new VBox(4, header, cardColumn);
                typeGroup.setAlignment(Pos.TOP_CENTER);
                typeGroup.setPadding(new Insets(4, 6, 4, 6));
                typeGroup.setStyle(
                        "-fx-background-color: rgba(255,255,255,0.06);" +
                                "-fx-background-radius: 6;"
                );
                ownTribePane.getChildren().add(typeGroup);
            }
        });
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
            System.err.println("Image not found: " + path);
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

    @Override
    public void showPlayerDisconnected(String playerName) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Player " + playerName + " has disconnected.");
            alert.setTitle("Connection Lost");
            alert.setHeaderText(null);
            alert.show();
        });
    }

    @Override
    public void showPlayerReconnected(String playerName) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Player " + playerName + " has reconnected.");
            alert.setTitle("Player Returned");
            alert.setHeaderText(null);
            alert.show();
        });
    }

    @Override
    public void showGameSuspended(int timeoutSeconds) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING,
                    "Game suspended. Waiting for other players to reconnect. Timeout: " + timeoutSeconds + "s.");
            alert.setTitle("Game Suspended");
            alert.setHeaderText(null);
            alert.show();
        });
    }

    @Override
    public void showGameResumed() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "All players are back. Game is resuming!");
            alert.setTitle("Game Resumed");
            alert.setHeaderText(null);
            alert.show();
        });
    }
    @Override
    public void showReconnectedTotem(ColorEnum totemColor) {
       //Messaggio che dice al player che il suo totem originale era di quel colore;
    }
}
