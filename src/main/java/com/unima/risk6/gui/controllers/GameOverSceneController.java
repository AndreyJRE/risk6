package com.unima.risk6.gui.controllers;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.GameStatisticService;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Statistic;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.ImageName;
import com.unima.risk6.gui.scenes.GameOverScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;


public class GameOverSceneController {

  private final GameOverScene gameOverScene;
  private final SceneController sceneController;
  private final GameStatisticService gameStatisticService;
  private BorderPane root;
  private ImageView userImage;
  private StackPane userStackPane;
  private GridPane statisticsGridPane;
  private String numberStyle = "-fx-font-family: 'Segoe UI'; -fx-font-size: 26px; -fx-text-fill: white";
  private String labelStyle = "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 26px; -fx-text-fill: white";
  private Statistic statistic;
  private GameState gameState;


  public GameOverSceneController(GameOverScene gameOverScene, GameState gameState) {
    this.gameOverScene = gameOverScene;
    this.sceneController = SceneConfiguration.getSceneController();
    gameStatisticService = DatabaseConfiguration.getGameStatisticService();
    this.statistic = gameState.getCurrentPlayer().getStatistic();
    this.gameState = gameState;
  }

  public void init() {
    this.root = (BorderPane) gameOverScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/Segoe UI Bold.ttf"),
        26);
    // Initialize elements
    initUserStackPane();
    initGridPane();
    initElements();
    // Load the image into an ImageView
    Image originalImage = ImageConfiguration.getImageByName(ImageName.WON_BACKGROUND);
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
  }

  private void initElements() {
    // Back arrow
    Path arrow = StyleConfiguration.generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    //TODO database after a game
    backButton.setOnMouseClicked(e -> {
      User user = SessionManager.getUser();
      LobbyConfiguration.sendJoinServer(UserDto.mapUserAndHisGameStatistics(user,
          gameStatisticService
              .getAllStatisticsByUserId(user.getId())));
    });

    // Initialize the user name TextField
    Label userName = new Label(gameState.getCurrentPlayer().getUser());
    userName.setAlignment(Pos.CENTER);
    userName.setStyle(
        "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 70px; -fx-text-fill: white");

    // Wrap the userNameField in an HBox to center it
    HBox userNameFieldContainer = new HBox(userName);
    userNameFieldContainer.setAlignment(Pos.CENTER);

    HBox gridPaneContainer = new HBox(statisticsGridPane);
    gridPaneContainer.setAlignment(Pos.CENTER);

    // Create a VBox to hold the userNameField, userStackPane, and the labels
    VBox centerVBox = new VBox(userNameFieldContainer, userStackPane, gridPaneContainer);
    centerVBox.setSpacing(20);
    centerVBox.setAlignment(Pos.CENTER);

    Label winner = new Label("Winner Winner Chicken Dinner!");
    winner.setStyle(
        "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 90px; -fx-text-fill: white");

    HBox winnerBox = new HBox(winner);
    winnerBox.setAlignment(Pos.CENTER);

    root.setTop(winnerBox);
    root.setLeft(backButton);
    // Add some spacing around backButton
    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    BorderPane.setMargin(winnerBox, new Insets(10, 10, 10, 10));
    root.setCenter(centerVBox);
  }

  private void initGridPane() {
    statisticsGridPane = new GridPane();
    Label countriesWon = new Label("Countries won: ");
    Label countriesLost = new Label("Countries lost: ");
    Label troops = new Label("Total troops at the end: ");
    Label troopsGained = new Label("Troops gained: ");
    Label troopsLost = new Label("Troops lost: ");
    Label numberCountriesWon = new Label(Integer.toString(statistic.getCountriesWon()));
    Label numberCountriesLost = new Label(Integer.toString(statistic.getCountriesLost()));
    Label numberTroops = new Label(Integer.toString(statistic.getNumberOfTroops()));
    Label numberTroopsGained = new Label(Integer.toString(statistic.getTroopsGained()));
    Label numberTroopsLost = new Label(Integer.toString(statistic.getTroopsLost()));

    countriesLost.setStyle(labelStyle);
    countriesWon.setStyle(labelStyle);
    troops.setStyle(labelStyle);
    troopsGained.setStyle(labelStyle);
    troopsLost.setStyle(labelStyle);
    numberCountriesWon.setStyle(numberStyle);
    numberCountriesLost.setStyle(numberStyle);
    numberTroops.setStyle(numberStyle);
    numberTroopsGained.setStyle(numberStyle);
    numberTroopsLost.setStyle(numberStyle);

    statisticsGridPane.add(troops, 0, 0);
    statisticsGridPane.add(troopsGained, 0, 1);
    statisticsGridPane.add(numberTroops, 1, 0);
    statisticsGridPane.add(numberTroopsGained, 1, 1);
    statisticsGridPane.add(troopsLost, 0, 2);
    statisticsGridPane.add(numberTroopsLost, 1, 2);
    statisticsGridPane.add(countriesWon, 0, 3);
    statisticsGridPane.add(numberCountriesWon, 1, 3);
    statisticsGridPane.add(countriesLost, 0, 4);
    statisticsGridPane.add(numberCountriesLost, 1, 4);

    statisticsGridPane.setAlignment(Pos.CENTER);
    statisticsGridPane.setHgap(30); // Set horizontal gap
    statisticsGridPane.setVgap(20); // Set vertical gap
  }

  private void initUserStackPane() {
    userImage = new ImageView(ImageConfiguration.getImageByName(ImageName.PLAYER_ICON));
    userImage.setFitHeight(200);
    userImage.setFitWidth(200);

    Circle circle = new Circle();
    circle.setRadius(95);
    circle.setStroke(Color.BLACK);
    circle.setFill(Color.LIGHTGRAY);
    circle.setStrokeWidth(3.0);

    // create a clip for the user image
    Circle clip = new Circle(userImage.getFitWidth() / 2, userImage.getFitHeight() / 2,
        circle.getRadius());

    // apply the clip to the user image
    userImage.setClip(clip);

    // create a stack pane to place the circle and image on top of each other
    userStackPane = new StackPane();
    userStackPane.getChildren().addAll(circle, userImage);
  }
}