package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;
import static com.unima.risk6.gui.configurations.StyleConfiguration.showErrorDialog;

import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.configurations.observers.GameLobbyObserver;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.ImageName;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.SinglePlayerSettingsScene;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

/**
 * The SinglePlayerSettingsSceneController handles interactions within the single player
 * settings scene.
 * It manages UI components, game lobby, bot addition/removal, and scene transitions.
 * Implements GameLobbyObserver to receive game lobby updates.
 *
 * @author fisommer
 * @author astoyano
 */

public class SinglePlayerSettingsSceneController implements GameLobbyObserver {

  private final SinglePlayerSettingsScene singlePlayerSettingsScene;
  private final SceneController sceneController;
  private BorderPane root;
  private UserDto myUser;
  private GameLobby gameLobby;
  private boolean tutorial;

  /**
   * Constructs a SinglePlayerSettingsSceneController object that handles user interactions
   * in the SinglePlayerSettingsScene.
   *
   * @param singlePlayerSettingsScene a reference to the single player settings scene
   */

  public SinglePlayerSettingsSceneController(SinglePlayerSettingsScene singlePlayerSettingsScene) {
    this.singlePlayerSettingsScene = singlePlayerSettingsScene;
    this.sceneController = SceneConfiguration.getSceneController();
    LobbyConfiguration.addGameLobbyObserver(this);
  }

  /**
   * Initializes the single player settings scene, sets up the game lobby, user, and
   * the UI elements.
   */

