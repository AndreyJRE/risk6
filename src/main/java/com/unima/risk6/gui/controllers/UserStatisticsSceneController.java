package com.unima.risk6.gui.controllers;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.repositories.GameStatisticRepository;
import com.unima.risk6.database.services.GameStatisticService;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.UserStatisticsScene;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;


public class UserStatisticsSceneController {

  private final UserStatisticsScene userStatisticsScene;
  private final SceneController sceneController;
  private User user;
  private BorderPane root;
  private ImageView userImage;
  private StackPane userStackPane;
  private GameStatisticRepository gameStatisticRepository;
  private final GameStatisticService gameStatisticService;
  private GridPane statisticsGridPane;
  private String numberStyle = "-fx-font-size: 26px;";
  private String labelStyle = "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 26px;";


  public UserStatisticsSceneController(UserStatisticsScene userStatisticsScene) {
    this.userStatisticsScene = userStatisticsScene;
    this.sceneController = SceneConfiguration.getSceneController();
    gameStatisticService = DatabaseConfiguration.getGameStatisticService();
  }

  public void init(User user) {
    this.user = user;
    this.root = (BorderPane) userStatisticsScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/Fonts/Fonts/Segoe UI Bold.ttf"), 26);
    // Initialize elements
    initUserStackPane();
    initGridPane();
    initElements();
  }

  private void initElements() {
    // Back arrow
    Path arrow = new Path();
    arrow.getElements().add(new MoveTo(10, 15));
    arrow.getElements().add(new LineTo(30, 0));
    arrow.getElements().add(new MoveTo(30, 30));
    arrow.getElements().add(new LineTo(10, 15));
    arrow.setStrokeWidth(3);
    arrow.setStroke(Color.BLACK);
    arrow.setFill(Color.TRANSPARENT);

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> sceneController.activate(SceneName.USER_OPTION));

    // Initialize the user name TextField
    Label userName = new Label(user.getUsername());
    userName.setAlignment(Pos.CENTER);
    userName.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 70px;");

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
    List<GameStatistic> statisticList = gameStatisticService.getAllStatisticsByUserId(user.getId());
    Label startDate = new Label("Account created on: ");
    Label troopsLost = new Label("Number of Troops lost: ");
    Label troopsWon = new Label("Number of Troops defeated: ");
    Label countriesLost = new Label("Number of countries lost: ");
    Label countriesWon = new Label("Number of countries defeated: ");
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
    if (!statisticList.isEmpty()) {
      GameStatistic userStatistics = statisticList.get(0);
      numberCountriesLost.setText(Integer.toString(userStatistics.getCountriesLost()));
      numberTroopsWon.setText(Integer.toString(userStatistics.getTroopsGained()));
      numberTroopsLost.setText(Integer.toString(userStatistics.getTroopsLost()));
      numberCountriesWon.setText(Integer.toString(userStatistics.getCountriesWon()));
    }
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
    userImage = new ImageView(new Image(getClass().getResource("/com/unima/risk6/pictures"
        + "/747376.png").toString()));
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