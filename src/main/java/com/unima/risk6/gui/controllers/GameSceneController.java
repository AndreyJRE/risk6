package com.unima.risk6.gui.controllers;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.GameStateObserver;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Move;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.game.models.enums.PlayerColor;
import com.unima.risk6.gui.configurations.CountriesUiConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.scenes.GameScene;
import com.unima.risk6.gui.uiModels.ActivePlayerUi;
import com.unima.risk6.gui.uiModels.CountryUi;
import com.unima.risk6.gui.uiModels.PlayerUi;
import com.unima.risk6.gui.uiModels.TimeUi;
import com.unima.risk6.gui.uiModels.TroopsCounterUi;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import javafx.animation.PathTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Popup;
import javafx.util.Duration;

public class GameSceneController implements GameStateObserver {

  private GameState gameState;
//
//  private Stack<CardUi> cardUis;

  private BorderPane root;

  private Set<CountryUi> countriesUis;

  private double originalScreenWidth;

  private double originalScreenHeight;

  private BorderPane chatBoxPane;

  private Group countriesGroup;

  private final GameScene gameScene;

  private LinkedList<PlayerUi> playerUis;

  private final SceneController sceneController;

  public static GamePhase mockGamePhase;

  private static PlayerUi myPlayerUi;

  private TimeUi timeUi;

  private static final PlayerController PLAYER_CONTROLLER = new PlayerController();

  public GameSceneController(GameScene gameScene) {
    this.gameScene = gameScene;
    this.sceneController = SceneConfiguration.getSceneController();
  }

  public void init() {
    this.gameState = GameConfiguration.getGameState();
    this.originalScreenWidth = SceneConfiguration.getWidth();
    this.originalScreenHeight = SceneConfiguration.getHeight();
    this.countriesUis = CountriesUiConfiguration.getCountriesUis();
    this.root = (BorderPane) gameScene.getRoot();
    this.countriesGroup = new Group();
    this.chatBoxPane = new BorderPane();
    mockGamePhase = GamePhase.ATTACK_PHASE;
    GameConfiguration.addObserver(this);
    this.initializeGameScene();

    this.addListeners();
  }

