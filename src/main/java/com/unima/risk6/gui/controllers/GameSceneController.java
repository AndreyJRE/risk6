package com.unima.risk6.gui.controllers;

import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.ai.tutorial.Tutorial;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.configurations.observers.ChatObserver;
import com.unima.risk6.game.configurations.observers.GameStateObserver;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Move;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.Statistic;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.game.models.enums.PlayerColor;
import com.unima.risk6.gui.configurations.CountriesUiConfiguration;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SoundConfiguration;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.ImageName;
import com.unima.risk6.gui.scenes.GameScene;
import com.unima.risk6.gui.uimodels.ActivePlayerUi;
import com.unima.risk6.gui.uimodels.ChatUi;
import com.unima.risk6.gui.uimodels.CountryUi;
import com.unima.risk6.gui.uimodels.DiceUi;
import com.unima.risk6.gui.uimodels.HandUi;
import com.unima.risk6.gui.uimodels.PlayerUi;
import com.unima.risk6.gui.uimodels.SettingsUi;
import com.unima.risk6.gui.uimodels.TroopsCounterUi;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
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

/**
 * The GameSceneController class controls the game scene and handles various user interactions and
 * events. It gets created by the game scene and then starts the initialization of the game scene,
 * including player UI, country UI, chat UI, and bottom pane UI. It is also responsible for updating
 * and adapting the game scene to represent the different phases of the board game Risk.
 *
 * @author astoyano
 * @author mmeider
 */

public class GameSceneController implements GameStateObserver, ChatObserver {

  private static PlayerController playerController;
  private static PlayerUi myPlayerUi;
  private final GameScene gameScene;
  private GameState gameState;
  private BorderPane root;
  private Set<CountryUi> countriesUis;
  private Group countriesGroup;
  private LinkedList<PlayerUi> playerUis;

  private ActivePlayerUi activePlayerUi;
  private Button nextPhaseButton;
  private Popup statisticPopup;
  boolean isStatisticsShowing = false;

  boolean isCountryNameShowing = false;

  private ChatUi chatUi;
  private HandUi handUi;
  private Button cardsButton;
  private Tutorial tutorial;
  private Label chatCounterLabel;
  Group countryNameGroup;
  private int tutorialMessageCounter = 0;
  private SettingsUi settingsUi;

  /**
   * Initializes the GameSceneController with the provided GameScene object.
   *
   * @param gameScene the GameScene object associated with the controller
   */
  public GameSceneController(GameScene gameScene) {
    this.gameScene = gameScene;
  }


  /**
   * Initializes the game scene by calling initializeGameScene() and event filters. It also prepares
   * the chatUi and handUi. the tutorial will get initialized if that is active.
   */
  public void init() {
    playerController = new PlayerController();
    tutorial = GameConfiguration.getTutorial();
    if (tutorial != null) {
      this.gameState = tutorial.getTutorialState();
    } else {
      this.gameState = GameConfiguration.getGameState();
    }
    this.countriesUis = CountriesUiConfiguration.getCountriesUis();
    this.root = (BorderPane) gameScene.getRoot();
    this.countriesGroup = new Group();
    this.initializeGameScene();
    this.chatUi = new ChatUi(gameScene);
    this.handUi = new HandUi(gameScene, countriesUis);
    Hand hand = gameState.getActivePlayers().stream()
        .filter(player -> player.equals(myPlayerUi.getPlayer())).findFirst().get().getHand();
    this.handUi.setHand(hand);
    GameConfiguration.addObserver(this);
    LobbyConfiguration.addChatObserver(this);
    addListeners();
    if (tutorial != null) {
      initTutorial();
    }


  }


