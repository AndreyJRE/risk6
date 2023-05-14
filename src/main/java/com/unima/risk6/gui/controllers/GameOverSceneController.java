package com.unima.risk6.gui.controllers;

import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Statistic;
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

/**
 * A controller class for managing the game over scene.
 * It handles the UI elements such as user statistics display and navigating back
 * to the title scene.
 *
 * @author fisommer
 */

public class GameOverSceneController {

  private final GameOverScene gameOverScene;
  private final SceneController sceneController;
  private BorderPane root;
  private ImageView userImage;
  private StackPane userStackPane;
  private GridPane statisticsGridPane;
  private final String numberStyle = "-fx-font-family: 'Segoe UI'; -fx-font-size: 26px; "
      + "-fx-text-fill: white";
  private final String labelStyle = "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; "
      + "-fx-font-size: 26px; -fx-text-fill: white";
  private final Statistic statistic;
  private final GameState gameState;

  /**
   * Constructor for the GameOverSceneController.
   *
   * @param gameOverScene the game over scene associated with this controller
   * @param gameState     the current state of the game
   */

  public GameOverSceneController(GameOverScene gameOverScene, GameState gameState) {
    this.gameOverScene = gameOverScene;
    this.sceneController = SceneConfiguration.getSceneController();
    this.statistic = gameState.getCurrentPlayer().getStatistic();
    this.gameState = gameState;
  }

  /**
   * Initializes the game over scene.
   * This includes loading the background image, initializing the UI elements,
   * and setting the opacity for the ImageView.
   */

  public void init() {
    this.root = (BorderPane) gameOverScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/segoe_ui_bold.ttf"),
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
    BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true,
        true);
    BackgroundImage backgroundImage = new BackgroundImage(semiTransparentImage,
        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
        backgroundSize);
    Background background = new Background(backgroundImage);
    root.setBackground(background);
  }

  /**
   * Initializes various UI elements of the game over scene.
   * This includes the back arrow, user name, and statistics display.
   */

  private void initElements() {
    // Back arrow
    Path arrow = StyleConfiguration.generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> {
      sceneController.activate(SceneName.TITLE);
    });

    // Initialize the user name TextField
    Label userName = new Label(gameState.getCurrentPlayer().getUser());
    userName.setAlignment(Pos.CENTER);
    userName.setStyle(
        "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 70px; -fx-text-fill: "
            + "white");

    // Wrap the userNameField in an HBox to center it
    HBox userNameFieldContainer = new HBox(userName);
    userNameFieldContainer.setAlignment(Pos.CENTER);

    HBox gridPaneContainer = new HBox(statisticsGridPane);
    gridPaneContainer.setAlignment(Pos.CENTER);

    // Create a VBox to hold the userNameField, userStackPane, and the labels
    VBox centervbox = new VBox(userNameFieldContainer, userStackPane, gridPaneContainer);
    centervbox.setSpacing(20);
    centervbox.setAlignment(Pos.CENTER);

    Label winner = new Label("Winner Winner Chicken Dinner!");
    winner.setStyle(
        "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 90px; -fx-text-fill: "
            + "white");

    HBox winnerBox = new HBox(winner);
    winnerBox.setAlignment(Pos.CENTER);

    root.setTop(winnerBox);
    root.setLeft(backButton);
    // Add some spacing around backButton
    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    BorderPane.setMargin(winnerBox, new Insets(10, 10, 10, 10));
    root.setCenter(centervbox);
  }

  /**
   * Initializes the grid pane that displays the user's game statistics.
   * These statistics include countries won/lost and troops count.
   */

  private void initGridPane() {
    statisticsGridPane = new GridPane();
    Label countriesWon = new Label("Countries won: ");
    countriesWon.setStyle(labelStyle);
    Label numberCountriesWon = new Label(Integer.toString(statistic.getCountriesWon()));
    numberCountriesWon.setStyle(numberStyle);
    Label countriesLost = new Label("Countries lost: ");
    countriesLost.setStyle(labelStyle);
    Label numberCountriesLost = new Label(Integer.toString(statistic.getCountriesLost()));
    numberCountriesLost.setStyle(numberStyle);
    Label troops = new Label("Total troops at the end: ");
    troops.setStyle(labelStyle);
    Label numberTroops = new Label(Integer.toString(statistic.getNumberOfTroops()));
    numberTroops.setStyle(numberStyle);
    Label troopsGained = new Label("Troops gained: ");
    troopsGained.setStyle(labelStyle);
    Label numberTroopsGained = new Label(Integer.toString(statistic.getTroopsGained()));
    numberTroopsGained.setStyle(numberStyle);
    Label troopsLost = new Label("Troops lost: ");
    troopsLost.setStyle(labelStyle);
    Label numberTroopsLost = new Label(Integer.toString(statistic.getTroopsLost()));
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

  /**
   * Initializes the user stack pane that displays the user's icon.
   */

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