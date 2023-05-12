package com.unima.risk6.gui.controllers;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.services.GameStatisticService;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.ImageName;
import com.unima.risk6.gui.controllers.enums.SceneName;
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
  private UserDto winner;


  public GameOverSceneController(GameOverScene gameOverScene, UserDto winner) {
    this.gameOverScene = gameOverScene;
    this.sceneController = SceneConfiguration.getSceneController();
    gameStatisticService = DatabaseConfiguration.getGameStatisticService();
    this.winner = winner;
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
    Image originalImage = ImageConfiguration.getBackgroundByName(ImageName.WON_BACKGROUND);
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
    backButton.setOnMouseClicked(e -> sceneController.activate(SceneName.TITLE));

    // Initialize the user name TextField
    Label userName = new Label(winner.getUsername());
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

    root.setLeft(backButton);
    // Add some spacing around backButton
    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    root.setCenter(centerVBox);
  }

  private void initGridPane() {
    statisticsGridPane = new GridPane();
    Label troopsLost = new Label("Number of Troops lost: ");
    Label troopsWon = new Label("Number of Troops defeated: ");
    Label countriesLost = new Label("Number of countries lost: ");
    Label countriesWon = new Label("Number of countries conquered: ");
    Label numberTroopsLost = new Label("0");
    Label numberTroopsWon = new Label("0");
    Label numberCountriesLost = new Label("0");
    Label numberCountriesWon = new Label("0");
    troopsWon.setStyle(labelStyle);
    troopsLost.setStyle(labelStyle);
    countriesLost.setStyle(labelStyle);
    countriesWon.setStyle(labelStyle);
    numberCountriesWon.setStyle(numberStyle);
    numberCountriesLost.setStyle(numberStyle);
    numberTroopsLost.setStyle(numberStyle);
    numberTroopsWon.setStyle(numberStyle);

    numberCountriesLost.setText(Double.toString(winner.getWinLossRatio()));
    numberTroopsWon.setText(Integer.toString(winner.getGamesLost()));
    numberTroopsLost.setText(Integer.toString(winner.getGamesWon()));
    numberCountriesWon.setText(Integer.toString(winner.getCountriesConquered()));

    statisticsGridPane.add(troopsLost, 0, 0);
    statisticsGridPane.add(troopsWon, 0, 1);
    statisticsGridPane.add(numberTroopsLost, 1, 0);
    statisticsGridPane.add(numberTroopsWon, 1, 1);
    statisticsGridPane.add(countriesLost, 0, 2);
    statisticsGridPane.add(numberCountriesLost, 1, 2);
    statisticsGridPane.add(countriesWon, 0, 3);
    statisticsGridPane.add(numberCountriesWon, 1, 3);
    statisticsGridPane.setAlignment(Pos.CENTER);
    statisticsGridPane.setHgap(30); // Set horizontal gap
    statisticsGridPane.setVgap(20); // Set vertical gap
  }

  private void initUserStackPane() {
    userImage = new ImageView(ImageConfiguration.getBackgroundByName(ImageName.PLAYER_ICON));
    userImage.setFitHeight(150);
    userImage.setFitWidth(150);

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