  /**
   * Initializes the game scene by setting up the background, player pane, countries pane, bottom
   * pane.
   */
  private void initializeGameScene() {
    Image waterImage = ImageConfiguration.getImageByName(ImageName.WATER_GIF);
    BackgroundImage backgroundImage = new BackgroundImage(waterImage, BackgroundRepeat.REPEAT,
        BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

    root.setBackground(new Background(backgroundImage));
    StackPane playerPane = initializePlayersPane();
    root.setLeft(playerPane);
    StackPane countriesPane = initializeCountriesPane();
    root.setCenter(countriesPane);
    StackPane bottomPane = initializeBottomPane();
    root.setBottom(bottomPane);
    settingsUi = new SettingsUi(gameScene);

  }

  /**
   * Displays an order popup showing the order of players' dice rolls. It includes dice UIs for each
   * player and a label indicating the player's position. Afterward, the popup is hidden and the
   * player pane is updated.
   */
  public void showOrderPopup() {
    BorderPane orderPane = new BorderPane();
    Popup orderPopup = new Popup();
    orderPopup.getContent().add(orderPane);
    Image image = ImageConfiguration.getImageByName(ImageName.DICE_PREVIEW);

    int numImages = gameState.getActivePlayers().size() - 1;

    Group otherDiceGroup = new Group();

    HashMap<String, Integer> hashMapOfPlayerDice = GameConfiguration.getDiceRolls();
    int myValue = hashMapOfPlayerDice.get(myPlayerUi.getPlayer().getUser());

    List<DiceUi> diceUis = new ArrayList<>();

    gameState.getActivePlayers().forEach(x -> {
      if (!Objects.equals(x.getUser(), myPlayerUi.getPlayer().getUser())) {
        diceUis.add(new DiceUi(false, hashMapOfPlayerDice.get(x.getUser())));
      }
    });
    if (diceUis.size() >= 2) {
      Arc semicircle = new Arc();
      semicircle.setCenterX(0);
      semicircle.setCenterY(0);
      semicircle.setRadiusX(300);
      semicircle.setRadiusY(150);
      semicircle.setStartAngle(0);
      semicircle.setLength(-180);
      double imageWidth = image.getWidth();
      double imageHeight = image.getHeight();
      for (int i = 0; i < diceUis.size(); i++) {
        double angle = Math.toRadians(
            semicircle.getStartAngle() + i * semicircle.getLength() / (numImages));
        double x =
            semicircle.getCenterX() + semicircle.getRadiusX() * Math.cos(angle) - imageWidth / 2;
        double y =
            semicircle.getCenterY() + semicircle.getRadiusY() * Math.sin(angle) - imageHeight / 2;
        DiceUi currentDice = diceUis.get(i);
        currentDice.setLayoutX(x);
        currentDice.setLayoutY(y);
        otherDiceGroup.getChildren().add(currentDice);
        orderPane.setCenter(otherDiceGroup);
      }
    } else {
      otherDiceGroup.getChildren().add(diceUis.get(0));
      orderPane.setTop(otherDiceGroup);
      BorderPane.setAlignment(otherDiceGroup, Pos.CENTER);
    }

    DiceUi myDice = new DiceUi(false, myValue);

    diceUis.add(myDice);

    PauseTransition delayTransitionHidePopup = new PauseTransition(Duration.millis(1000));
    delayTransitionHidePopup.setOnFinished(delayTransitionEvent -> {
      orderPopup.hide();
      StackPane stackPane = initializePlayersPane();
      activePlayerUi.changeActivePlayerUi(getCurrentPlayerUi());
      nextPhaseButton.setVisible(
          checkIfCurrentPlayerIsMe() && (myPlayerUi.getPlayer().getCurrentPhase()
              != GamePhase.NOT_ACTIVE) && (myPlayerUi.getPlayer().getCurrentPhase()
              != GamePhase.CLAIM_PHASE));
      root.setLeft(stackPane);
    });

    PauseTransition delayTransitionShowOrder = new PauseTransition(Duration.millis(3000));
    delayTransitionShowOrder.setOnFinished(delayTransitionEvent -> {
      orderPane.getChildren().clear();
      int i = 1;
      for (Player player : gameState.getActivePlayers()) {
        if (player.equals(myPlayerUi.getPlayer())) {
          break;
        }
        i++;
      }
      Label orderLabel = new Label("You are in position " + i + ".\n" + "Good Luck!");
      orderLabel.setStyle("-fx-font-size: 29px;");

      HBox orderBox = new HBox();
      orderBox.setAlignment(Pos.CENTER);
      orderBox.getChildren().add(orderLabel);
      orderPane.setCenter(orderBox);
      delayTransitionHidePopup.play();

    });
    for (DiceUi dice : diceUis) {
      dice.rollDice();
    }
    delayTransitionShowOrder.play();

    VBox myDiceBox = new VBox();
    myDiceBox.getChildren().addAll(myDice);
    myDiceBox.setSpacing(10);

    HBox diceHbox = new HBox(myDiceBox);
    diceHbox.setAlignment(Pos.CENTER);

    orderPane.setBottom(diceHbox);
    BorderPane.setMargin(diceHbox, new Insets(10));

    orderPane.setStyle("-fx-background-radius: 10;-fx-background-color: rgba(255, 255, 255, 0.7);");

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

  /**
   * Initializes the countries pane by adding country UI elements, troops counters, and country
   * names. It also handles the placement of troops counters and country names.
   *
   * @return a StackPane containing the countries pane
   */
  private StackPane initializeCountriesPane() {

    countryNameGroup = new Group();

    countriesGroup.getChildren().addAll(countriesUis);

    for (CountryUi countryUi : countriesUis) {
      Bounds bounds = countryUi.getCountryPath().getBoundsInParent();
      double ellipseX = bounds.getMinX() + bounds.getWidth() * 0.5;
      double ellipseY = bounds.getMinY() + bounds.getHeight() * 0.5;
      countryUi.initMouseListener();

      Point2D correctedCoordinates = CountryUi.correctEllipsePlacement(countryUi.getCountry(),
          ellipseX, ellipseY);
      ellipseX = correctedCoordinates.getX();
      ellipseY = correctedCoordinates.getY();

      Label countryName = new Label(
          countryUi.getCountry().getCountryName().name().replaceAll("_", " "));

      double finalEllipseY;
      double finalEllipseX;

      switch (countryUi.getCountry().getCountryName().name()) {
        case "SOUTHERN_EUROPE", "SOUTH_AFRICA", "EAST_AFRICA", "ALASKA", "QUEBEC" -> {
          finalEllipseY = ellipseY - 5;
          finalEllipseX = ellipseX;
        }
        case "WESTERN_UNITED_STATES" -> {
          finalEllipseY = ellipseY - 3;
          finalEllipseX = ellipseX;
        }
        case "MADAGASCAR" -> {
          finalEllipseY = ellipseY + 12;
          finalEllipseX = ellipseX;
        }
        case "WESTERN_EUROPE" -> {
          finalEllipseY = ellipseY + 10;
          finalEllipseX = ellipseX;
        }
        case "EASTERN_UNITED_STATES" -> {
          finalEllipseY = ellipseY + 5;
          finalEllipseX = ellipseX;
        }
        case "WESTERN_AUSTRALIA" -> {
          finalEllipseY = ellipseY - 40;
          finalEllipseX = ellipseX - 20;
        }
        case "EASTERN_AUSTRALIA" -> {
          finalEllipseY = ellipseY + 10;
          finalEllipseX = ellipseX - 50;
        }
        default -> {
          finalEllipseY = ellipseY;
          finalEllipseX = ellipseX;
        }
      }

      Platform.runLater(() -> {
        countryName.setStyle(
            "-fx-background-radius: 15px;-fx-background-color: rgba(0,0,0,0.50);-fx-font-weight: "
                + "bold;-fx-font-size: 12px; -fx-padding: 2px;");
        countryName.setTextFill(Color.WHITE);
        countryName.setLayoutX(finalEllipseX - (countryName.getLayoutBounds().getWidth() / 2));
        countryName.setLayoutY(finalEllipseY + 10);
      });

      countryNameGroup.getChildren().add(countryName);
      TroopsCounterUi troopsCounterUi = new TroopsCounterUi(ellipseX, ellipseY);
      troopsCounterUi.setText(countryUi.getCountry().getTroops().toString());
      countryUi.setTroopsCounterUi(troopsCounterUi);
      countriesGroup.getChildren().add(troopsCounterUi);

    }
    countryNameGroup.setVisible(false);
    countriesGroup.getChildren().add(countryNameGroup);

    StackPane countriesPane = new StackPane();
    countriesPane.getChildren().add(countriesGroup);
    return countriesPane;
  }


  /**
   * Initializes the players pane by adding player UI elements and setting up their colors and
   * sizes. It also sets the active player UI and initializes the player controller for the user's
   * player.
   *
   * @return a StackPane containing the players pane
   */
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
        playerController.setPlayer(player);
        playerController.getHandController().setHand(player.getHand());
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


  /**
   * Initializes the bottom pane by adding chat button, chat counter label, active player UI, next
   * phase button, and cards button. It also handles their visibility and event handling.
   *
   * @return a StackPane containing the bottom pane
   */
  private StackPane initializeBottomPane() {
    Button chatButton = new Button();
    ImageView chatIcon = new ImageView(
        new Image(getClass().getResource("/com/unima/risk6/pictures/chatIcon.png").toString()));
    chatIcon.setFitWidth(40);
    chatIcon.setFitHeight(40);
    chatButton.setGraphic(chatIcon);
    chatButton.setStyle("-fx-background-radius: 15px;");
    chatButton.setFocusTraversable(false);
    chatButton.setOnAction(event -> {
      chatCounterLabel.setVisible(false);
      chatUi.show();
    });
    chatCounterLabel = new Label("!");
    chatCounterLabel.setStyle("-fx-background-radius: 12px;"
        + "-fx-background-color: rgba(255,165,0,0.71); -fx-font-size: 22px; -fx-padding: 3px");
    chatCounterLabel.setTextFill(Color.WHITE);
    chatButton.setVisible(gameState.isChatEnabled());
    chatCounterLabel.setVisible(false);
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
    nextPhaseButton.setVisible(false);
    nextPhaseButton.setOnAction(event -> {
      Player currentPlayer = myPlayerUi.getPlayer();
      GamePhase currentPhase = currentPlayer.getCurrentPhase();
      if ((currentPlayer.getDeployableTroops() > 0 || playerController.getHandController()
          .mustExchange()) && currentPlayer.getCurrentPhase()
          .equals(GamePhase.REINFORCEMENT_PHASE)) {
        StyleConfiguration.showErrorDialog("Can't end phase yet!",
            "You either still have troops to deploy or must exchange cards in your hand. "
                + "Deploy them all before ending the phase.");
      } else {
        playerController.sendEndPhase(currentPhase);
      }
      for (CountryUi countryUi : countriesUis) {
        countryUi.removeArrowsAndAdjacentCountries();
      }
    });

    cardsButton = new Button();
    ImageView cardsGroup = new ImageView(
        new Image(getClass().getResource("/com/unima/risk6/pictures/cardsGroup.png").toString()));
    cardsGroup.setFitWidth(150);
    cardsGroup.setFitHeight(80);
    cardsButton.setGraphic(cardsGroup);
    cardsButton.setStyle("-fx-background-radius: 15px;");
    cardsButton.setFocusTraversable(false);
    if (tutorial != null) {
      cardsButton.setVisible(false);
    }
    cardsButton.setOnAction(event -> showCardsPopup());

    StackPane bottomPane = new StackPane();
    bottomPane.getChildren()
        .addAll(cardsButton, activePlayerUi, nextPhaseButton, chatButton, chatCounterLabel);
    bottomPane.setAlignment(Pos.CENTER);
    StackPane.setMargin(nextPhaseButton, new Insets(0, 0, 5, 525));
    StackPane.setAlignment(cardsButton, Pos.CENTER_LEFT);
    StackPane.setMargin(cardsButton, new Insets(0, 0, 0, 15));
    StackPane.setAlignment(chatButton, Pos.CENTER_RIGHT);
    StackPane.setMargin(chatButton, new Insets(0, 15, 0, 0));
    StackPane.setAlignment(chatCounterLabel, Pos.CENTER_RIGHT);
    StackPane.setMargin(chatCounterLabel, new Insets(0, 10, 40, 0));
    bottomPane.setPadding(new Insets(0, 0, 15, 0));
    return bottomPane;
  }


  /**
   * Updates the chat display with the given list of messages. This method is run on the JavaFX
   * application thread using Platform.runLater() to ensure thread safety.
   *
   * @param messages the list of messages to update the chat with
   */
  @Override
  public void updateChat(List<String> messages) {
    Platform.runLater(() -> {
      if (chatUi.getChatPopup().isShowing()) {
        chatCounterLabel.setVisible(false);
      } else {
        chatCounterLabel.setVisible(true);
        SoundConfiguration.playNotificationSound();
      }
    });

  }

  private void showCardsPopup() {
    handUi.show();
  }


  /**
   * Adds a close request listener to the game scene. When the user tries to close the game scene's
   * window, a confirmation dialog is shown, and the behavior is handled based on the user's
   * response.
   */
  public void addCloseRequestListener() {
    SceneController sceneController = SceneConfiguration.getSceneController();
    gameScene.getWindow().setOnCloseRequest(e -> {
      e.consume();
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("Warning: Exiting Game");
      alert.setHeaderText("Are you sure you want to to leave the game?");
      alert.setContentText(
          "If you leave, you cannot rejoin the game! Your place will be replaced " + "by a bot.");
      ButtonType buttonYes = new ButtonType("Yes, exit game");
      ButtonType buttonNo = new ButtonType("No, continue playing");
      alert.getButtonTypes().setAll(buttonYes, buttonNo);
      alert.showAndWait().ifPresent(buttonType -> {
        if (buttonType == buttonYes) {
          if (GameConfiguration.getCurrentGameStatistic() != null) {
            Statistic statistic = playerController.getPlayer().getStatistic();
            GameConfiguration.updateGameStatistic(false, statistic.getTroopsLost(),
                statistic.getCountriesWon(), statistic.getTroopsGained(),
                statistic.getCountriesLost());
          }
          sceneController.close();
        }
        if (buttonType == buttonNo) {
          alert.close();
        }
      });
    });
  }


  /**
   * Adds listeners to the game scene for various events such as window resizing, key presses, and
   * releases.
   */
  private void addListeners() {
    gameScene.widthProperty().addListener((obs, oldVal, newVal) -> {
      double widthScale = newVal.doubleValue() / 1080;
      double heightScale = gameScene.getHeight() / 720;
      double scale = Math.min(widthScale, heightScale);
      countriesGroup.setScaleX(scale + 0.4);
      countriesGroup.setScaleY(scale + 0.4);
    });

    gameScene.heightProperty().addListener((obs, oldVal, newVal) -> {
      double widthScale = gameScene.getWidth() / 1080;
      double heightScale = newVal.doubleValue() / 720;
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

    gameScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      if (event.getCode() == KeyCode.M && !isCountryNameShowing) {
        countryNameGroup.setVisible(true);
        isCountryNameShowing = true;
        event.consume();
      }
    });
    gameScene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
      if (event.getCode() == KeyCode.M) {
        countryNameGroup.setVisible(false);
        isCountryNameShowing = false;
        event.consume();
      }
    });
    gameScene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ESCAPE) {
        settingsUi.show();
        keyEvent.consume();
      }
    });
  }

  /**
   * Displays a popup showing the statistics of each player in the game.
   */
  private void showStatisticsPopup() {
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
        userImage = new ImageView(ImageConfiguration.getImageByName(ImageName.EASYBOT_ICON));
      } else if (player instanceof MediumBot) {
        userImage = new ImageView(ImageConfiguration.getImageByName(ImageName.MEDIUMBOT_ICON));
      } else if (player instanceof HardBot) {
        userImage = new ImageView(ImageConfiguration.getImageByName(ImageName.HARDBOT_ICON));
      } else {
        userImage = new ImageView(ImageConfiguration.getImageByName(ImageName.PLAYER_ICON));
      }
      userCircle.setFill(new ImagePattern(userImage.getImage()));
      userBox.getChildren().addAll(userLabel, userCircle);
      statisticsGrid.add(userBox, i, 0);

      String[] attributeStrings = {"Troops: ", "Countries: ", "Continents: ", "Bonus Troops: "};
      for (int j = 0; j < attributeStrings.length; j++) {
        HBox statisticBox = new HBox();
        statisticBox.setPadding(new Insets(5));
        statisticBox.setAlignment(Pos.CENTER);
        int value = switch (j) {
          case 0 -> player.getStatistic().getNumberOfTroops();
          case 1 -> player.getStatistic().getNumberOfOwnedCountries();
          case 2 -> player.getContinents().size();
          case 3 -> player.getContinents().stream().mapToInt(Continent::getBonusTroops).sum();
          default -> throw new IllegalStateException("Unexpected value: " + j);
        };
        Label statisticName = new Label(attributeStrings[j]);
        statisticName.setStyle("-fx-font-size: 15px;");
        Label userStat = new Label(Integer.toString(value));
        userStat.setStyle("-fx-font-size: 15px; -fx-font-weight: bold");
        statisticBox.getChildren().addAll(statisticName, userStat);
        statisticsGrid.add(statisticBox, i, j + 1);
      }
    }
    BorderPane statisticPane = new BorderPane();
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

  /**
   * Updates the game state and performs animations based on the last moves in the game.
   *
   * @param gameState The updated game state.
   */
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
      if (playerController.getHandController().holdsExchangeable()) {
        cardsButton.setStyle("-fx-background-color: rgb(88,8,8); -fx-border-color: rgb(88,8,8);");
      } else {
        cardsButton.setStyle("-fx-background-radius: 15px;");
      }
    });
    if (gameState.isGameOver() && GameConfiguration.getCurrentGameStatistic() != null) {
      Statistic statistic = playerController.getPlayer().getStatistic();
      GameConfiguration.updateGameStatistic(
          gameState.getActivePlayers().contains(playerController.getPlayer()),
          statistic.getTroopsLost(), statistic.getCountriesWon(), statistic.getTroopsGained(),
          statistic.getCountriesLost());
      GameConfiguration.setCurrentGameStatistic(null);
    }
  }

  /**
   * Updates the troops count for the active player's country.
   */
  private void updateActivePlayerTroops() {
    if (activePlayerUi.getPlayerUi().getPlayer().getCurrentPhase()
        == GamePhase.REINFORCEMENT_PHASE) {
      activePlayerUi.updateActivePlayerTroops();
    }
  }

  /**
   * Updates the references to player UI objects based on the current game state.
   */
  private void updateReferencesFromGameState() {
    this.gameState.getActivePlayers().forEach(this::updatePlayerUiReferenceByPlayer);
    this.gameState.getLostPlayers().forEach(this::updatePlayerUiReferenceByPlayer);

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
  }

  /**
   * Updates the reference to the player UI object for a specific player.
   *
   * @param player The player for which to update the player UI reference.
   */
  private void updatePlayerUiReferenceByPlayer(Player player) {
    if (player.getUser().equals(GameConfiguration.getMyGameUser().getUsername())) {
      myPlayerUi.setPlayer(player);
      playerController.setPlayer(player);
      playerController.getHandController().setHand(player.getHand());
    } else {
      playerUis.forEach(playerUi -> {
        if (playerUi.getPlayer().getUser().equals(player.getUser())) {
          playerUi.setPlayer(player);
        }
      });
    }
  }


  /**
   * Animates the movement of troops during a fortification phase.
   *
   * @param fortify The fortification move to animate.
   */
  public void animateTroopsMovement(Fortify fortify) {
    double maxOffsetX = 3;
    double maxOffsetY = 3;
    Image image = ImageConfiguration.getImageByName(ImageName.INFANTRY_RUNNING_GIF);

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

  /**
   * Animates an attack between two countries.
   *
   * @param attack The attack move to animate.
   */
  private void animateAttack(Attack attack) {
    CountryUi countryUi = getCountryUiByCountry(attack.getAttackingCountry());
    CountryUi countryUi1 = getCountryUiByCountry(attack.getDefendingCountry());
    countryUi.updateAfterAttack(activePlayerUi, attack, countryUi, countryUi1);

  }

  /**
   * Animates the reinforcement of troops to a country.
   *
   * @param reinforce The reinforce move to animate.
   */
  private void animateReinforce(Reinforce reinforce) {
    CountryUi countryUi = getCountryUiByCountry(reinforce.getCountry());
    countryUi.update(activePlayerUi);

  }

  /**
   * Animates the end of a game phase.
   *
   * @param endPhase The end phase move to animate.
   */
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
    if (tutorial != null && checkIfCurrentPlayerIsMe()) {
      switch (activePlayerUi.getPlayerUi().getPlayer().getCurrentPhase()) {
        case CLAIM_PHASE -> {
          sendTutorialMessage();
          tutorial.updatePlayerClaim();
          Reinforce reinforce = tutorial.getCurrentClaim();
          if (reinforce != null) {
            CountryUi countryUi = getCountryUiByCountry(reinforce.getCountry());
            countryUi.animateTutorialCountry();
          }
        }
        case REINFORCEMENT_PHASE -> {
          sendTutorialMessage();
          Reinforce reinforce = tutorial.getCurrentReinforce();
          if (tutorial.isHandInEnabled()) {
            cardsButton.setVisible(true);
            GameConfiguration.setTutorial(null);
            tutorial = null;
            GameConfiguration.setTutorialOver(true);
            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          }
          if (reinforce != null && GameConfiguration.getTutorial() != null) {
            CountryUi countryUi = getCountryUiByCountry(reinforce.getCountry());
            countryUi.animateTutorialCountry();
          }
        }
        case ATTACK_PHASE -> {
          sendTutorialMessage();
          Attack attack = tutorial.getCurrentAttack();
          if (attack != null) {
            CountryUi countryUi = getCountryUiByCountry(attack.getAttackingCountry());
            countryUi.animateTutorialCountry();
          }
        }
        case FORTIFY_PHASE -> {
          sendTutorialMessage();
          Fortify fortify = tutorial.getCurrentFortify();
          if (fortify != null) {
            CountryUi countryUi = getCountryUiByCountry(fortify.getOutgoing());
            countryUi.animateTutorialCountry();
          }
        }
        default -> {
        }

      }
    }
    nextPhaseButton.setVisible(
        checkIfCurrentPlayerIsMe() && (myPlayerUi.getPlayer().getCurrentPhase()
            != GamePhase.NOT_ACTIVE) && (myPlayerUi.getPlayer().getCurrentPhase()
            != GamePhase.CLAIM_PHASE));
  }

  /**
   * Sends the next tutorial message and performs necessary actions based on the current phase.
   */
  private void sendTutorialMessage() {
    if (tutorialMessageCounter == 0 || tutorialMessageCounter == 1 || tutorialMessageCounter == 4
        || tutorialMessageCounter == 9 || tutorialMessageCounter == 10
        || tutorialMessageCounter == 11 || tutorialMessageCounter == 12) {
      LobbyConfiguration.setLastChatMessage(tutorial.getNextMessage());
    }
    if (tutorialMessageCounter == 12) {
      showChooseTutorialDifficultyDialog();
    }
    tutorialMessageCounter++;
  }

  /**
   * Displays a dialog for the player to choose the difficulty for the rest of the tutorial.
   */
  private static void showChooseTutorialDifficultyDialog() {
    List<String> choices = new ArrayList<>();
    choices.add("Easy");
    choices.add("Medium");
    choices.add("Hard");

    ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Easy", choices);
    choiceDialog.setTitle("Choice");
    choiceDialog.setHeaderText("Choose a difficulty for the rest of the tutorial");
    choiceDialog.setContentText("Difficulties:");

    Optional<String> result = choiceDialog.showAndWait();
    GameConfiguration.setBotDifficulty(result.orElse(choiceDialog.getDefaultChoice()));
  }

  /**
   * Animates the hand-in of cards during the game.
   *
   * @param handIn The hand-in move to animate.
   */
  private void animateHandIn(HandIn handIn) {
    activePlayerUi.updateActivePlayerTroops();
  }

  /**
   * Checks if the current player is the local player.
   *
   * @return {@code true} if the current player is the local player, {@code false} otherwise.
   */
  public boolean checkIfCurrentPlayerIsMe() {
    return gameState.getCurrentPlayer().getUser()
        .equals(GameConfiguration.getMyGameUser().getUsername());
  }


  /**
   * Retrieves the {@link CountryUi} object associated with a given country.
   *
   * @param country The country to find the associated CountryUi object for.
   * @return The CountryUi object associated with the given country.
   */
  public CountryUi getCountryUiByCountry(Country country) {
    return countriesUis.stream().filter(countryUi -> countryUi.getCountry().equals(country))
        .findFirst().get();
  }

  public void updateActivePlayerUi() {
    activePlayerUi.changeActivePlayerUi(getCurrentPlayerUi());
  }


  /**
   * Retrieves the PlayerUi object of the current player.
   *
   * @return the PlayerUi object representing the current player
   */
  public PlayerUi getCurrentPlayerUi() {
    Player currentPlayer = gameState.getCurrentPlayer();
    return playerUis.stream().filter(playerUi -> currentPlayer.equals(playerUi.getPlayer()))
        .findFirst().get();
  }

  /**
   * Initializes the tutorial by updating the UI elements and displaying tutorial messages.
   */
  public void initTutorial() {
    for (CountryUi countryUi : countriesUis) {
      if (countryUi.getCountry().hasPlayer()) {
        Color colorByPlayer = getColorByPlayer(countryUi.getCountry().getPlayer());
        countryUi.getCountryPath().setFill(colorByPlayer);
        countryUi.setColor(colorByPlayer);
      }

    }
    tutorial.updatePlayerClaim();

    Reinforce reinforce = tutorial.getCurrentClaim();
    if (reinforce != null) {
      CountryUi countryUi = getCountryUiByCountry(reinforce.getCountry());
      countryUi.animateTutorialCountry();
    }
    try {
      Thread.sleep(300);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    sendTutorialMessage();
  }

  /**
   * Retrieves the color associated with a given player.
   *
   * @param player The player to find the associated color for.
   * @return The color associated with the given player.
   */
  public Color getColorByPlayer(Player player) {
    return playerUis.stream().filter(p -> p.getPlayer().equals(player)).findFirst()
        .orElse(null)
        .getPlayerColor();
  }

  public static PlayerUi getMyPlayerUi() {
    return myPlayerUi;
  }

  public static PlayerController getPlayerController() {
    return playerController;
  }
}

