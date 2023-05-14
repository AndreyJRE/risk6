package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;
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
import com.unima.risk6.gui.scenes.LobbyUserStatisticScene;
import com.unima.risk6.gui.scenes.MultiplayerLobbyScene;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

public class MultiplayerLobbySceneController implements GameLobbyObserver {

  private final MultiplayerLobbyScene multiplayerLobbyScene;
  private final SceneController sceneController;
  private UserDto myUser;
  private BorderPane root;
  private GameLobby gameLobby;

  private DropShadow dropShadow;


  public MultiplayerLobbySceneController(MultiplayerLobbyScene multiplayerLobbyScene) {
    this.multiplayerLobbyScene = multiplayerLobbyScene;
    this.sceneController = SceneConfiguration.getSceneController();
    LobbyConfiguration.addGameLobbyObserver(this);
  }

  public void init() {
    this.gameLobby = LobbyConfiguration.getGameLobby();
    this.myUser = GameConfiguration.getMyGameUser();
    this.root = (BorderPane) multiplayerLobbyScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/segoe_ui_bold.ttf"), 26);
    dropShadow = new DropShadow();
    dropShadow.setRadius(15.0);
    dropShadow.setColor(Color.LIGHTSKYBLUE.darker());
    initHBox();
    initElements();
  }

  private void initElements() {
    Path arrow = generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> handleQuitGameLobby());

    // Initialize the username TextField
    Label title = new Label("Multiplayer Lobby");
    title.setAlignment(Pos.CENTER);
    title.setStyle(
        "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 46px; -fx-text-fill: white");

    HBox titleBox = new HBox(title);
    titleBox.setAlignment(Pos.CENTER);
    if (checkIfUserIsOwner()) {
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
    } else {
      Label waiting = new Label("Waiting for the host to start the game...");
      waiting.setStyle(
          "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white");
      HBox waitingLabelBox = new HBox(waiting);
      waitingLabelBox.setAlignment(Pos.CENTER);
      root.setBottom(waitingLabelBox);
      BorderPane.setMargin(waitingLabelBox, new Insets(10, 20, 20, 10));

    }

    //TODO: Implement Gridpane for Multiplayer Settings

