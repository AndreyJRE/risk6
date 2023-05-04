package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.User;
import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.CountriesUiConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.GameScene;
import com.unima.risk6.gui.scenes.MultiplayerLobbyScene;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
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

public class MultiplayerLobbySceneController {

  private final MultiplayerLobbyScene multiplayerLobbyScene;
  private final SceneController sceneController;
  private User user;
  private BorderPane root;
  private HBox centralHBox;


  public MultiplayerLobbySceneController(MultiplayerLobbyScene multiplayerLobbyScene) {
    this.multiplayerLobbyScene = multiplayerLobbyScene;
    this.sceneController = SceneConfiguration.getSceneController();
  }

  public void init() {
    this.user = SessionManager.getUser();
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
    backButton.setOnMouseClicked(e -> sceneController.activate(SceneName.TITLE));

    // Initialize the username TextField
    Label title = new Label("Single Player Settings");
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

    root.setBottom(playButton);
    root.setTop(titleBox);
    root.setLeft(backButton);
    root.setCenter(centralHBox);

    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    BorderPane.setMargin(playButton, new Insets(10, 20, 20, 10));
    BorderPane.setMargin(titleBox, new Insets(10, 20, 20, 10));

  }

// Weitere Imports ...

  private void botAdded() {
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

  private void removeBot() {
    StackPane plus = (StackPane) centralHBox.getChildren()
        .remove(centralHBox.getChildren().size() - 1);
    centralHBox.getChildren().remove(centralHBox.getChildren().size() - 1);
    centralHBox.getChildren().add(plus);
  }

  private void initHBox() {
    VBox userVBox = createPlayerVBox(user);
    VBox botBox1 = createBotVBox(0);
    ImageView plusImage = new ImageView(
        new Image(getClass().getResource("/com/unima/risk6/pictures/plusIcon.png").toString()));
    plusImage.setFitHeight(20);
    plusImage.setFitWidth(20);
    Circle circle = new Circle();
    circle.setRadius(20);
    circle.setStroke(Color.BLACK);
    circle.setFill(Color.LIGHTGRAY);
    circle.setStrokeWidth(2.0);

    // create a clip for the user image
    Circle clip = new Circle(plusImage.getFitWidth() / 2, plusImage.getFitHeight() / 2,
        circle.getRadius());

    // apply the clip to the user image
    plusImage.setClip(clip);

    // create a stack pane to place the circle and image on top of each other
    StackPane plusStackPane = new StackPane();
    plusStackPane.getChildren().addAll(circle, plusImage);
    plusStackPane.setOnMouseClicked(e -> botAdded());

    centralHBox = new HBox(userVBox, botBox1, plusStackPane);
    centralHBox.setAlignment(Pos.CENTER);
    centralHBox.setSpacing(20.0);
  }


  private StackPane createPlayerStackPane(String imagePath, boolean bot) {
    Circle circle = new Circle();
    ImageView userImage = new ImageView(new Image(getClass().getResource(imagePath).toString()));
    if (bot) {
      userImage.setFitHeight(130);
      userImage.setFitWidth(130);
      circle.setRadius(65);
    } else {
      userImage.setFitHeight(110);
      userImage.setFitWidth(110);
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

  private VBox createBotVBox(int difficultyNumber) {
    StackPane botImage = new StackPane();
    String difficulty = "";
    switch (difficultyNumber) {
      case 0:
        difficulty = "Easy Bot";
        botImage = createPlayerStackPane("/com/unima/risk6/pictures/easyBot.png", true);
        break;
      case 1:
        difficulty = "Medium Bot";
        botImage = createPlayerStackPane("/com/unima/risk6/pictures/mediumBot.png", true);
        break;
      case 2:
        difficulty = "Hard Bot";
        botImage = createPlayerStackPane("/com/unima/risk6/pictures/hardBot.png", true);
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

    removeButton.setOnMouseClicked(e -> removeBot());

    VBox removeBox = new VBox(removeButton, botBox);
    removeBox.setAlignment(Pos.CENTER);
    removeBox.setSpacing(10);

    return removeBox;
  }

  private VBox createPlayerVBox(User user) {
    StackPane userImage = createPlayerStackPane(user.getImagePath(), false);
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

  private void handlePlayButton() {
    // TODO: Implement the single player game

    List<String> users = new ArrayList<>();
    users.add(SessionManager.getUser().getUsername());
    users.add("Jake");
    users.add("Joel");
    users.add("John");
    List<AiBot> bots = new ArrayList<>();

    /*//TODO: Initiallize AIBots Bot
    while(centralHBox.getChildren().size() != 0){
      StackPane stackPane = (StackPane) centralHBox.getChildren().remove(centralHBox.getChildren().size()-1);
      Label label = (Label) stackPane.getChildren().remove(stackPane.getChildren().size());
    }*/

    GameState gameState = GameConfiguration.configureGame(users, bots);
    User myUser = SessionManager.getUser();
    GameConfiguration.setMyGameUser(UserDto.mapUserAndHisGameStatistics(myUser,
        DatabaseConfiguration.getGameStatisticService().getAllStatisticsByUserId(myUser.getId()))
    );
    GameConfiguration.setGameState(gameState);
    CountriesUiConfiguration.configureCountries(gameState.getCountries());
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
}