  private void initializeGameScene() {
    Image waterImage = new Image(
        getClass().getResource("/com/unima/risk6/pictures/flowingWater.gif").toString());
    BackgroundImage backgroundImage = new BackgroundImage(waterImage,
        BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
        BackgroundSize.DEFAULT);

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
      PlayerUi playerUi = new PlayerUi(player, possibleColors[colorIndex].getColor(), 35,
          35, 100, 45);
      playerUis.offer(playerUi);
      if (playerUi.getPlayer().getUser().equals(GameConfiguration.getMyGameUser().getUsername())) {
        myPlayerUi = playerUi;
        PLAYER_CONTROLLER.setPlayer(player);
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
    timeUi = new TimeUi(120, 120);
    timePane.getChildren().add(timeUi);
    timePane.setAlignment(Pos.CENTER);
    timePane.setPadding(new Insets(0, 15, 0, 0));
    return timePane;
  }

  private StackPane initializeBottomPane() {
    StackPane bottomPane = new StackPane();
    Button chatButton = new Button();
    ImageView chatIcon = new ImageView(new Image(
        getClass().getResource("/com/unima/risk6/pictures/chatIcon.png").toString()));
    chatIcon.setFitWidth(40);
    chatIcon.setFitHeight(40);
    chatButton.setGraphic(chatIcon);
    chatButton.setStyle("-fx-background-radius: 15px;");
    chatButton.setFocusTraversable(false);

    Label chatLabel = new Label("This is a chat popup.");
    chatLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white;");

    Button closeButton = new Button();
    closeButton.setPrefSize(20, 20);
    ImageView closeIcon = new ImageView(new Image(
        getClass().getResource("/com/unima/risk6/pictures/closeIcon.png").toString()));
    closeIcon.setFitWidth(40);
    closeIcon.setFitHeight(40);
    closeButton.setGraphic(closeIcon);
    closeButton.setStyle("-fx-background-radius: 15px;");
    closeButton.setFocusTraversable(false);

    chatBoxPane.setTop(closeButton);
    chatBoxPane.setAlignment(closeButton, Pos.TOP_RIGHT);

    VBox chatBox = new VBox();
    chatBox.getChildren().addAll(chatLabel);

    chatBoxPane.setCenter(chatBox);

    chatBoxPane.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");

    Popup chatPopup = new Popup();
    chatPopup.getContent().add(chatBoxPane);

    closeButton.setOnAction(event -> chatPopup.hide());

    DropShadow dropShadow = new DropShadow();
    dropShadow.setColor(Color.BLACK);
    dropShadow.setRadius(10);
    chatBoxPane.setEffect(dropShadow);

    chatButton.setOnAction(event -> {
      chatBoxPane.setPrefSize(root.getWidth() * 0.70, root.getHeight() * 0.70);

      Bounds rootBounds = root.localToScreen(root.getBoundsInLocal());

      double centerX = rootBounds.getMinX() + rootBounds.getWidth() / 2;
      double centerY = rootBounds.getMinY() + rootBounds.getHeight() / 2;

      // Get the width and height of the popup
      double popupWidth = chatBoxPane.getPrefWidth();
      double popupHeight = chatBoxPane.getPrefHeight();

      // Set the position of the chat popup to center it in the root Scene
      chatPopup.setX(centerX - popupWidth / 2);
      chatPopup.setY(centerY - popupHeight / 2);
      chatPopup.show(chatButton.getScene().getWindow());
    });

    ActivePlayerUi activePlayerUi = new ActivePlayerUi(40,
        40, 300, 75, playerUis.peek(), mockGamePhase);
    playerUis.offer(playerUis.poll());

    Button nextPhaseButton = new Button();
    ImageView rightArrowIcon = new ImageView(new Image(
        getClass().getResource("/com/unima/risk6/pictures/rightArrowIcon.png").toString()));
    rightArrowIcon.setFitWidth(50);
    rightArrowIcon.setFitHeight(50);
    nextPhaseButton.setGraphic(rightArrowIcon);
    nextPhaseButton.setStyle("-fx-background-radius: 25px;");
    nextPhaseButton.setFocusTraversable(false);
    nextPhaseButton.setOnAction(event -> {
      //TODO: CHANGE ACTIVE PLAYER HERE IN FORTIFY CASE, AS NEW PLAYER TURN BEGINS
      switch (mockGamePhase) {
        case REINFORCEMENT_PHASE:
          mockGamePhase = GamePhase.ATTACK_PHASE;
          break;
        case ATTACK_PHASE:
          mockGamePhase = GamePhase.FORTIFY_PHASE;
          break;
        case FORTIFY_PHASE:
          mockGamePhase = GamePhase.REINFORCEMENT_PHASE;
          break;
      }
      activePlayerUi.changePhase();
    });

    Button cardsButton = new Button();
    ImageView cardsGroup = new ImageView(new Image(
        getClass().getResource("/com/unima/risk6/pictures/cardsGroup.png").toString()));
    cardsGroup.setFitWidth(150);
    cardsGroup.setFitHeight(80);
    cardsButton.setGraphic(cardsGroup);
    cardsButton.setStyle("-fx-background-radius: 15px;");
    cardsButton.setFocusTraversable(false);

    Button closeCardsButton = new Button();
    closeCardsButton.setPrefSize(20, 20);
    ImageView closeCardIcon = new ImageView(new Image(
        getClass().getResource("/com/unima/risk6/pictures/closeIcon.png").toString()));
    closeCardIcon.setFitWidth(40);
    closeCardIcon.setFitHeight(40);
    closeCardsButton.setGraphic(closeCardIcon);
    closeCardsButton.setStyle("-fx-background-radius: 15px;");
    closeCardsButton.setFocusTraversable(false);

    BorderPane cardsPane = new BorderPane();
    cardsPane.setTop(closeCardsButton);
    cardsPane.setAlignment(closeCardsButton, Pos.TOP_RIGHT);

    VBox cardsBox = new VBox();
    Label cardTitleLabel = new Label("This is cards popup.");
    cardTitleLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white;");
    cardsBox.getChildren().addAll(cardTitleLabel);

    cardsPane.setCenter(cardsBox);

    cardsPane.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");

    Popup cardsPopup = new Popup();
    cardsPopup.getContent().add(cardsPane);

    closeCardsButton.setOnAction(event -> cardsPopup.hide());

    cardsPane.setEffect(dropShadow);
    cardsButton.setOnAction(event -> {
      cardsPane.setPrefSize(root.getWidth() * 0.70, root.getHeight() * 0.70);

      Bounds rootBounds = root.localToScreen(root.getBoundsInLocal());

      double centerX = rootBounds.getMinX() + rootBounds.getWidth() / 2;
      double centerY = rootBounds.getMinY() + rootBounds.getHeight() / 2;

      // Get the width and height of the popup
      double popupWidth = cardsPane.getPrefWidth();
      double popupHeight = cardsPane.getPrefHeight();

      // Set the position of the chat popup to center it in the root Scene
      cardsPopup.setX(centerX - popupWidth / 2);
      cardsPopup.setY(centerY - popupHeight / 2);
      cardsPopup.show(cardsButton.getScene().getWindow());
    });

    bottomPane.getChildren().addAll(cardsButton, activePlayerUi, nextPhaseButton, chatButton);
    bottomPane.setAlignment(Pos.CENTER);
    bottomPane.setMargin(nextPhaseButton, new Insets(0, 0, 0, 450));
    bottomPane.setAlignment(cardsButton, Pos.CENTER_LEFT);
    bottomPane.setMargin(cardsButton, new Insets(0, 0, 0, 15));
    bottomPane.setAlignment(chatButton, Pos.CENTER_RIGHT);
    bottomPane.setMargin(chatButton, new Insets(0, 15, 0, 0));
    bottomPane.setPadding(new Insets(0, 0, 15, 0));
    return bottomPane;
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
  }

  public static void checkIfAllCountriesOccupied(Group countriesGroup) {
    boolean allOccupied = true;
    for (Node node : countriesGroup.getChildren()) {
      if (node instanceof CountryUi) {
        CountryUi countryUi = (CountryUi) node;
        if (countryUi.getCountryPath().getFill() == Color.WHITE) {
          allOccupied = false;
          break;
        }
      }
    }
    if (allOccupied) {
      mockGamePhase = GamePhase.ATTACK_PHASE;
    }
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
        animateFortify(fortify);
      } else if (lastMove instanceof Attack attack) {
        animateAttack(attack);
      } else if (lastMove instanceof Reinforce reinforce) {
        animateReinforce(reinforce);
      }
      iterator.remove();
    }
  }

  private void updateReferencesFromGameState() {
    //TODO Update all references to the new gameState
    this.gameState.getActivePlayers().forEach(player -> {
      if (player.getUser().equals(GameConfiguration.getMyGameUser().getUsername())) {
        myPlayerUi.setPlayer(player);
        PLAYER_CONTROLLER.setPlayer(player);
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
    countriesUis.forEach(countryUi -> countryUi.getAdjacentCountryUis()
        .forEach(adjacentCountryUi -> {
          adjacentCountryUi.setCountry(countriesUis.stream()
              .filter(countryUi1 -> countryUi1.getCountry().getCountryName()
                  .equals(adjacentCountryUi.getCountry().getCountryName()))
              .findFirst().get().getCountry());
        }));
    //TODO Update reference for the deckUi,handUi, etc
  }

  public static PlayerUi getMyPlayerUi() {
    return myPlayerUi;
  }

  public CountryUi getCountryUiByCountry(Country country) {
    return countriesUis.stream()
        .filter(countryUi -> countryUi.getCountry().equals(country))
        .findFirst().get();
  }

  public void animateFortify(Fortify fortify) {
    double maxOffsetX = 10;
    double maxOffsetY = 10;
    for (int i = 0; i < fortify.getTroopsToMove(); i++) {
      ImageView imageView = new ImageView(new Image(
          getClass().getResource("/com/unima/risk6/pictures/InfantryRunning.gif")
              .toString()));
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
      path.getElements().add(
          new MoveTo(startX, startY));
      path.getElements()
          .add(new LineTo(endX, endY));
      PathTransition pathTransition = new PathTransition();
      pathTransition.setDuration(Duration.seconds(2));
      pathTransition.setPath(path);
      pathTransition.setNode(imageView);
      pathTransition.setOnFinished(
          onFinishedEvent -> countriesGroup.getChildren().remove(imageView));
      pathTransition.play();
      countriesGroup.getChildren().add(imageView);
    }
  }

  private void animateAttack(Attack attack) {
  }

  private void animateReinforce(Reinforce reinforce) {
  }

  public static PlayerController getPlayerController() {
    return PLAYER_CONTROLLER;
  }

}

