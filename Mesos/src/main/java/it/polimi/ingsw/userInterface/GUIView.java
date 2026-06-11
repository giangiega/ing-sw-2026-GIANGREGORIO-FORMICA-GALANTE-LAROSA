package it.polimi.ingsw.userInterface;

import it.polimi.ingsw.database.RankingRow;
import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.boardAndTiles.OfferTile;
import it.polimi.ingsw.model.boardAndTiles.TurnOrderTile;
import it.polimi.ingsw.model.cards.buildings.BuildingCard;
import it.polimi.ingsw.model.cards.tribe.TribeCard;
import it.polimi.ingsw.model.cards.tribe.characters.Builder;
import it.polimi.ingsw.model.cards.tribe.characters.CharacterCard;
import it.polimi.ingsw.network.clientInterface.ChooseCardOperation;
import it.polimi.ingsw.network.clientInterface.LoginOperation;
import it.polimi.ingsw.network.clientInterface.NumPlayersOperation;
import it.polimi.ingsw.network.ClientSender;
import it.polimi.ingsw.network.clientInterface.PlaceTotemOperation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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


    private final Map<String, Map<CharacterEnum, List<CharacterCard>>> allTribesData = new HashMap<>();
    private final Map<String, List<BuildingCard>> allBuildingsData = new HashMap<>();


    private HBox upperRowPane;
    private HBox lowerRowPane;
    private HBox buildingUpperPane;
    private HBox buildingLowerPane;
    private HBox offerTrackPane;
    private VBox playersStatus;
    private Label roundLabel;
    private Label eraLabel;
    private EraEnum currentMaxEra = EraEnum.I;
    private VBox turnOrderPane;
    private HBox actionBar;
    private int pendingUpperCount = 0;
    private int pendingLowerCount = 0;
    private final List<Integer> selectedUpperIndices = new ArrayList<>();
    private final List<Integer> selectedLowerIndices = new ArrayList<>();
    private final List<Integer> selectedBuildingUpperIndices = new ArrayList<>();
    private final List<Integer> selectedBuildingLowerIndices = new ArrayList<>();
    private List<BuildingCard> renderedBuildingUpper = new ArrayList<>();
    private List<BuildingCard> renderedBuildingLower = new ArrayList<>();
    private int totalPlayers = 0;

    private HBox ownTribePane;
    private Accordion otherTribesAccordion;
    private StackPane sceneRoot;
    private String myName;
    private Map<CharacterEnum, List<CharacterCard>> myTribe = new HashMap<>();
    private int myFood = 0;
    private int errorCountE = 0;
    private int errorCountB = 0;
    private List<RankingRow> globalRanking;
    private Map<String, Integer> globalPlayersPosition;


    private Dialog<Void> suspendedDialog;
    private Timeline countdownTimeline;
    private StackPane reconnectingOverlay;
    private VBox suspendedOverlay;

    private VBox loginNumBox;
    private TextField loginNumField;
    private boolean isFirstPlayer = false;


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
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
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
        Platform.runLater(() -> {
            hideReconnectingOverlay();
            closeSuspendedDialogIfOpen();
            this.isFirstPlayer = true;
            if (loginNumBox != null && sceneRoot != null) {
                loginNumBox.setVisible(true);
                loginNumBox.setManaged(true);
            } else {
                showLoginScene(true);
            }
        });
    }

    @Override
    public void askLogin() {
        Platform.runLater(() ->{
            hideReconnectingOverlay();
            closeSuspendedDialogIfOpen();
            this.isFirstPlayer = false;
            showLoginScene(false);
        });
    }

    private void showLoginScene(boolean isFirst) {
        this.upperRowPane = null;
        this.isFirstPlayer = isFirst;
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
        this.loginNumBox = new VBox(6);
        loginNumBox.setVisible(isFirstPlayer);
        loginNumBox.setManaged(isFirstPlayer);
        loginNumBox.setAlignment(Pos.CENTER);

        Label numLabel = new Label("Number of players (2–5):");
        numLabel.setStyle("-fx-text-fill: #f5e6c8;");
        this.loginNumField = new TextField();
        loginNumField.setPromptText("es. 3");
        loginNumField.setMaxWidth(50);
        loginNumBox.getChildren().addAll(numLabel, loginNumField);

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

        // login button
        Button confirmBtn = new Button("Login");
        confirmBtn.setStyle(
                "-fx-background-color: #c0392b; -fx-text-fill: white;" +
                        "-fx-font-size: 14; -fx-padding: 8 24; -fx-background-radius: 6;"
        );
        confirmBtn.setOnAction(e -> handleLogin(
                this.isFirstPlayer, loginNumField, nameField, colorGroup
        ));

        form.getChildren().addAll(loginNumBox, nameLabel, nameField, colorLabel,
                centeredColorBox, confirmBtn);

        Region spacer = new Region();
        spacer.setPrefHeight(288);

        VBox verticalLayout = new VBox();
        verticalLayout.setAlignment(Pos.TOP_CENTER);
        verticalLayout.getChildren().addAll(spacer, form);

        sceneRoot = new StackPane(background, verticalLayout);

        Scene scene = new Scene(sceneRoot, 1280, 720);
        primaryStage.setScene(scene);

    }

    private void handleLogin(boolean isFirst, TextField numField,
                             TextField nameField, ToggleGroup colorGroup) {
        String name = nameField.getText().trim();
        Toggle selectedColor = colorGroup.getSelectedToggle();

        // Validation
        if (name.isEmpty()) {
            showToast("Please insert your name");
            return;
        }
        if (selectedColor == null) {
            showToast("Please select a color");
            return;
        }

        if (isFirst) {
            String numText = numField.getText().trim();
            int num;
            try{
                num = Integer.parseInt(numText);
                if (num < 2 || num > 5) throw new NumberFormatException();

            } catch (NumberFormatException e) {
                showToast("Invalid number.");
                return;
            }
            sender.sendOperation(new NumPlayersOperation(num));
        }
        this.myName = name;
        ColorEnum color = (ColorEnum) selectedColor.getUserData();
        sender.sendOperation(new LoginOperation(name, color));
        //After sendOperation, server will answer with a LoggedEvent → showLobby()
    }

    @Override
    public void showLobby(List<String> lobby) {
        Platform.runLater(() -> {
            this.upperRowPane = null;
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
        Platform.runLater(this::buildGameScene);
    }

    private void buildGameScene(){
        //**********BACKGROUND**********//
        var bgUrl = getClass().getResource("/images/screen/background.png");
        ImageView background = new ImageView(new Image(bgUrl.toExternalForm()));
        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(primaryStage.widthProperty());
        background.fitHeightProperty().bind(primaryStage.heightProperty());

        //**********CARD ROWS**********//
        upperRowPane = new HBox(12);
        upperRowPane.setAlignment(Pos.CENTER_LEFT);

        lowerRowPane = new HBox(12);
        lowerRowPane.setAlignment(Pos.CENTER);

        buildingUpperPane = new HBox(12);
        buildingUpperPane.setAlignment(Pos.BOTTOM_LEFT);

        buildingLowerPane = new HBox(12);
        buildingLowerPane.setAlignment(Pos.BOTTOM_LEFT);



        HBox topRow = new HBox(20, upperRowPane, buildingUpperPane);
        topRow.setAlignment(Pos.BOTTOM_CENTER);
        HBox lowerRow = new HBox(20,lowerRowPane, buildingLowerPane);
        lowerRow.setAlignment(Pos.BOTTOM_CENTER);

        //**********OFFER TRACK**********//
        offerTrackPane = new HBox(14);
        offerTrackPane.setAlignment(Pos.CENTER);
        offerTrackPane.setPadding(new Insets(10));

        //**********TURN ORDER**********//
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

        //**********MIDDLE ROW: turn order + offer track**********//
        HBox middleRow = new HBox(30, turnOrderSection, offerTrackPane);
        middleRow.setAlignment(Pos.CENTER);
        middleRow.setPadding(new Insets(8, 0, 8, 0));

        //**********PLAYER'S TRIBE SECTION**********//
        ownTribePane = new HBox(16);
        ownTribePane.setAlignment(Pos.CENTER_LEFT);
        ownTribePane.setPadding(new Insets(8));

        ScrollPane ownTribeScroll = new ScrollPane(ownTribePane);
        ownTribeScroll.setFitToHeight(false);
        ownTribeScroll.setPrefHeight(CARD_H + 70);
        ownTribeScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        ownTribeScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        ownTribeScroll.setStyle(
                "-fx-background: transparent; -fx-background-color: transparent;"
        );

        Label ownTribeTitle = new Label("YOUR TRIBE");
        ownTribeTitle.setStyle(
                "-fx-text-fill: #e8c46a; -fx-font-size: 12; -fx-font-weight: bold;"
        );

        VBox ownTribeSection = new VBox(18, ownTribeTitle, ownTribeScroll);
        ownTribeSection.setPadding(new Insets(8, 16, 4, 16));
        ownTribeSection.setStyle(
                "-fx-background-color: rgba(0,0,0,0.35); -fx-background-radius: 8;"
        );

        //**********ACCORDION OTHER'S PLAYER TRIBE**********//
        otherTribesAccordion = new Accordion();
        otherTribesAccordion.setStyle("-fx-background-color: transparent;");

        VBox tribeArea = new VBox(8, ownTribeSection, otherTribesAccordion);
        tribeArea.setPadding(new Insets(8, 0, 0, 0));

        //**********COMPLETED CARD AREA**********//
        VBox cardArea = new VBox(16, topRow, middleRow, lowerRow, tribeArea);
        cardArea.setAlignment(Pos.CENTER);
        cardArea.setPadding(new Insets(20));

        ScrollPane cardScroll = new ScrollPane(cardArea);
        cardScroll.setFitToWidth(true);
        cardScroll.setFitToHeight(false);
        cardScroll.setStyle(
                "-fx-background: transparent; -fx-background-color: transparent;"
        );
        HBox.setHgrow(cardScroll, Priority.ALWAYS);

        //**********RIGHT PANEL**********//
        roundLabel = new Label("Round 1");
        roundLabel.setStyle(
                "-fx-text-fill: #f5e6c8; -fx-font-size: 15; -fx-font-weight: bold;"
        );

        eraLabel = new Label("Era I");
        eraLabel.setStyle("-fx-text-fill: #e8c46a; -fx-font-size: 13;");
        HBox eraRoundBox = new HBox(10, eraLabel, roundLabel);
        eraRoundBox.setAlignment(Pos.CENTER_LEFT);

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
                eraRoundBox,
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

        //**********ACTION BAR**********//
        actionBar = new HBox(10);
        actionBar.setPadding(new Insets(8, 16, 8, 16));
        actionBar.setAlignment(Pos.CENTER);
        actionBar.setStyle("-fx-background-color: rgba(180,40,20,0.85);");
        actionBar.setVisible(false);
        actionBar.setManaged(false);

        HBox mainArea = new HBox(cardScroll, rightPanel);
        VBox.setVgrow(mainArea, Priority.ALWAYS);

        VBox rootLayout = new VBox(mainArea, actionBar);
        sceneRoot = new StackPane(background, rootLayout);

        primaryStage.setScene(new Scene(sceneRoot, 1280, 720));
    }

    private void showToast(String message) {
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

        new Thread(() -> {
            try { Thread.sleep(3500); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> sceneRoot.getChildren().remove(toast));
        }).start();
    }


    /**
     * @author Daniele
     * @param upperRow upperRow (tribe)
     * @param lowerRow lowerRow
     * @param buildingUpperRow buildingUpperRow
     * @param buildingLowerRow buildingLowerRow
     * This method will update the cardRows every time the board change
     */
    @Override
    public void updateRows(List<TribeCard> upperRow, List<TribeCard> lowerRow,
                           List<BuildingCard> buildingUpperRow, List<BuildingCard> buildingLowerRow) {
        Platform.runLater(() -> {
            if(upperRowPane == null) return;

            EraEnum highestVisibleEra = EraEnum.I;
            if (upperRow != null && !upperRow.isEmpty()) {
                highestVisibleEra = upperRow.get(0).getEra();
            }
            if (buildingUpperRow != null && !buildingUpperRow.isEmpty()) {
                EraEnum buildingEra = buildingUpperRow.get(0).getEra();

                if (buildingEra.compareTo(highestVisibleEra) > 0) {
                    highestVisibleEra = buildingEra;
                }
            }
            if (highestVisibleEra.compareTo(currentMaxEra) > 0) {
                currentMaxEra = highestVisibleEra;
                updateEra(currentMaxEra);

            }

            renderTribeRow(upperRowPane, upperRow, false);
            renderTribeRow(lowerRowPane, lowerRow, false);
            redistributeBuildings(buildingUpperRow, buildingLowerRow);

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
                                List<Map<CharacterEnum, List<CharacterCard>>> tribes, List<List<BuildingCard>> buildings) {
        Platform.runLater(() -> {
            allTribesData.clear();
            allBuildingsData.clear();
            for (int i = 0; i < names.size(); i++) {
                allTribesData.put(names.get(i), tribes.get(i));
                allBuildingsData.put(names.get(i), buildings.get(i));
            }
            refreshPlayerButtons(names);
        });
    }

    private void refreshPlayerButtons(List<String> names) {
        if (playersStatus == null) return;

        for (var node : playersStatus.getChildren()) {
            // Checking node's type using getClass()
            if (node != null && node.getClass().equals(VBox.class)) {
                VBox playerBox = (VBox) node;
                Object userData = playerBox.getUserData();

                // Checking UserData's type using getClass()
                if (userData != null && userData.getClass().equals(String.class)) {
                    String name = (String) userData;

                    if (playerBox.getChildren().size() > 2) continue;

                    if (!name.equals(myName) && allTribesData.containsKey(name)) {
                        Button viewBtn = new Button("👁 View tribe");
                        viewBtn.setStyle(
                                "-fx-background-color: rgba(200,150,0,0.5); -fx-text-fill: #f5e6c8;" +
                                        "-fx-font-size: 10; -fx-padding: 3 8; -fx-background-radius: 4; -fx-cursor: hand;"
                        );

                        String captureName = name;
                        viewBtn.setOnAction(e -> showTribePopup(
                                captureName,
                                allTribesData.get(captureName),
                                allBuildingsData.get(captureName)
                        ));

                        playerBox.getChildren().add(viewBtn);
                    }
                }
            }
        }
    }

    private void showTribePopup(String playerName, Map<CharacterEnum, List<CharacterCard>> tribe, List<BuildingCard> buildings) {
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

            ImageView typeIcon = icon(type.name().toLowerCase());
            typeIcon.setFitWidth(48);
            typeIcon.setFitHeight(48);

            Label countLabel = new Label("×" + cards.size());
            countLabel.setStyle("-fx-text-fill: #e8c46a; -fx-font-size: 12;");

            VBox col = new VBox(8, typeIcon, countLabel);
            col.setAlignment(Pos.TOP_CENTER);

            for (CharacterCard card : cards) {
                ImageView img = cardImage(card.getImage());
                img.setFitWidth(100);
                img.setFitHeight(145);
                col.getChildren().add(img);
            }
            tribeContent.getChildren().add(col);
        }
        if (buildings != null && !buildings.isEmpty()) {
            Region spacer = new Region();
            spacer.setPrefWidth(20);
            tribeContent.getChildren().add(spacer);

            Label bTitle = new Label("BUILDINGS");
            bTitle.setStyle("-fx-text-fill: #e8c46a; -fx-font-size: 16; -fx-font-weight: bold;");

            VBox bCol = new VBox(32, bTitle);
            bCol.setAlignment(Pos.TOP_CENTER);
            bCol.setPadding(new Insets(14, 0, 0, 0));

            for (BuildingCard card : buildings) {
                ImageView img = cardImage(card.getImage());
                img.setFitWidth(100);
                img.setFitHeight(145);
                bCol.getChildren().add(img);
            }
            tribeContent.getChildren().add(bCol);
        }

        ScrollPane scroll = new ScrollPane(tribeContent);
        scroll.setFitToHeight(true);
        scroll.setPrefHeight(630);
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
        popup.setMaxWidth(1000);
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

    public void updateEra(EraEnum era) {
        Platform.runLater(() -> {
            if (eraLabel == null) return;
            eraLabel.setText("Era " + era.name());
        });
    }


    @Override
    public void updateTurnOrder(TurnOrderTile turnOrder) {
        Platform.runLater(() -> {
            if (turnOrderPane == null) return;
            errorCountE = 0;
            errorCountB = 0;
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

            int pickableUpper = countPickableTribeCards(upperRow);
            int pickableLower = countPickableTribeCards(lowerRow);
            int actualUpper = Math.min(upperCount, pickableUpper);
            int actualLower = Math.min(lowerCount, pickableLower);

            if(actualUpper == 0 && actualLower == 0) {
                showToast(" No cards available. Skipping turn.");
                sender.sendOperation(new ChooseCardOperation( // sending operation with null lists--> skip turn
                        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
                return;
            }
            pendingUpperCount = actualUpper;
            pendingLowerCount = actualLower;
            selectedUpperIndices.clear();
            selectedLowerIndices.clear();
            selectedBuildingUpperIndices.clear();
            selectedBuildingLowerIndices.clear();
            renderTribeRowSelectable(upperRowPane, upperRow, selectedUpperIndices, actualUpper);
            renderTribeRowSelectable(lowerRowPane, lowerRow, selectedLowerIndices, actualLower);

            renderBuildingRowSelectable(buildingUpperPane, renderedBuildingUpper, selectedBuildingUpperIndices);
            renderBuildingRowSelectable(buildingLowerPane, renderedBuildingLower, selectedBuildingLowerIndices);
            actionBar.getChildren().clear();
            Label msg = new Label(
                    "Select " + actualUpper + " from upper row  |  " + actualLower + " from lower row"
            );
            msg.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 14;");
            actionBar.getChildren().add(msg);
            actionBar.setVisible(true);
            actionBar.setManaged(true);

            tryConfirmSelection();
        });

    }

    private int countPickableTribeCards(List<TribeCard> cards) {
        if (cards == null) return 0;
        int count = 0;
        for (TribeCard card : cards) {
            if (card.isPickable()) {
                count++;
            }
        }
        return count;
    }
    private void renderTribeRowSelectable(HBox pane, List<TribeCard> cards,
                                          List<Integer> selectedIndices, int maxSelectable) {
        pane.getChildren().clear();
        for (int i = 0; i < cards.size(); i++) {
            final int index = i;
            TribeCard card = cards.get(i);
            ImageView iv = cardImage(cards.get(i).getImage());
            iv.setStyle("-fx-cursor: hand;");
            iv.setOpacity(1);
            iv.setOnMouseEntered(e -> {
                if (!selectedIndices.contains(index)) iv.setOpacity(0.85);
            });
            iv.setOnMouseExited(e -> {
                if (!selectedIndices.contains(index)) iv.setOpacity(1.0);
            });
            iv.setOnMouseClicked(e -> {
                // 1 Events: clickable but locked with toast
                if (!card.isPickable()) {
                    if(errorCountE<5){
                        showToast(" Events cannot be selected.");
                    }else{
                        showToast(" Are you dumb ??? You cannot pick an event card!!!");
                    }
                    errorCountE ++;
                    return;
                }

                // 2 Rows with no card: just visible
                if (maxSelectable == 0) {
                    showToast(" No cards required from this row.");
                    return;
                }

                //Requirement for this row is already met
                if (selectedIndices.contains(index) || selectedIndices.size()< maxSelectable) {
                    handleCardSelection(iv, index, selectedIndices, maxSelectable);
                    tryConfirmSelection();
                }else {
                    showToast(" Requirement for this row is already met.");
                }

            });
            pane.getChildren().add(iv);
        }
    }


    private void renderBuildingRowSelectable(HBox pane, List<BuildingCard> cards,
                                             List<Integer> selectedIndices) {
        pane.getChildren().clear();
        int builderDiscount = calculateBuilderDiscount();

        for (int i = 0; i < cards.size(); i++) {
            final int index = i;
            BuildingCard card = cards.get(i);
            ImageView iv = cardImage(card.getImage());

            int effectiveCost = Math.max(0, card.getBaseFC() - builderDiscount);
            boolean canAfford = myFood >= effectiveCost;


            iv.setStyle("-fx-cursor: hand;");
            iv.setOnMouseEntered(e -> {
                if (!selectedIndices.contains(index)) iv.setOpacity(0.85);
            });
            iv.setOnMouseExited(e -> {
                if (!selectedIndices.contains(index)) iv.setOpacity(1.0);
            });
            iv.setOnMouseClicked(e -> {
                errorCountB++;
                if(errorCountB >= 5){
                    showToast("Are you dumb ??? You are poor !!!");
                    return;
                }
                if (!canAfford) {
                    String msg = builderDiscount > 0
                            ? "Need " + effectiveCost + " food (base " + card.getBaseFC()
                            + " - builder discount " + builderDiscount + ") — you have " + myFood
                            : "Need " + effectiveCost + " food — you have " + myFood;

                    showToast(msg);
                    return;
                }
                handleCardSelection(iv, index, selectedIndices, Integer.MAX_VALUE);
                tryConfirmSelection();
            });

            pane.getChildren().add(iv);
        }
    }

    private void redistributeBuildings(List<BuildingCard> upper, List<BuildingCard> lower) {
        List<BuildingCard> all = new ArrayList<>();
        if (upper != null) all.addAll(upper);
        if (lower != null) all.addAll(lower);

        renderedBuildingUpper.clear();
        renderedBuildingLower.clear();
        int idx = currentMaxEra.ordinal();

        for (BuildingCard b : all) {
            int bIdx = b.getEra().ordinal();
            if (bIdx == idx)       renderedBuildingUpper.add(b);
            else if (bIdx == idx - 1) renderedBuildingLower.add(b);
        }

        renderBuildingRow(buildingUpperPane, renderedBuildingUpper, false);
        renderBuildingRow(buildingLowerPane, renderedBuildingLower, false);
    }

    private void handleCardSelection(ImageView iv, int index,
                                     List<Integer> selectedIndices, int maxSelectable) {
        if (selectedIndices.contains(index)) {
            selectedIndices.remove((Integer) index);
            iv.setOpacity(1.0);
            iv.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, gold, 6, 0.3, 0, 0);");
        } else{
            selectedIndices.add(index);
            iv.setOpacity(0.6);
            iv.setStyle("-fx-cursor: hand; -fx-effect: dropshadow(gaussian, #00ff88, 12, 0.7, 0, 0);");
        }
    }

    private void tryConfirmSelection() {
        int totalUpper = selectedUpperIndices.size() + selectedBuildingUpperIndices.size();
        int totalLower = selectedLowerIndices.size() + selectedBuildingLowerIndices.size();

        if (totalUpper == pendingUpperCount && totalLower == pendingLowerCount) {
            sender.sendOperation(new ChooseCardOperation(
                    new ArrayList<>(selectedUpperIndices),
                    new ArrayList<>(selectedLowerIndices),
                    new ArrayList<>(selectedBuildingUpperIndices),
                    new ArrayList<>(selectedBuildingLowerIndices)
            ));
            actionBar.setVisible(false);
            actionBar.setManaged(false);
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
                if (node != null && node.getClass().equals(VBox.class)) {
                    VBox tileBox = (VBox) node;

                    Object userData = tileBox.getUserData();

                    if (userData != null && userData.getClass().equals(OfferTile.class)) {
                        OfferTile tile = (OfferTile) userData;
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
            showToast(message);
        });
    }

    @Override
    public void showFinalScore(List<String> winners, Map< String , Integer> finalScores) {
        Platform.runLater(() -> {
            var bgUrl = getClass().getResource("/images/screen/background.png");
            ImageView background = new ImageView(new Image(bgUrl.toExternalForm()));
            background.setPreserveRatio(false);
            background.fitWidthProperty().bind(primaryStage.widthProperty());
            background.fitHeightProperty().bind(primaryStage.heightProperty());

            String winnerText = winners.size() == 1 ? "🏆  " + winners.get(0) + " wins!"
                    : "🏆  Draw: " + String.join(", ", winners);
            Label winnerLabel = new Label(winnerText);
            winnerLabel.setStyle("-fx-text-fill: #e8c46a; -fx-font-size: 28; -fx-font-weight: bold;"
            );

            List<Map.Entry<String, Integer>> sorted = new ArrayList<>(finalScores.entrySet());
            sorted.sort((a, b) -> b.getValue() - a.getValue());

            VBox scoreboard = new VBox(8);
            scoreboard.setAlignment(Pos.CENTER);

            int rank = 1;
            for (Map.Entry<String, Integer> entry : sorted) {
                String name  = entry.getKey();
                int    score = entry.getValue();
                boolean isWinner = winners.contains(name);

                Label rankLabel = new Label(rank + ".");
                rankLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 14;");
                rankLabel.setPrefWidth(30);

                Label nameLabel = new Label(name);
                nameLabel.setPrefWidth(200);
                nameLabel.setStyle(isWinner
                        ? "-fx-text-fill: #e8c46a; -fx-font-size: 16; -fx-font-weight: bold;"
                        : "-fx-text-fill: #f5e6c8; -fx-font-size: 16;"
                );

                Label scoreLabel = new Label(score + " PP");
                scoreLabel.setStyle(isWinner
                        ? "-fx-text-fill: #e8c46a; -fx-font-size: 16; -fx-font-weight: bold;"
                        : "-fx-text-fill: #f5e6c8; -fx-font-size: 16;"
                );

                HBox row = new HBox(16, rankLabel, nameLabel, scoreLabel);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8, 16, 8, 16));
                row.setStyle(isWinner
                        ? "-fx-background-color: rgba(200,150,0,0.25); -fx-background-radius: 8;"
                        : "-fx-background-color: rgba(255,255,255,0.06);  -fx-background-radius: 8;"
                );

                scoreboard.getChildren().add(row);
                rank++;
            }

            Label subtitle = new Label("Final Scores — End of Round 10");
            subtitle.setStyle("-fx-text-fill: #888; -fx-font-size: 12;");

            Button rankingBtn = new Button("🌍 View Global Ranking");
            rankingBtn.setStyle(
                    "-fx-background-color: #2980b9; -fx-text-fill: white;" +
                            "-fx-font-size: 14; -fx-padding: 8 24;" +
                            "-fx-background-radius: 6; -fx-cursor: hand;"
            );
            rankingBtn.setOnAction(e -> showRankingPopup());

            VBox content = new VBox(20, winnerLabel, subtitle, new Separator(), scoreboard, rankingBtn);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(40));
            content.setMaxWidth(520);
            content.setStyle(
                    "-fx-background-color: rgba(20,10,5,0.92);" +
                            "-fx-background-radius: 16;"
            );

            sceneRoot = new StackPane(background, content);
            primaryStage.setScene(new Scene(sceneRoot,
                    primaryStage.getWidth(), primaryStage.getHeight()));


        });

    }
    private void showRankingPopup() {
        if (sceneRoot == null || globalRanking == null) {
            showToast("Ranking data not available yet.");
            return;
        }
        Region dim = new Region();
        dim.setStyle("-fx-background-color: rgba(0,0,0,0.85);");

        Label title = new Label("Global Leaderboard");
        title.setStyle("-fx-text-fill: #e8c46a; -fx-font-size: 24; -fx-font-weight: bold;");

        VBox rowsBox = new VBox(8);
        rowsBox.setAlignment(Pos.CENTER);

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 16, 10, 16));
        header.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-background-radius: 6;");

        Label hPos = new Label("Pos"); hPos.setPrefWidth(40); hPos.setStyle("-fx-text-fill: #f5e6c8; -fx-font-weight: bold;");
        Label hName = new Label("Nickname"); hName.setPrefWidth(150); hName.setStyle("-fx-text-fill: #f5e6c8; -fx-font-weight: bold;");
        Label hWins = new Label("Wins"); hWins.setPrefWidth(60); hWins.setStyle("-fx-text-fill: #f5e6c8; -fx-font-weight: bold;");
        Label hScore = new Label("Total PP"); hScore.setPrefWidth(70); hScore.setStyle("-fx-text-fill: #f5e6c8; -fx-font-weight: bold;");

        header.getChildren().addAll(hPos, hName, hWins, hScore);
        rowsBox.getChildren().add(header);

        for (RankingRow row : globalRanking) {
            HBox rowBox = new HBox(15);
            rowBox.setAlignment(Pos.CENTER_LEFT);
            rowBox.setPadding(new Insets(8, 16, 8, 16));

            // Highlight row for current player
            boolean isMe = row.getNickname().equals(myName);
            rowBox.setStyle(isMe
                    ? "-fx-background-color: rgba(200,150,0,0.3); -fx-background-radius: 6;"
                    : "-fx-background-color: rgba(255,255,255,0.06); -fx-background-radius: 6;");

            Label rPos = new Label("#" + row.getPosition());
            rPos.setPrefWidth(40);
            rPos.setStyle("-fx-text-fill: #aaa; -fx-font-size: 14;");

            Label rName = new Label(row.getNickname());
            rName.setPrefWidth(150);
            rName.setStyle(isMe ? "-fx-text-fill: #e8c46a; -fx-font-weight:bold; -fx-font-size: 14;" : "-fx-text-fill: #f5e6c8; -fx-font-size: 14;");

            Label rWins = new Label(String.valueOf(row.getTotalWin()));
            rWins.setPrefWidth(60);
            rWins.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 14;");

            Label rScore = new Label(String.valueOf(row.getScore()));
            rScore.setPrefWidth(70);
            rScore.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 14;");

            rowBox.getChildren().addAll(rPos, rName, rWins, rScore);
            rowsBox.getChildren().add(rowBox);
        }

        ScrollPane scroll = new ScrollPane(rowsBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(350);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Button closeBtn = new Button("✕ Close");
        closeBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 8 24; -fx-background-radius: 6; -fx-cursor: hand;");

        VBox popup = new VBox(20, title, scroll, closeBtn);
        popup.setAlignment(Pos.CENTER);
        popup.setPadding(new Insets(30));
        popup.setMaxWidth(450);
        popup.setStyle("-fx-background-color: rgba(20,10,5,0.98); -fx-background-radius: 12; -fx-border-color: #e8c46a; -fx-border-radius: 12; -fx-border-width: 2;");

        StackPane overlay = new StackPane(dim, popup);
        overlay.setAlignment(Pos.CENTER);

        closeBtn.setOnAction(e -> sceneRoot.getChildren().remove(overlay));
        dim.setOnMouseClicked(e -> sceneRoot.getChildren().remove(overlay));

        sceneRoot.getChildren().add(overlay);
    }

    @Override
    public void showLeaderboard(List<RankingRow> ranking, Map<String, Integer> playersPosition){
        Platform.runLater(() -> {
            this.globalRanking = ranking;
            this.globalPlayersPosition = playersPosition;
        });

    }
    @Override
    public void showValidCards(Map<CharacterEnum, List<CharacterCard>> tribe, List<BuildingCard> buildings) {
        // Using copy to avoid race condition
        Map<CharacterEnum, List<CharacterCard>> tribeSnapshot = new HashMap<>();
        if (tribe != null) {
            for (Map.Entry<CharacterEnum, List<CharacterCard>> entry : tribe.entrySet()) {
                tribeSnapshot.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }
        this.myTribe = tribeSnapshot;

        final Map<CharacterEnum, List<CharacterCard>> safeTribe = tribeSnapshot;
        final List<BuildingCard> safeBuildings = (buildings != null) ? new ArrayList<>(buildings) : new ArrayList<>();

        Platform.runLater(() -> {
            if (ownTribePane == null) return;
            ownTribePane.getChildren().clear();

            for (CharacterEnum type : CharacterEnum.values()) {
                List<CharacterCard> cards = safeTribe.get(type);
                if (cards == null || cards.isEmpty()) continue;

                VBox typeGroup = createCharacterGroup(type, cards);
                ownTribePane.getChildren().add(typeGroup);
            }

            if (!safeBuildings.isEmpty()) {
                VBox buildingGroup = createBuildingGroup(safeBuildings);
                ownTribePane.getChildren().add(buildingGroup);
            }
        });
    }

    private VBox createCharacterGroup(CharacterEnum type, List<CharacterCard> cards) {
        ImageView typeIcon = icon(type.name().toLowerCase());
        typeIcon.setFitHeight(42);
        typeIcon.setFitWidth(42);

        Label countLabel = new Label("× " + cards.size());
        countLabel.setStyle("-fx-text-fill: #e8c46a; -fx-font-size: 11; ");

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
        typeGroup.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-background-radius: 6; ");

        return typeGroup;
    }

    private VBox createBuildingGroup(List<BuildingCard> buildings) {
        Label buildingTitle = new Label("BUILDINGS ");
        buildingTitle.setStyle("-fx-text-fill: #e8c46a; -fx-font-size: 14; -fx-font-weight: bold; ");

        VBox buildingColumn = new VBox(4);
        buildingColumn.setAlignment(Pos.TOP_CENTER);
        for (BuildingCard card : buildings) {
            buildingColumn.getChildren().add(cardImage(card.getImage()));
        }

        VBox buildingGroup = new VBox(29, buildingTitle, buildingColumn);
        buildingGroup.setAlignment(Pos.TOP_CENTER);
        buildingGroup.setPadding(new Insets(12, 6, 4, 6));
        buildingGroup.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-background-radius: 6; ");

        return buildingGroup;
    }



    /**
     * @author daniele
     * The following 3 methods are used to render the card face
     * @param image image to render
     * @return iv
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
            if("server".equals(playerName)){
                showReconnectingOverlay(); // if player crashes
            }else {
                showToast(playerName + " has disconnected.");// if other player crashes
            }
        });
    }

    private void showReconnectingOverlay(){
        if(reconnectingOverlay != null) return;

        Label label = new Label("Connection lost. Reconnecting ...");
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setMaxSize(40,40);

        VBox box = new VBox(15,spinner,label);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 30;");

        reconnectingOverlay = new StackPane(box);
        reconnectingOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");

        Pane currentRoot = (Pane) primaryStage.getScene().getRoot();
        currentRoot.getChildren().add(reconnectingOverlay);

    }

    public void hideReconnectingOverlay(){
        Platform.runLater(() -> {
            if(reconnectingOverlay != null) {
                Pane currentRoot = (Pane) primaryStage.getScene().getRoot();
                currentRoot.getChildren().remove(reconnectingOverlay);
                reconnectingOverlay = null;
            }
        });
    }

    @Override
    public void showPlayerReconnected(String playerName) {
        Platform.runLater(() -> showToast( playerName + " has reconnected."));
    }

    private void closeSuspendedDialogIfOpen(){
        if(countdownTimeline!= null){
            countdownTimeline.stop();
            countdownTimeline = null;
        }
        if(suspendedDialog != null){
            suspendedDialog.close();
            suspendedDialog = null;
        }
    }

    @Override
    public void showGameSuspended(int timeoutSeconds) {
        Platform.runLater(() -> {
            if (sceneRoot == null) return;
            if (suspendedOverlay != null) {
                sceneRoot.getChildren().remove(suspendedOverlay);
            }

            //*****overlay suspension popUp*****//
            Label title = new Label("⚠️ Game Suspended");
            title.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 18; -fx-font-weight: bold;");

            Label desc = new Label("Waiting for players...");
            desc.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 14;");

            Label timerLabel = new Label(timeoutSeconds + "s");
            timerLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 26; -fx-font-weight: bold;");

            suspendedOverlay = new VBox(8, title, desc, timerLabel);
            suspendedOverlay.setAlignment(Pos.CENTER);
            suspendedOverlay.setPadding(new Insets(15, 25, 15, 25));

            suspendedOverlay.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

            suspendedOverlay.setStyle(
                    "-fx-background-color: rgba(20, 10, 5, 0.9);" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 8;"
            );
            // You can change Pos.BOTTOM_RIGHT in Pos.TOP_LEFT o Pos.TOP_RIGHT to your likings
            StackPane.setAlignment(suspendedOverlay, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(suspendedOverlay, new Insets(20)); // Distance from borders
            sceneRoot.getChildren().add(suspendedOverlay);
            final int[] timeRemaining = {timeoutSeconds};

            if (countdownTimeline != null) {
                countdownTimeline.stop();
            }

            countdownTimeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> {
                timeRemaining[0]--;
                timerLabel.setText(timeRemaining[0] + "s");
                if (timeRemaining[0] <= 0) {
                    countdownTimeline.stop();
                }
            }));
            countdownTimeline.setCycleCount(Timeline.INDEFINITE);
            countdownTimeline.play();
        });
    }

    @Override
    public void showGameResumed() {
        Platform.runLater(() -> {
            if (countdownTimeline != null) {
                countdownTimeline.stop();
                countdownTimeline = null;
            }
            if (suspendedOverlay != null && sceneRoot != null) {
                sceneRoot.getChildren().remove(suspendedOverlay);
                suspendedOverlay = null;
            }
            if (suspendedDialog != null) {
                suspendedDialog.setOnCloseRequest(null);
                suspendedDialog.close();
                suspendedDialog = null;
            }
            showToast("All players are back — game is resuming!");
        });

    }
    @Override
    public void showReconnectedTotem(ColorEnum totemColor) {
        Platform.runLater(() -> {
            if (upperRowPane == null) {
                buildGameScene();
            }

            if(sceneRoot== null) return;
            Region dim = new Region();
            dim.setStyle("-fx-background-color: rgba(0,0,0,0.65);");

            Label title = new Label("🔄 Totem Restored");
            title.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 18; -fx-font-weight: bold;"); // Yellow

            Label desc = new Label("Your original totem color has been reassigned.");
            desc.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 14;");

            Label totemName = new Label(totemColor.name());
            totemName.setStyle("-fx-text-fill: #e8c46a; -fx-font-size: 22; -fx-font-weight: bold;");

            Button okBtn = new Button("OK");
            okBtn.setStyle(
                    "-fx-background-color: #2980b9; -fx-text-fill: white;" +
                            "-fx-font-size: 14; -fx-padding: 6 24; -fx-background-radius: 6; -fx-cursor: hand;"
            );

            VBox popup = new VBox(12, title, desc, totemName, okBtn);
            popup.setAlignment(Pos.CENTER);
            popup.setPadding(new Insets(20, 30, 20, 30));
            popup.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
            popup.setStyle(
                    "-fx-background-color: rgba(20, 10, 5, 0.95);" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-color: #f1c40f; -fx-border-width: 2; -fx-border-radius: 12;"
            );

            StackPane overlay = new StackPane(dim, popup);
            overlay.setAlignment(Pos.CENTER);

            okBtn.setOnAction(e -> sceneRoot.getChildren().remove(overlay));

            sceneRoot.getChildren().add(overlay);
        });
    }
    private int calculateBuilderDiscount() {
        List<CharacterCard> builders = myTribe.get(CharacterEnum.BUILDER);
        if (builders == null || builders.isEmpty()) return 0;

        int discount = 0;
        for (CharacterCard card : builders) {
            discount += ((Builder) card).getWingCount();
        }
        return discount;
    }
    @Override
    public  void showWaitingForRecovery(int playersStillNeeded){
        Platform.runLater(() -> {
            if (sceneRoot == null) return;

            if (suspendedOverlay != null) {
                sceneRoot.getChildren().remove(suspendedOverlay);
            }

            Label title = new Label("🔄 Server Recovered");
            title.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 18; -fx-font-weight: bold;");

            Label desc = new Label("Waiting for " + playersStillNeeded + " player(s) to resume...");
            desc.setStyle("-fx-text-fill: #f5e6c8; -fx-font-size: 14;");

            ProgressIndicator spinner = new ProgressIndicator();
            spinner.setMaxSize(30, 30);

            suspendedOverlay = new VBox(12, title, desc, spinner);
            suspendedOverlay.setAlignment(Pos.CENTER);
            suspendedOverlay.setPadding(new Insets(15, 25, 15, 25));
            suspendedOverlay.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

            suspendedOverlay.setStyle(
                    "-fx-background-color: rgba(20, 10, 5, 0.9);" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: #2ecc71; -fx-border-width: 2; -fx-border-radius: 8;"
            );

            StackPane.setAlignment(suspendedOverlay, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(suspendedOverlay, new Insets(20));
            sceneRoot.getChildren().add(suspendedOverlay);
        });

    }

    @Override
    public void showServerCrashed(){
        Platform.runLater(() -> {
            showReconnectingOverlay();
        });

    }

    @Override
    public void resetInputState(){
        Platform.runLater(() -> {
            if (actionBar != null) {
                actionBar.setVisible(false);
                actionBar.setManaged(false);
                actionBar.getChildren().clear();
            }

            if (upperRowPane != null) clearRowHandlers(upperRowPane);
            if (lowerRowPane != null) clearRowHandlers(lowerRowPane);
            if (buildingUpperPane != null) clearRowHandlers(buildingUpperPane);
            if (buildingLowerPane != null) clearRowHandlers(buildingLowerPane);
            if (offerTrackPane != null) clearOfferTrackHandlers();

            closeSuspendedDialogIfOpen();
        });
    }

}

