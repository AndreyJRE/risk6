package com.unima.risk6.gui.controllers;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.GameStatisticService;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.ImageName;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.UserStatisticsScene;
import java.time.Duration;
import java.util.List;
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
 * The UserStatisticsSceneController manages the user statistics scene in the UI. It handles the
 * initialization and display of user-specific statistics and profile information, such as hours
 * played, ranking, number of countries lost and won. This class also manages the transition back to
 * the user option scene.
 *
 * @author fisommer
 * @author astoyano
 */

public class UserStatisticsSceneController {

  private final UserStatisticsScene userStatisticsScene;
  private final SceneController sceneController;
  private final GameStatisticService gameStatisticService;
  private User user;
  private BorderPane root;
  private ImageView userImage;
  private StackPane userStackPane;
  private GridPane statisticsGridPane;
  private final String numberStyle = "-fx-font-family: 'Segoe UI'; "
      + "-fx-font-size: 26px; -fx-text-fill: white";
  private final String labelStyle = "-fx-font-family: 'Segoe UI'; "
      + "-fx-font-weight: bold; -fx-font-size: 26px; -fx-text-fill: white";

  /**
   * Creates a UserStatisticsSceneController associated with a given UserStatisticsScene.
   *
   * @param userStatisticsScene the UserStatisticsScene this controller will manage.
   */

  public UserStatisticsSceneController(UserStatisticsScene userStatisticsScene) {
    this.userStatisticsScene = userStatisticsScene;
    this.sceneController = SceneConfiguration.getSceneController();
    gameStatisticService = DatabaseConfiguration.getGameStatisticService();
  }

  /**
   * Initializes the scene with the current user's statistics and relevant UI components. Sets the
   * background image and initializes the UI elements for displaying statistics.
   */

  public void init() {
    this.user = SessionManager.getUser();
    this.root = (BorderPane) userStatisticsScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/segoe_ui_bold.ttf"),
        26);
    // Initialize elements
    initUserStackPane();
    initGridPane();
    initElements();
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
    BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, true);
    BackgroundImage backgroundImage = new BackgroundImage(semiTransparentImage,
        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
        backgroundSize);
    Background background = new Background(backgroundImage);
    root.setBackground(background);
  }

  /**
   * Initializes the UI elements for the scene, including the back arrow, user name, and statistical
   * data display components.
   */

  private void initElements() {
    // Back arrow
    Path arrow = StyleConfiguration.generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> sceneController.activate(SceneName.USER_OPTION));

    // Initialize the user name TextField
    Label userName = new Label(user.getUsername());
    userName.setAlignment(Pos.CENTER);
    userName.setStyle(
        "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold;"
            + " -fx-font-size: 70px; -fx-text-fill: white");

    // Wrap the userNameField in an HBox to center it
    HBox userNameFieldContainer = new HBox(userName);
    userNameFieldContainer.setAlignment(Pos.CENTER);

    HBox gridPaneContainer = new HBox(statisticsGridPane);
    gridPaneContainer.setAlignment(Pos.CENTER);

    // Create a VBox to hold the userNameField, userStackPane, and the labels
    VBox centervbox = new VBox(userNameFieldContainer, userStackPane, gridPaneContainer);
    centervbox.setSpacing(20);
    centervbox.setAlignment(Pos.CENTER);

    root.setLeft(backButton);
    // Add some spacing around backButton
    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    root.setCenter(centervbox);
  }

  /**
   * Initializes the GridPane for displaying statistical data. Retrieves data from the
   * GameStatisticService and updates the UI components accordingly.
   */

  private void initGridPane() {
    statisticsGridPane = new GridPane();
    Label hoursPlayedLabel = new Label("Hours played: ");
    hoursPlayedLabel.setStyle(labelStyle);
    Label eloLabel = new Label("Elo: ");
    eloLabel.setStyle(labelStyle);
    Label countriesLostLabel = new Label("Number of countries lost: ");
    countriesLostLabel.setStyle(labelStyle);
    Label countriesWonLabel = new Label("Number of countries defeated: ");
    countriesWonLabel.setStyle(labelStyle);

    Label numberHoursPlayed = new Label("0");
    numberHoursPlayed.setStyle(numberStyle);
    Label numberRanking = new Label("0");
    numberRanking.setStyle(numberStyle);
    Label numberCountriesLost = new Label("0");
    numberCountriesLost.setStyle(numberStyle);
    Label numberCountriesWon = new Label("0");
    numberCountriesWon.setStyle(numberStyle);
    List<GameStatistic> statisticList = gameStatisticService.getAllStatisticsByUserId(user.getId());
    int won = (int) statisticList.stream().filter(GameStatistic::isGameWon).count();
    double lossRatio = ((double) won / statisticList.size()) * 10;
    numberRanking.setText(String.format("%.2f", lossRatio));
    int lostCountries = statisticList.stream()
        .mapToInt(GameStatistic::getCountriesWon).sum();
    numberCountriesLost.setText(Integer.toString(lostCountries));
    double hoursPlayed = statisticList.stream().mapToDouble(g -> {
      Duration duration = Duration.between(g.getStartDate(), g.getFinishDate());
      return duration.toSeconds() / 3600.0;
    }).sum();
    numberHoursPlayed.setText(String.format("%.2f", hoursPlayed));
    int wonCountries = statisticList.stream()
        .mapToInt(GameStatistic::getCountriesLost).sum();
    numberCountriesWon.setText(Integer.toString(wonCountries));
    statisticsGridPane.add(hoursPlayedLabel, 0, 0);
    statisticsGridPane.add(eloLabel, 0, 1);
    statisticsGridPane.add(numberHoursPlayed, 1, 0);
    statisticsGridPane.add(numberRanking, 1, 1);
    statisticsGridPane.add(countriesLostLabel, 0, 2);
    statisticsGridPane.add(numberCountriesLost, 1, 2);
    statisticsGridPane.add(countriesWonLabel, 0, 3);
    statisticsGridPane.add(numberCountriesWon, 1, 3);
    statisticsGridPane.setAlignment(Pos.CENTER);
    statisticsGridPane.setHgap(30); // Set horizontal gap
    statisticsGridPane.setVgap(40); // Set vertical gap
  }

  /**
   * Initializes the StackPane for displaying the user's profile picture. The image is clipped to
   * fit within a circular boundary.
   */

  private void initUserStackPane() {
    userImage = new ImageView(new Image(getClass().getResource(user.getImagePath()).toString()));
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