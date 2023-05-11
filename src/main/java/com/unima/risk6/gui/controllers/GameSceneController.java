package com.unima.risk6.gui.controllers;

import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.ai.tutorial.Tutorial;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.observers.GameStateObserver;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Move;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.game.models.enums.PlayerColor;
import com.unima.risk6.gui.configurations.CountriesUiConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SoundConfiguration;
import com.unima.risk6.gui.scenes.GameScene;
import com.unima.risk6.gui.uiModels.ActivePlayerUi;
import com.unima.risk6.gui.uiModels.ChatUi;
import com.unima.risk6.gui.uiModels.CountryUi;
import com.unima.risk6.gui.uiModels.DiceUi;
import com.unima.risk6.gui.uiModels.HandUi;
import com.unima.risk6.gui.uiModels.PlayerUi;
import com.unima.risk6.gui.uiModels.TimeUi;
import com.unima.risk6.gui.uiModels.TroopsCounterUi;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Popup;
import javafx.util.Duration;

public class GameSceneController implements GameStateObserver {

  private static final PlayerController PLAYER_CONTROLLER = new PlayerController();
  private static PlayerUi myPlayerUi;
  private final GameScene gameScene;
  private final SceneController sceneController;
  private GameState gameState;
  private BorderPane root;
  private Set<CountryUi> countriesUis;
  private double originalScreenWidth;
  private double originalScreenHeight;
  private Group countriesGroup;
  private LinkedList<PlayerUi> playerUis;
  private TimeUi timeUi;
  private ActivePlayerUi activePlayerUi;
  private Button nextPhaseButton;
  private Popup statisticPopup;
  boolean isStatisticsShowing = false;

  private ChatUi chatUi;

  private HandUi handUi;

  public GameSceneController(GameScene gameScene) {
    this.gameScene = gameScene;
    this.sceneController = SceneConfiguration.getSceneController();
  }

  public static PlayerUi getMyPlayerUi() {
    return myPlayerUi;
  }

  public static PlayerController getPlayerController() {
    return PLAYER_CONTROLLER;
  }

  public void init() {
    Tutorial tutorial = GameConfiguration.getTutorial();
    if (tutorial != null) {
      System.out.println("Tutorial is not null");
      this.gameState = tutorial.getTutorialState();
      System.out.println(gameState);
    } else {
      this.gameState = GameConfiguration.getGameState();
    }
    this.countriesUis = CountriesUiConfiguration.getCountriesUis();
    this.originalScreenWidth = SceneConfiguration.getWidth();
    this.originalScreenHeight = SceneConfiguration.getHeight();
    this.root = (BorderPane) gameScene.getRoot();
    this.countriesGroup = new Group();
    this.initializeGameScene();
    this.chatUi = new ChatUi(gameScene);
    this.handUi = new HandUi(gameScene, countriesUis);
    Hand hand = gameState.getActivePlayers().stream()
        .filter(player -> player.equals(myPlayerUi.getPlayer())).findFirst().get().getHand();
    this.handUi.setHand(hand);
    GameConfiguration.addObserver(this);
    this.addListeners();
  }

