package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.configurations.observers.GameLobbyObserver;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.CountriesUiConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.GameScene;
import com.unima.risk6.gui.scenes.MultiplayerLobbyScene;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
  private final List<AiBot> aiBots = new ArrayList<>();
  private UserDto user;
  private BorderPane root;
  private HBox centralHBox;
  private StackPane plus;
  private GameLobby gameLobby;


  public MultiplayerLobbySceneController(MultiplayerLobbyScene multiplayerLobbyScene) {
    this.multiplayerLobbyScene = multiplayerLobbyScene;
    this.sceneController = SceneConfiguration.getSceneController();
    LobbyConfiguration.addGameLobbyObserver(this);
  }

  public void init() {
    this.gameLobby = LobbyConfiguration.getGameLobby();
    this.user = GameConfiguration.getMyGameUser();
    this.root = (BorderPane) multiplayerLobbyScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/Fonts/Fonts/Segoe UI Bold.ttf"),
        26);
    // Initialize elements
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
    title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 46px;");

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

    //TODO: Implement Gridpane for Multiplayer Settings

    root.setBottom(playButton);
    root.setTop(titleBox);
    root.setLeft(backButton);
    root.setCenter(centralHBox);

    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    BorderPane.setMargin(playButton, new Insets(10, 20, 20, 10));
    BorderPane.setMargin(titleBox, new Insets(10, 20, 20, 10));

  }

  private void handleQuitGameLobby() {
    //TODO: Abfrage im PopUp einfügen
    Platform.runLater(
        () -> LobbyConfiguration.sendQuitGameLobby(GameConfiguration.getMyGameUser()));
    sceneController.activate(SceneName.SELECT_LOBBY);
  }

  private void initHBox() {
    List<UserDto> users = gameLobby.getUsers();
    for (UserDto user : users) {
      VBox userVBox = createPlayerVBox(user);
      centralHBox.getChildren().add(userVBox);
    }
    plus = createPlusStackpane();
    for (int i = gameLobby.getMaxPlayers() - users.size(); i > 0; i--) {
      centralHBox.getChildren().add(plus);
    }
    centralHBox.setAlignment(Pos.CENTER);
    centralHBox.setSpacing(20.0);
  }

  private VBox createPlayerVBox(UserDto user) {
    //TODO: Image Path für UserDto nachfragen
    StackPane userImage = createPlayerStackPane("/com/unima/risk6/pictures/playerIcon.png");
    Label userName = new Label(user.getUsername());
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

  private StackPane createPlayerStackPane(String imagePath) {
    Circle circle = new Circle();
    ImageView userImage = new ImageView(new Image(getClass().getResource(imagePath).toString()));
    userImage.setFitHeight(110);
    userImage.setFitWidth(110);
    circle.setRadius(70);

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
    ImageView plusImage = new ImageView(
        new Image(getClass().getResource("/com/unima/risk6/pictures/plusIcon.png").toString()));
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
    plusStackPane.setOnMouseClicked(e -> showMessage());
    return plusStackPane;
  }

  private void showMessage() {
    //TODO: Show Message to invite Friends
  }

  private void handlePlayButton() {
    // TODO: Implement the single player game

    List<String> users = new ArrayList<>();
    users.add(SessionManager.getUser().getUsername());
    users.add("Jake");
    users.add("Joel");
    users.add("John");

    Platform.runLater(() -> LobbyConfiguration.sendJoinServer(GameConfiguration.getMyGameUser()));

    /*GameState gameState = GameConfiguration.configureGame(users, aiBots);
    User myUser = SessionManager.getUser();
    GameConfiguration.setMyGameUser(UserDto.mapUserAndHisGameStatistics(myUser,
        DatabaseConfiguration.getGameStatisticService().getAllStatisticsByUserId(myUser.getId()))
    );
    GameConfiguration.setGameState(gameState);*/
    CountriesUiConfiguration.configureCountries(GameConfiguration.getGameState().getCountries());
    GameScene gameScene = (GameScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.GAME);
    if (gameScene == null) {
      gameScene = new GameScene();
      GameSceneController gameSceneController = new GameSceneController(gameScene);
      gameScene.setGameSceneController(gameSceneController);
      sceneController.addScene(SceneName.GAME, gameScene);
    }
    sceneController.activate(SceneName.GAME);
    //TODO If we want to go full screen we can use this
    sceneController.getStage().setFullScreen(true);
  }

  @Override
  public void updateGameLobby(GameLobby gameLobby) {
    this.gameLobby = gameLobby;
  }

/*  private void botAdded() {
    // Erstellen Sie eine Liste der Auswahlmöglichkeiten
    List<String> choices = new ArrayList<>();
    choices.add("Easy");
    choices.add("Medium");
    choices.add("Hard");

    // Erstellen Sie einen ChoiceDialog
    ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Easy", choices);
    choiceDialog.setTitle("Choice");
    choiceDialog.setHeaderText("Please choose difficulty level");
    choiceDialog.setContentText("Difficulties:");

    // Zeigen Sie den Dialog und speichern Sie das Ergebnis in einer Optional-Variable
    Optional<String> result = choiceDialog.showAndWait();

    // Überprüfen Sie das Ergebnis und führen Sie entsprechende Aktionen durch
    result.ifPresent(selectedOption -> {
      // Führen Sie Aktionen basierend auf der ausgewählten Option durch
      int difficulty = 0;
      switch (result.get()) {
        case "Easy":
          difficulty = 0;
          break;
        case "Medium":
          difficulty = 1;
          break;
        case "Hard":
          difficulty = 2;
          break;
      }
      VBox botBox = createBotVBox(difficulty);
      StackPane plus = (StackPane) centralHBox.getChildren()
          .remove(centralHBox.getChildren().size() - 1);
      centralHBox.getChildren().add(botBox);
      if (centralHBox.getChildren().size() < 6) {
        centralHBox.getChildren().add(plus);
      }
    });
  }

  private void removeBot(AiBot botToRemove) {
    // Find the index of the botToRemove in the aiBots list
    int botIndex = aiBots.indexOf(botToRemove);

    boolean full = (centralHBox.getChildren().size() == 6) && (!centralHBox.getChildren()
        .get(centralHBox.getChildren().size() - 1).equals(plus));
    System.out.println(full);

    // Remove the VBox containing the bot and the remove button from the centralHBox
    centralHBox.getChildren().remove(botIndex + 1); // Add 1 to account for the user VBox at index 0

    // Remove the botToRemove from the aiBots list
    aiBots.remove(botToRemove);

    // If the size of centralHBox is 5, ensure the plusStackPane is present
    if (full) {
      centralHBox.getChildren().add(plus);
    }
  }

  private VBox createBotVBox(int difficultyNumber) {
    StackPane botImage = new StackPane();
    String difficulty = "";
    switch (difficultyNumber) {
      case 0:
        botImage = createPlayerStackPane("/com/unima/risk6/pictures/easyBot.png", true);
        EasyBot easyBot = new EasyBot();
        aiBots.add(easyBot);
        difficulty = easyBot.getUser();
        break;
      case 1:
        botImage = createPlayerStackPane("/com/unima/risk6/pictures/mediumBot.png", true);
        MediumBot mediumBot = new MediumBot();
        aiBots.add(mediumBot);
        difficulty = mediumBot.getUser();
        break;
      case 2:
        botImage = createPlayerStackPane("/com/unima/risk6/pictures/hardBot.png", true);
        HardBot hardBot = new HardBot();
        aiBots.add(hardBot);
        difficulty = hardBot.getUser();
        break;
    }
    Label userName = new Label(difficulty);
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

    AiBot bot = aiBots.get(aiBots.size() - 1);

    removeButton.setOnMouseClicked(e -> removeBot(bot));

    VBox removeBox = new VBox(removeButton, botBox);
    removeBox.setAlignment(Pos.CENTER);
    removeBox.setSpacing(10);

    return removeBox;
  }*/
}