  public void init() {
    this.gameLobby = LobbyConfiguration.getGameLobby();
    this.myUser = GameConfiguration.getMyGameUser();
    this.root = (BorderPane) singlePlayerSettingsScene.getRoot();
    this.tutorial = singlePlayerSettingsScene.isTutorial();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/segoe_ui_bold.ttf"),
        26);
    // Initialize elements
    initHbox();
    initElements(tutorial ? "Tutorial" : "Singleplayer");
  }

  private void initElements(String lobbyName) {
    Path arrow = generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> handleQuitGameLobby());

    // Initialize the username TextField
    Label title = new Label(lobbyName);
    title.setAlignment(Pos.CENTER);
    title.setStyle(
        "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 46px; "
            + "-fx-text-fill: white");

    HBox titleBox = new HBox(title);
    titleBox.setAlignment(Pos.CENTER);
    Button play = new Button("Play");
    applyButtonStyle(play);
    play.setPrefWidth(470);
    play.setPrefHeight(40);
    play.setAlignment(Pos.CENTER);
    play.setFont(new Font(18));

    HBox playButton = new HBox(play);
    playButton.setAlignment(Pos.CENTER);
    play.setOnMouseClicked(e -> handlePlayButton());
    root.setBottom(playButton);
    BorderPane.setMargin(playButton, new Insets(10, 20, 20, 10));

    // Load the image into an ImageView
    Image originalImage = ImageConfiguration.getImageByName(ImageName.SINGLEPLAYER_BACKGROUND);
    ImageView imageView = new ImageView(originalImage);

    // Set the opacity
    imageView.setOpacity(0.9);

    // Create a snapshot of the ImageView
    SnapshotParameters parameters = new SnapshotParameters();
    parameters.setFill(Color.TRANSPARENT);
    Image semiTransparentImage = imageView.snapshot(parameters, null);

    // Use the semi-transparent image for the background
    BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true,
        true, true);
    BackgroundImage backgroundImage = new BackgroundImage(semiTransparentImage,
        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
        backgroundSize);
    Background background = new Background(backgroundImage);
    root.setBackground(background);
    root.setTop(titleBox);
    root.setLeft(backButton);

    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));

    BorderPane.setMargin(titleBox, new Insets(10, 20, 20, 10));
  }

  private void handleQuitGameLobby() {
    if (StyleConfiguration.showConfirmationDialog("Leave Lobby",
        "Are you sure that you want to leave the Lobby?")) {
      LobbyConfiguration.stopGameClient();
      NetworkConfiguration.stopGameServer();
      sceneController.activate(SceneName.TITLE);
    }
  }

  private void initHbox() {
    HBox centralhbox = new HBox();
    VBox userVbox = createPlayerVbox(myUser);
    centralhbox.getChildren().add(userVbox);
    if (tutorial) {
      for (String s : gameLobby.getBots()) {
        VBox botVbox = createBotVbox(3, s);
        centralhbox.getChildren().add(botVbox);
      }
    } else {
      for (String bot : gameLobby.getBots()) {
        int i = 0;
        if (bot.contains("Medium")) {
          i = 1;
        }
        if (bot.contains("Hard")) {
          i = 2;
        }
        VBox botVbox = createBotVbox(i, bot);
        centralhbox.getChildren().add(botVbox);
      }
    }
    if (gameLobby.getBots().size() + gameLobby.getUsers().size() < gameLobby.getMaxPlayers()) {
      StackPane plus = createPlusStackpane();
      centralhbox.getChildren().add(plus);
    }
    centralhbox.setAlignment(Pos.CENTER);
    centralhbox.setSpacing(20.0);
    root.setCenter(centralhbox);
  }

  private VBox createPlayerVbox(UserDto userDto) {
    StackPane userImage = createPlayerStackPane(ImageName.PLAYER_ICON, false);
    Label userName = new Label(userDto.getUsername());
    userName.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 20px; "
        + "-fx-font-weight: bold; -fx-text-fill: #2D2D2D;"
        + "-fx-background-color: #CCCCCC; -fx-border-color: #000000; -fx-border-radius: 20; "
        + "-fx-background-radius: 20; -fx-padding: 5 10 5 10; -fx-border-width: 2.0");
    VBox playerBox = new VBox(userImage, userName);
    playerBox.setAlignment(Pos.CENTER);
    playerBox.setSpacing(-10);

    Button removeButton = new Button("");
    removeButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent");

    VBox removeBox = new VBox(removeButton, playerBox);
    removeBox.setAlignment(Pos.CENTER);
    removeBox.setSpacing(15);

    return removeBox;
  }

  private StackPane createPlayerStackPane(ImageName imageName, boolean isBot) {
    Circle circle = new Circle();
    ImageView userImage = new ImageView(ImageConfiguration.getImageByName(imageName));
    if (isBot) {
      userImage.setFitHeight(130);
      userImage.setFitWidth(130);
      circle.setRadius(65);
    } else {
      userImage.setFitHeight(140);
      userImage.setFitWidth(140);
      circle.setRadius(70);
    }

    circle.setStroke(Color.BLACK);
    circle.setFill(Color.LIGHTGRAY);
    circle.setStrokeWidth(2.0);

    // create a clip for the user image
    Circle clip = new Circle(userImage.getFitWidth() / 2, userImage.getFitHeight() / 2,
        circle.getRadius());

    // apply the clip to the user image
    userImage.setClip(clip);

    // create a stack pane to place the circle and image on top of each other
    StackPane userStackPane = new StackPane();
    userStackPane.getChildren().addAll(circle, userImage);

    return userStackPane;
  }

  private StackPane createPlusStackpane() {
    ImageView plusImage = new ImageView(ImageConfiguration.getImageByName(ImageName.PLUS_ICON));
    plusImage.setFitHeight(20);
    plusImage.setFitWidth(20);
    Circle circle = new Circle();
    circle.setRadius(20);
    circle.setStroke(Color.BLACK);
    circle.setFill(Color.LIGHTGRAY);
    circle.setStrokeWidth(2.0);

    Circle clip = new Circle(plusImage.getFitWidth() / 2, plusImage.getFitHeight() / 2,
        circle.getRadius());

    plusImage.setClip(clip);

    StackPane plusStackPane = new StackPane();
    plusStackPane.getChildren().addAll(circle, plusImage);
    plusStackPane.setOnMouseClicked(e -> handlePlusButton());
    return plusStackPane;
  }

  private void handlePlusButton() {
    if (gameLobby.getUsers().size() + gameLobby.getBots().size() < gameLobby.getMaxPlayers()) {
      botAdded();
    } else {
      showErrorDialog("Maximum number of players reached",
          "You can not add more players to this game lobby.");
    }
  }

  private void botAdded() {
    List<String> choices = new ArrayList<>();
    choices.add("Easy");
    choices.add("Medium");
    choices.add("Hard");

    ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Easy", choices);
    choiceDialog.setTitle("Choice");
    choiceDialog.setHeaderText("Please choose difficulty level");
    choiceDialog.setContentText("Difficulties:");

    Optional<String> result = choiceDialog.showAndWait();

    result.ifPresent(selectedOption -> {
      int difficulty = switch (result.get()) {
        case "Medium" -> {
          MediumBot mediumBot = new MediumBot();
          gameLobby.getBots().add(mediumBot.getUser());
          yield 1;
        }
        case "Hard" -> {
          HardBot hardBot = new HardBot();
          gameLobby.getBots().add(hardBot.getUser());
          yield 2;
        }
        default -> {
          EasyBot easyBot = new EasyBot();
          gameLobby.getBots().add(easyBot.getUser());
          yield 0;
        }
      };
      LobbyConfiguration.sendBotJoinLobby(gameLobby);
    });
  }

  private VBox createBotVbox(int difficultyNumber, String botName) {
    StackPane botImage = new StackPane();
    switch (difficultyNumber) {
      case 0 -> botImage = createPlayerStackPane(ImageName.EASYBOT_ICON, true);
      case 1 -> botImage = createPlayerStackPane(ImageName.MEDIUMBOT_ICON, true);
      case 2 -> botImage = createPlayerStackPane(ImageName.HARDBOT_ICON, true);
      case 3 -> botImage = createPlayerStackPane(ImageName.TUTORIAL_ICON, true);
      default -> botImage = createPlayerStackPane(ImageName.EASYBOT_ICON, true);
    }
    Label userName = new Label(botName);
    userName.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 20px; "
        + "-fx-font-weight: bold; -fx-text-fill: #2D2D2D;"
        + "-fx-background-color: #CCCCCC; -fx-border-color: #000000; -fx-border-radius: 20; "
        + "-fx-background-radius: 20; -fx-padding: 5 10 5 10; -fx-border-width: 2.0");

    VBox botBox = new VBox(botImage, userName);
    botBox.setAlignment(Pos.CENTER);
    botBox.setSpacing(-10);

    VBox removeBox = new VBox();
    if (tutorial) {
      Button removeButton = new Button("");
      removeButton.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-font-size: 16; "
          + "-fx-background-color: transparent; -fx-border-color: transparent;");
      removeBox = new VBox(removeButton, botBox);
    } else {
      Button removeButton = new Button("Remove");
      removeButton.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-font-size: 16; "
          + "-fx-background-color: lightgrey; -fx-border-color: black;");
      removeButton.setOnMouseClicked(e -> removeBot(botName));
      removeBox = new VBox(removeButton, botBox);
    }
    removeBox.setAlignment(Pos.CENTER);
    removeBox.setSpacing(10);
    return removeBox;
  }

  public void removeBot(String bot) {
    gameLobby.getBots().remove(bot);
    LobbyConfiguration.sendRemoveBotFromLobby(gameLobby);
  }

  /**
   * Handles the action of the "Play" button. Validates the number of players and
   * initiates the game or tutorial.
   */

  private void handlePlayButton() {
    int usersSize = gameLobby.getUsers().size();
    int together = usersSize + gameLobby.getBots().size();
    if (together < 2 || together > gameLobby.getMaxPlayers()) {
      showErrorDialog("Not enough players",
          "You need at least 2 players to start the game.");
      return;
    }
    if (tutorial) {
      LobbyConfiguration.sendStartTutorial(gameLobby);
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      SceneConfiguration.startGame();
    } else {
      LobbyConfiguration.sendStartGame(gameLobby);
      try {
        Thread.sleep(300);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

  }

  public void setTutorial(boolean tutorial) {
    this.tutorial = tutorial;
  }

  /**
   * Updates the current game lobby and re-initializes the scene.
   *
   * @param gameLobby the updated game lobby
   */

  @Override
  public void updateGameLobby(GameLobby gameLobby) {
    this.gameLobby = gameLobby;
    Platform.runLater(this::init);
  }
}