    // Load the image into an ImageView
    Image originalImage = ImageConfiguration.getImageByName(ImageName.MULTIPLAYER_BACKGROUND);
    ImageView imageView = new ImageView(originalImage);

// Set the opacity
    imageView.setOpacity(0.8);

// Create a snapshot of the ImageView
    SnapshotParameters parameters = new SnapshotParameters();
    parameters.setFill(Color.TRANSPARENT);
    Image semiTransparentImage = imageView.snapshot(parameters, null);

// Use the semi-transparent image for the background
    BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, true);
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

  private boolean checkIfUserIsOwner() {
    return myUser.getUsername().equals(gameLobby.getLobbyOwner().getUsername());
  }

  private void handleQuitGameLobby() {
    if (StyleConfiguration.showConfirmationDialog("Leave Lobby",
        "Are you sure that you want to leave the Lobby?")) {
      LobbyConfiguration.sendQuitGameLobby(GameConfiguration.getMyGameUser());
      sceneController.activate(SceneName.SELECT_LOBBY);
    }
  }

  private void initHBox() {
    HBox centralHBox = new HBox();
    List<UserDto> users = gameLobby.getUsers();
    for (UserDto user : users) {
      VBox userVBox = createPlayerVBox(user);
      centralHBox.getChildren().add(userVBox);
    }
    for (String bot : gameLobby.getBots()) {
      int i = 0;
      if (bot.contains("Medium")) {
        i = 1;
      }
      if (bot.contains("Hard")) {
        i = 2;
      }
      VBox botVBox = createBotVBox(i, bot);
      centralHBox.getChildren().add(botVBox);
    }
    if (gameLobby.getBots().size() + gameLobby.getUsers().size() < gameLobby.getMaxPlayers()) {
      StackPane plus = createPlusStackpane();
      if (checkIfUserIsOwner()) {
        centralHBox.getChildren().add(plus);
      }

    }
    centralHBox.setAlignment(Pos.CENTER);
    centralHBox.setSpacing(20.0);
    root.setCenter(centralHBox);

  }

  private void userClicked(UserDto user) {
    LobbyUserStatisticScene scene = (LobbyUserStatisticScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.LOBBY_USER_STATISTIC);
    if (scene == null) {
      scene = new LobbyUserStatisticScene();
      LobbyUserStatisticSceneController lobbyUserStatisticSceneController = new LobbyUserStatisticSceneController(
          scene);
      scene.setController(lobbyUserStatisticSceneController);
      sceneController.addScene(SceneName.LOBBY_USER_STATISTIC, scene);
    }
    pauseTitleSound();
    scene.setUserDto(user);
    sceneController.activate(SceneName.LOBBY_USER_STATISTIC);
  }


  private VBox createPlayerVBox(UserDto userDto) {
    StackPane userImage = createPlayerStackPane(ImageName.PLAYER_ICON, false);
    Label userName = new Label(userDto.getUsername());
    userName.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 20px; "
        + "-fx-font-weight: bold; -fx-text-fill: #2D2D2D;"
        + "-fx-background-color: #CCCCCC; -fx-border-color: #000000; -fx-border-radius: 20; "
        + "-fx-background-radius: 20; -fx-padding: 5 10 5 10; -fx-border-width: 2.0");
    VBox playerBox = new VBox(userImage, userName);
    userImage.hoverProperty().addListener((observableValue, aBoolean, t1) -> {
      if (t1) {
        userImage.setEffect(dropShadow);
        userImage.setCursor(Cursor.HAND);
      } else {
        userImage.setCursor(Cursor.DEFAULT);
        userImage.setEffect(null);
      }
    });
    playerBox.setOnMouseClicked(e -> userClicked(userDto));
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
    if (checkIfUserIsOwner()) {
      if (gameLobby.getUsers().size() + gameLobby.getBots().size() < gameLobby.getMaxPlayers()) {
        botAdded();
      } else {
        showErrorDialog("Maximum number of players reached",
            "You can not add more players to this game lobby.");
      }
    } else {
      showMessage();
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

  private VBox createBotVBox(int difficultyNumber, String botName) {
    StackPane botImage = new StackPane();
    switch (difficultyNumber) {
      case 0 -> botImage = createPlayerStackPane(ImageName.EASYBOT_ICON, true);
      case 1 -> botImage = createPlayerStackPane(ImageName.MEDIUMBOT_ICON, true);
      case 2 -> botImage = createPlayerStackPane(ImageName.HARDBOT_ICON, true);
    }
    Label userName = new Label(botName);
    userName.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 20px; "
        + "-fx-font-weight: bold; -fx-text-fill: #2D2D2D;"
        + "-fx-background-color: #CCCCCC; -fx-border-color: #000000; -fx-border-radius: 20; "
        + "-fx-background-radius: 20; -fx-padding: 5 10 5 10; -fx-border-width: 2.0");

    VBox botBox = new VBox(botImage, userName);
    botBox.setAlignment(Pos.CENTER);
    botBox.setSpacing(-10);

    Button removeButton = new Button("Remove");
    removeButton.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-font-size: 16; "
        + "-fx-background-color: lightgrey; -fx-border-color: black;");
    removeButton.setOnMouseClicked(e -> removeBot(botName));
    removeButton.setVisible(checkIfUserIsOwner());
    VBox removeBox = new VBox(removeButton, botBox);
    removeBox.setAlignment(Pos.CENTER);
    removeBox.setSpacing(10);
    return removeBox;
  }

  public void removeBot(String bot) {
    gameLobby.getBots().remove(bot);
    LobbyConfiguration.sendRemoveBotFromLobby(gameLobby);
  }

  private void showMessage() {
    showErrorDialog("No admin rights",
        "You can not add players or bots as you are not the admin of the Game Lobby.");
  }

  private void handlePlayButton() {
    int usersSize = gameLobby.getUsers().size();
    int together = usersSize + gameLobby.getBots().size();
    if (together < 2 || together > gameLobby.getMaxPlayers()) {
      showErrorDialog("Not enough players", "You need at least 2 players to start the game.");
      return;
    }
    LobbyConfiguration.sendStartGame(gameLobby);
  }

  @Override
  public void updateGameLobby(GameLobby gameLobby) {
    this.gameLobby = gameLobby;
    Platform.runLater(this::init);

  }

}
