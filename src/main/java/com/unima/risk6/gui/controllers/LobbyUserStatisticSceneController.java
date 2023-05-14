package com.unima.risk6.gui.controllers;

import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.ImageName;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.LobbyUserStatisticScene;
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
 * Controller for the LobbyUserStatisticScene.
 * This class handles the interactions between the LobbyUserStatisticScene and the underlying logic.
 *
 * @author fisommer
 */

public class LobbyUserStatisticSceneController {

  private final LobbyUserStatisticScene lobbyUserStatisticScene;
  private final SceneController sceneController;
  private UserDto user;
  private BorderPane root;
  private ImageView userImage;
  private StackPane userStackPane;
  private GridPane statisticsGridPane;
  private String numberStyle = "-fx-font-size: 26px; -fx-text-fill: white";
  private String labelStyle = "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; "
      + "-fx-font-size: 26px; -fx-text-fill: white";

  /**
   * Constructs a new LobbyUserStatisticSceneController with the given LobbyUserStatisticScene.
   *
   * @param lobbyUserStatisticScene The LobbyUserStatisticScene for this controller.
   */

  public LobbyUserStatisticSceneController(LobbyUserStatisticScene lobbyUserStatisticScene) {
    this.lobbyUserStatisticScene = lobbyUserStatisticScene;
    this.sceneController = SceneConfiguration.getSceneController();
  }

  /**
   * Initializes the controller with the given user.
   *
   * @param user The user to display statistics for.
   */

  public void init(UserDto user) {
    this.user = user;
    this.root = (BorderPane) lobbyUserStatisticScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/segoe_ui_bold.ttf"),
        26);
    // Initialize elements
    initUserStackPane();
    initGridPane();
    initElements();
  }

  /**
   * Initializes the scene elements.
   */

  private void initElements() {
    // Back arrow
    Path arrow = StyleConfiguration.generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> sceneController.activate(SceneName.MULTIPLAYER_LOBBY));

    // Initialize the user name TextField
    Label userName = new Label(user.getUsername());
    userName.setAlignment(Pos.CENTER);
    userName.setStyle(
        "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 70px; "
            + "-fx-text-fill: white");

    // Wrap the userNameField in an HBox to center it
    HBox userNameFieldContainer = new HBox(userName);
    userNameFieldContainer.setAlignment(Pos.CENTER);

    HBox gridPaneContainer = new HBox(statisticsGridPane);
    gridPaneContainer.setAlignment(Pos.CENTER);

    // Create a VBox to hold the userNameField, userStackPane, and the labels
    VBox centerVbox = new VBox(userNameFieldContainer, userStackPane, gridPaneContainer);
    centerVbox.setSpacing(20);
    centerVbox.setAlignment(Pos.CENTER);

    // Load the image into an ImageView
    Image originalImage = ImageConfiguration.getImageByName(ImageName.STATISTICS_BACKGROUND);
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

    root.setLeft(backButton);
    // Add some spacing around backButton
    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    root.setCenter(centerVbox);
  }

  /**
   * Initializes the statistics GridPane.
   */

  private void initGridPane() {
    statisticsGridPane = new GridPane();
    Label gamesLost = new Label("Games lost: ");
    gamesLost.setStyle(labelStyle);
    Label gamesWon = new Label("Games won: ");
    gamesWon.setStyle(labelStyle);
    Label countriesConquered = new Label("Total countries conquered: ");
    countriesConquered.setStyle(labelStyle);
    Label hoursPlayed = new Label("Total hours played: ");
    hoursPlayed.setStyle(labelStyle);
    Label winLossRatio = new Label("Win / Loss Ratio: ");
    winLossRatio.setStyle(labelStyle);
    Label numberGamesLost = new Label(Integer.toString(user.getGamesLost()));
    numberGamesLost.setStyle(numberStyle);
    Label numberGamesWon = new Label(Integer.toString(user.getGamesWon()));
    numberGamesWon.setStyle(numberStyle);
    Label numberCountriesConquered = new Label(Integer.toString(user.getCountriesConquered()));
    numberCountriesConquered.setStyle(numberStyle);
    Label numberHoursPlayed = new Label(String.format("%.2f", user.getHoursPlayed()));
    numberHoursPlayed.setStyle(numberStyle);
    Label elo = new Label(String.format("%.2f", user.getWinLossRatio()));
    elo.setStyle(numberStyle);


    statisticsGridPane.add(gamesWon, 0, 0);
    statisticsGridPane.add(gamesLost, 0, 1);
    statisticsGridPane.add(numberGamesWon, 1, 0);
    statisticsGridPane.add(numberGamesLost, 1, 1);
    statisticsGridPane.add(countriesConquered, 0, 2);
    statisticsGridPane.add(numberCountriesConquered, 1, 2);
    statisticsGridPane.add(hoursPlayed, 0, 3);
    statisticsGridPane.add(numberHoursPlayed, 1, 3);
    statisticsGridPane.add(winLossRatio, 0, 4);
    statisticsGridPane.add(elo, 1, 4);

    statisticsGridPane.setAlignment(Pos.CENTER);
    statisticsGridPane.setHgap(30); // Set horizontal gap
    statisticsGridPane.setVgap(20); // Set vertical gap
  }

  /**
   * Initializes the user StackPane.
   */

  private void initUserStackPane() {
    userImage = new ImageView(
        ImageConfiguration.getImageByName(ImageName.PLAYER_ICON));
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