  private void initializeGameScene() {
    Image waterImage = new Image(
        getClass().getResource("/com/unima/risk6/pictures/flowingWater.gif").toString());
    BackgroundImage backgroundImage = new BackgroundImage(waterImage, BackgroundRepeat.REPEAT,
        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

    root.setBackground(new Background(backgroundImage));
    StackPane playerPane = initializePlayersPane();
    root.setLeft(playerPane);
    StackPane countriesPane = initializeCountriesPane();
    root.setCenter(countriesPane);
    StackPane timePane = initializeTimePane();
    root.setRight(timePane);
    StackPane bottomPane = initializeBottomPane();
    root.setBottom(bottomPane);

  }

  public void showOrderPopup() {

    BorderPane orderPane = new BorderPane();
    Popup orderPopup = new Popup();
    orderPopup.getContent().add(orderPane);

    Image image = new Image(
        getClass().getResource("/com/unima/risk6/pictures/dicePreview.png").toString());

    int numImages = gameState.getActivePlayers().size() - 1;

    Group otherDiceGroup = new Group();

    //TODO LOGIC: REPLACE IMAGES WITH PREDETERMINED DICEUI. AS SOON AS OTHER PLAYERS HAVE CLICKED
    // THEIR DICE BUTTON IT SHOULD UPDATE THE OTHERS

    if (numImages <= 2) {
      Arc semicircle = new Arc();
      semicircle.setCenterX(0);
      semicircle.setCenterY(0);
      semicircle.setRadiusX(300);
      semicircle.setRadiusY(150);
      semicircle.setStartAngle(0);
      semicircle.setLength(-180);
      double imageWidth = image.getWidth();
      double imageHeight = image.getHeight();
      for (int i = 0; i < numImages; i++) {
        double angle = Math.toRadians(
            semicircle.getStartAngle() + i * semicircle.getLength() / (numImages));
        double x =
            semicircle.getCenterX() + semicircle.getRadiusX() * Math.cos(angle) - imageWidth / 2;
        double y =
            semicircle.getCenterY() + semicircle.getRadiusY() * Math.sin(angle) - imageHeight / 2;
        ImageView imageView = new ImageView(image);
        imageView.setX(x);
        imageView.setY(y);
        otherDiceGroup.getChildren().add(imageView);
        orderPane.setCenter(otherDiceGroup);
      }
    } else {
      otherDiceGroup.getChildren().add(new ImageView(image));
      orderPane.setTop(otherDiceGroup);
    }

    VBox myDiceBox = new VBox();

    //TODO LOGIC: REPLACE THIS MOCKED DICEUI WITH PREDETERMINED RESULT VALUE FROM LOGIC

    DiceUi myDice = new DiceUi(false, 3);

    Button rollMyDiceButton = new Button("Roll the Dice!");
    rollMyDiceButton.setStyle(
        "-fx-background-radius: 15px; -fx-font-size: 14; -fx-font-weight: bold;");
    rollMyDiceButton.setFocusTraversable(false);

    PauseTransition delayTransitionHidePopup = new PauseTransition(Duration.millis(3000));
    delayTransitionHidePopup.setOnFinished(delayTransitionEvent -> orderPopup.hide());

    PauseTransition delayTransitionShowOrder = new PauseTransition(Duration.millis(4000));
    delayTransitionShowOrder.setOnFinished(delayTransitionEvent -> {
      orderPane.getChildren().clear();
      Label orderLabel = new Label("You are " + myDice.getResult() + ". Place. Good Luck!");
      orderLabel.setStyle("-fx-font-size: 29px;");

      HBox orderBox = new HBox();
      orderBox.setAlignment(Pos.CENTER);
      orderBox.getChildren().add(orderLabel);
      orderPane.setCenter(orderBox);
      delayTransitionHidePopup.play();
    });

    rollMyDiceButton.setOnAction(event -> {
      myDice.rollDice();
      delayTransitionShowOrder.play();
    });

    myDiceBox.getChildren().addAll(myDice, rollMyDiceButton);
    myDiceBox.setSpacing(10);

    HBox hBox = new HBox(myDiceBox);
    hBox.setAlignment(Pos.CENTER);

    orderPane.setBottom(hBox);
    BorderPane.setMargin(hBox, new Insets(10));

    orderPane.setStyle("-fx-background-radius: 10;-fx-background-color: rgba(255, 255, 255, 0.3);");

    orderPane.setPrefSize(root.getWidth() * 0.70, root.getHeight() * 0.70);

    Bounds rootBounds = root.localToScreen(root.getBoundsInLocal());

    double centerX = rootBounds.getMinX() + rootBounds.getWidth() / 2;
    double centerY = rootBounds.getMinY() + rootBounds.getHeight() / 2;
    double popupWidth = orderPane.getPrefWidth();
    double popupHeight = orderPane.getPrefHeight();
    orderPopup.setX(centerX - popupWidth / 2);
    orderPopup.setY(centerY - popupHeight / 2);
    orderPopup.show(gameScene.getWindow());
  }

  private StackPane initializeCountriesPane() {
    double widthRatio = gameScene.getWidth() / originalScreenWidth;
    double heightRatio = gameScene.getHeight() / originalScreenHeight;
    double initialScale = Math.min(widthRatio, heightRatio);

    countriesGroup.getChildren().addAll(countriesUis);
    StackPane countriesPane = new StackPane();

    for (CountryUi countryUi : countriesUis) {
      Bounds bounds = countryUi.getCountryPath().getBoundsInParent();
      double ellipseX = bounds.getMinX() + bounds.getWidth() * 0.5;
      double ellipseY = bounds.getMinY() + bounds.getHeight() * 0.5;
      countryUi.initMouseListener();

      Point2D correctedCoordinates = CountryUi.correctEllipsePlacement(countryUi.getCountry(),
          ellipseX, ellipseY);
      ellipseX = correctedCoordinates.getX();
      ellipseY = correctedCoordinates.getY();

      TroopsCounterUi troopsCounterUi = new TroopsCounterUi(ellipseX, ellipseY);
      troopsCounterUi.setText(countryUi.getCountry().getTroops().toString());
      countryUi.setTroopsCounterUi(troopsCounterUi);
      countriesGroup.getChildren().add(troopsCounterUi);

    }
    countriesGroup.setScaleX(initialScale);
    countriesGroup.setScaleY(initialScale);

    countriesPane.getChildren().add(countriesGroup);
    return countriesPane;
  }

  private StackPane initializePlayersPane() {
    VBox playersVbox = new VBox();
    playersVbox.setMaxWidth(100);

    PlayerColor[] possibleColors = {PlayerColor.RED, PlayerColor.BLUE, PlayerColor.PURPLE,
        PlayerColor.YELLOW, PlayerColor.GREEN, PlayerColor.ORANGE};
    int colorIndex = 0;
    playerUis = new LinkedList<>();
    for (Player player : gameState.getActivePlayers()) {
      PlayerUi playerUi = new PlayerUi(player, possibleColors[colorIndex].getColor(), 35, 35, 120,
          45);
      playerUis.offer(playerUi);
      if (playerUi.getPlayer().getUser().equals(GameConfiguration.getMyGameUser().getUsername())) {
        myPlayerUi = playerUi;
        PLAYER_CONTROLLER.setPlayer(player);
        PLAYER_CONTROLLER.getHandController().setHand(player.getHand());
        System.out.println("My player: " + playerUi.getPlayer().getUser());
      }
      colorIndex++;
    }

    playersVbox.getChildren().addAll(playerUis);
    playersVbox.setAlignment(Pos.CENTER);
    playersVbox.setSpacing(10);
    StackPane playerPane = new StackPane();
    playerPane.getChildren().add(playersVbox);
    playerPane.setPadding(new Insets(0, 0, 0, 15));
    return playerPane;
  }

  private StackPane initializeTimePane() {
    StackPane timePane = new StackPane();
    timeUi = new TimeUi(120, 120, gameState.getPhaseTime());
    timePane.getChildren().add(timeUi);
    timePane.setAlignment(Pos.CENTER);
    timePane.setPadding(new Insets(0, 15, 0, 0));
    return timePane;
  }

  private StackPane initializeBottomPane() {
    StackPane bottomPane = new StackPane();
    Button chatButton = new Button();
    ImageView chatIcon = new ImageView(
        new Image(getClass().getResource("/com/unima/risk6/pictures/chatIcon.png").toString()));
    chatIcon.setFitWidth(40);
    chatIcon.setFitHeight(40);
    chatButton.setGraphic(chatIcon);
    chatButton.setStyle("-fx-background-radius: 15px;");
    chatButton.setFocusTraversable(false);
    chatButton.setOnAction(event -> {
      chatUi.show();
    });

    activePlayerUi = new ActivePlayerUi(40, 40, 300, 75, getCurrentPlayerUi());
    activePlayerUi.controlDeployableTroops();
    nextPhaseButton = new Button();
    ImageView rightArrowIcon = new ImageView(new Image(
        getClass().getResource("/com/unima/risk6/pictures/rightArrowIcon.png").toString()));
    rightArrowIcon.setFitWidth(50);
    rightArrowIcon.setFitHeight(50);
    nextPhaseButton.setGraphic(rightArrowIcon);
    nextPhaseButton.setStyle("-fx-background-radius: 20px;");
    nextPhaseButton.setFocusTraversable(false);
    nextPhaseButton.setVisible(checkIfCurrentPlayerIsMe()
        && activePlayerUi.getPlayerUi().getPlayer().getCurrentPhase() != GamePhase.CLAIM_PHASE);
    nextPhaseButton.setOnAction(event -> {
      GamePhase currentPhase = myPlayerUi.getPlayer().getCurrentPhase();
      PLAYER_CONTROLLER.sendEndPhase(currentPhase);
      for (CountryUi countryUi : countriesUis) {
        countryUi.removeArrowsAndAdjacentCountries();
      }
    });

    Button cardsButton = new Button();
    ImageView cardsGroup = new ImageView(
        new Image(getClass().getResource("/com/unima/risk6/pictures/cardsGroup.png").toString()));
    cardsGroup.setFitWidth(150);
    cardsGroup.setFitHeight(80);
    cardsButton.setGraphic(cardsGroup);
    cardsButton.setStyle("-fx-background-radius: 15px;");
    cardsButton.setFocusTraversable(false);
    cardsButton.setOnAction(event -> showCardsPopup());

    bottomPane.getChildren().addAll(cardsButton, activePlayerUi, nextPhaseButton, chatButton);
    bottomPane.setAlignment(Pos.CENTER);
    StackPane.setMargin(nextPhaseButton, new Insets(0, 0, 5, 525));
    StackPane.setAlignment(cardsButton, Pos.CENTER_LEFT);
    StackPane.setMargin(cardsButton, new Insets(0, 0, 0, 15));
    StackPane.setAlignment(chatButton, Pos.CENTER_RIGHT);
    StackPane.setMargin(chatButton, new Insets(0, 15, 0, 0));
    bottomPane.setPadding(new Insets(0, 0, 15, 0));
    return bottomPane;
  }

  private void showCardsPopup() {
    handUi.show();
  }

  private void addListeners() {
    gameScene.widthProperty().addListener((obs, oldVal, newVal) -> {
      double widthScale = newVal.doubleValue() / originalScreenWidth;
      double heightScale = gameScene.getHeight() / originalScreenHeight;
      double scale = Math.min(widthScale, heightScale);
      countriesGroup.setScaleX(scale + 0.4);
      countriesGroup.setScaleY(scale + 0.4);
    });

    gameScene.heightProperty().addListener((obs, oldVal, newVal) -> {
      double widthScale = gameScene.getWidth() / originalScreenWidth;
      double heightScale = newVal.doubleValue() / originalScreenHeight;
      double scale = Math.min(widthScale, heightScale);
      countriesGroup.setScaleX(scale + 0.3);
      countriesGroup.setScaleY(scale + 0.3);
    });

    gameScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      if (event.getCode() == KeyCode.TAB && !isStatisticsShowing) {
        showStatisticsPopup();
        event.consume();
      }
    });
    gameScene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
      if (event.getCode() == KeyCode.TAB) {
        statisticPopup.hide();
        isStatisticsShowing = false;
        event.consume();
      }
    });
  }

  private void showStatisticsPopup() {
    BorderPane statisticPane = new BorderPane();
    GridPane statisticsGrid = new GridPane();
    statisticsGrid.setAlignment(Pos.CENTER);
    statisticsGrid.setHgap(10);

    playerUis.sort((p1, p2) -> {
      int numOwnedCountries1 = p1.getPlayer().getStatistic().getNumberOfOwnedCountries();
      int numOwnedCountries2 = p2.getPlayer().getStatistic().getNumberOfOwnedCountries();

      if (numOwnedCountries1 == numOwnedCountries2) {
        int numTroops1 = p1.getPlayer().getStatistic().getNumberOfTroops();
        int numTroops2 = p2.getPlayer().getStatistic().getNumberOfTroops();

        if (numTroops1 == numTroops2) {
          return 0;
        } else {
          return numTroops2 - numTroops1;
        }
      } else {
        return numOwnedCountries2 - numOwnedCountries1;
      }
    });

    for (int i = 0; i < playerUis.size(); i++) {
      Player player = playerUis.get(i).getPlayer();
      VBox userBox = new VBox();
      userBox.setAlignment(Pos.CENTER);
      userBox.setSpacing(5);
      Label userLabel = new Label(player.getUser());
      userLabel.setStyle("-fx-font-size: 16px;");
      Circle userCircle = new Circle(40);
      userCircle.setStroke(Color.BLACK);
      ImageView userImage;
      if (player instanceof EasyBot) {
        userImage = new ImageView(
            new Image(getClass().getResource("/com/unima/risk6/pictures/easyBot.png").toString()));
      } else if (player instanceof MediumBot) {
        userImage = new ImageView(new Image(
            getClass().getResource("/com/unima/risk6/pictures/mediumBot.png").toString()));
      } else if (player instanceof HardBot) {
        userImage = new ImageView(
            new Image(getClass().getResource("/com/unima/risk6/pictures/hardBot.png").toString()));
      } else {
        userImage = new ImageView(new Image(
            getClass().getResource("/com/unima/risk6/pictures/playerIcon.png").toString()));
      }
      userCircle.setFill(new ImagePattern(userImage.getImage()));
      userBox.getChildren().addAll(userLabel, userCircle);
      statisticsGrid.add(userBox, i, 0);

      String[] attributeStrings = {"Troops: ", "Countries: "};
      for (int j = 0; j < attributeStrings.length; j++) {
        HBox statisticBox = new HBox();
        statisticBox.setPadding(new Insets(5));
        statisticBox.setAlignment(Pos.CENTER);
        int value;
        if (j == 0) {
          value = player.getStatistic().getNumberOfTroops();
        } else {
          value = player.getStatistic().getNumberOfOwnedCountries();
        }
        Label statisticName = new Label(attributeStrings[j]);
        statisticName.setStyle("-fx-font-size: 15px;");
        Label userStat = new Label(Integer.toString(value));
        userStat.setStyle("-fx-font-size: 15px; -fx-font-weight: bold");
        statisticBox.getChildren().addAll(statisticName, userStat);
        statisticsGrid.add(statisticBox, i, j + 1);
      }
    }
    statisticPane.setCenter(statisticsGrid);
    statisticPane.setPrefSize(gameScene.getWidth() * 0.7, gameScene.getHeight() * 0.7);
    statisticPane.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");

    statisticPopup = new Popup();
    statisticPopup.getContent().add(statisticPane);
    DropShadow dropShadow = new DropShadow();
    dropShadow.setColor(Color.BLACK);
    dropShadow.setRadius(10);
    statisticPane.setEffect(dropShadow);
    double popupWidth = statisticPane.getPrefWidth();
    double popupHeight = statisticPane.getPrefHeight();
    statisticPopup.setX(
        (gameScene.getWindow().getX() + gameScene.getWindow().getWidth() / 2) - popupWidth / 2);
    statisticPopup.setY(
        (gameScene.getWindow().getY() + gameScene.getWindow().getHeight() / 2) - popupHeight / 2);
    statisticPopup.show(gameScene.getWindow());
    isStatisticsShowing = true;
  }

  @Override
  public void update(GameState gameState) {
    this.gameState = gameState;
    Queue<Move> lastMoves = gameState.getLastMoves();
    Iterator<Move> iterator = lastMoves.iterator();
    updateReferencesFromGameState();
    while (iterator.hasNext()) {
      Move lastMove = iterator.next();
      if (lastMove instanceof Fortify fortify) {
        Platform.runLater(() -> animateTroopsMovement(fortify));
      } else if (lastMove instanceof Attack attack) {
        Platform.runLater(() -> animateAttack(attack));
      } else if (lastMove instanceof Reinforce reinforce) {
        Platform.runLater(() -> {
          animateReinforce(reinforce);
          updateActivePlayerTroops();
        });
      } else if (lastMove instanceof HandIn handIn) {
        Platform.runLater(() -> animateHandIn(handIn));
      } else if (lastMove instanceof EndPhase endPhase) {
        Platform.runLater(() -> animateEndPhase(endPhase));
      }
      iterator.remove();
    }
    Platform.runLater(() -> {
      playerUis.forEach(PlayerUi::updateAmountOfTroops);
      handUi.setHand(myPlayerUi.getPlayer().getHand());
    });

  }

  private void updateActivePlayerTroops() {
    if (activePlayerUi.getPlayerUi().getPlayer().getCurrentPhase()
        == GamePhase.REINFORCEMENT_PHASE) {
      activePlayerUi.updateActivePlayerTroops();
    }
  }

  private void updateReferencesFromGameState() {
    this.gameState.getActivePlayers().forEach(player -> {
      if (player.getUser().equals(GameConfiguration.getMyGameUser().getUsername())) {
        myPlayerUi.setPlayer(player);
        PLAYER_CONTROLLER.setPlayer(player);
        PLAYER_CONTROLLER.getHandController().setHand(player.getHand());
      } else {
        playerUis.forEach(playerUi -> {
          if (playerUi.getPlayer().getUser().equals(player.getUser())) {
            playerUi.setPlayer(player);
          }
        });
      }
    });
    this.gameState.getCountries().forEach(country -> countriesUis.forEach(countryUi -> {
      if (countryUi.getCountry().getCountryName().equals(country.getCountryName())) {
        countryUi.setCountry(country);
      }
    }));
    countriesUis.forEach(countryUi -> countryUi.getAdjacentCountryUis().forEach(
        adjacentCountryUi -> adjacentCountryUi.setCountry(countriesUis.stream().filter(
                countryUi1 -> countryUi1.getCountry().getCountryName()
                    .equals(adjacentCountryUi.getCountry().getCountryName())).findFirst().get()
            .getCountry())));
    handUi.setHand(myPlayerUi.getPlayer().getHand());
    //TODO Update reference for the deckUi,handUi, etc
  }

  public CountryUi getCountryUiByCountry(Country country) {
    return countriesUis.stream().filter(countryUi -> countryUi.getCountry().equals(country))
        .findFirst().get();
  }


  public void animateTroopsMovement(Fortify fortify) {
    double maxOffsetX = 2.5;
    double maxOffsetY = 2.5;
    Image image = new Image(
        getClass().getResource("/com/unima/risk6/pictures/InfantryRunning.gif").toString());

    for (int i = 0; i < fortify.getTroopsToMove(); i++) {
      ImageView imageView = new ImageView(image);
      imageView.setFitWidth(35);
      imageView.setFitHeight(35);
      double offsetX = Math.random() * maxOffsetX;
      double offsetY = Math.random() * maxOffsetY;
      CountryUi countryUiFrom = getCountryUiByCountry(fortify.getOutgoing());
      CountryUi countryUiTo = getCountryUiByCountry(fortify.getIncoming());
      Point2D clickPosInScene = countryUiFrom.localToScene(
          countryUiFrom.getTroopsCounterUi().getEllipseCounter().getCenterX(),
          countryUiFrom.getTroopsCounterUi().getEllipseCounter().getCenterY());
      Point2D clickPosInSceneToCountry = countryUiTo.localToScene(
          countryUiTo.getTroopsCounterUi().getEllipseCounter().getCenterX(),
          countryUiTo.getTroopsCounterUi().getEllipseCounter().getCenterY());
      Point2D clickPosInGroup = countryUiFrom.sceneToLocal(clickPosInScene);
      Point2D clickPosInGroupToCountry = countryUiTo.sceneToLocal(clickPosInSceneToCountry);
      double startX = clickPosInGroup.getX() + i * offsetX;
      double startY = clickPosInGroup.getY() + i * offsetY;
      double endX = clickPosInGroupToCountry.getX() + i * offsetX;
      double endY = clickPosInGroupToCountry.getY() + i * offsetY;
      if (endX < startX) {
        imageView.setScaleX(-1); // flip horizontally
      }
      Path path = new Path();
      path.getElements().add(new MoveTo(startX, startY));
      path.getElements().add(new LineTo(endX, endY));
      PathTransition pathTransition = new PathTransition();
      pathTransition.setDuration(Duration.seconds(2));
      pathTransition.setPath(path);
      pathTransition.setNode(imageView);
      pathTransition.setOnFinished(onFinishedEvent -> {
        SoundConfiguration.stopTroopsMoveSound();
        countriesGroup.getChildren().remove(imageView);
      });
      pathTransition.play();
      countriesGroup.getChildren().add(imageView);
    }
    SoundConfiguration.playTroopsMoveSound();
    CountryUi countryUi1 = getCountryUiByCountry(fortify.getIncoming());
    CountryUi countryUi = getCountryUiByCountry(fortify.getOutgoing());
    countryUi.update(activePlayerUi);
    countryUi1.update(activePlayerUi);
  }

  private void animateAttack(Attack attack) {
    CountryUi countryUi = getCountryUiByCountry(attack.getAttackingCountry());
    CountryUi countryUi1 = getCountryUiByCountry(attack.getDefendingCountry());
    countryUi.updateAfterAttack(activePlayerUi, attack, countryUi, countryUi1);

  }

  private void animateReinforce(Reinforce reinforce) {
    CountryUi countryUi = getCountryUiByCountry(reinforce.getCountry());
    countryUi.update(activePlayerUi);

  }

  private void animateEndPhase(EndPhase endPhase) {
    updateActivePlayerUi();
    if (endPhase.getPhaseToEnd() == GamePhase.FORTIFY_PHASE) {
      StackPane.setMargin(nextPhaseButton, new Insets(0, 0, 5, 525));
      activePlayerUi.setDisplayDeployable(true);
      activePlayerUi.controlDeployableTroops();
    }
    if (endPhase.getPhaseToEnd() == GamePhase.REINFORCEMENT_PHASE) {
      StackPane.setMargin(nextPhaseButton, new Insets(0, 0, 5, 450));
      activePlayerUi.setDisplayDeployable(false);
      activePlayerUi.controlDeployableTroops();
    }
    nextPhaseButton.setVisible(
        checkIfCurrentPlayerIsMe() && (myPlayerUi.getPlayer().getCurrentPhase()
            != GamePhase.NOT_ACTIVE) && (myPlayerUi.getPlayer().getCurrentPhase()
            != GamePhase.CLAIM_PHASE));
  }

  private void animateHandIn(HandIn handIn) {
    activePlayerUi.updateActivePlayerTroops();
  }

  public boolean checkIfCurrentPlayerIsMe() {
    return gameState.getCurrentPlayer().getUser()
        .equals(GameConfiguration.getMyGameUser().getUsername());
  }

  public void updateActivePlayerUi() {
    activePlayerUi.changeActivePlayerUi(getCurrentPlayerUi());
  }

  public PlayerUi getCurrentPlayerUi() {
    Player currentPlayer = gameState.getCurrentPlayer();
    return playerUis.stream().filter(playerUi -> currentPlayer.equals(playerUi.getPlayer()))
        .findFirst().get();
  }

  public void initTutorial() {

  }
}

