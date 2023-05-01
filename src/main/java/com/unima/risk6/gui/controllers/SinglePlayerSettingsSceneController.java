package com.unima.risk6.gui.controllers;

import com.unima.risk6.RisikoMain;
import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.repositories.GameStatisticRepository;
import com.unima.risk6.database.services.GameStatisticService;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.SinglePlayerSettingsScene;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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


public class SinglePlayerSettingsSceneController {

  private final SinglePlayerSettingsScene singlePlayerSettingsScene;
  private final SceneController sceneController;
  private User user;
  private AnchorPane root;
  private GameStatisticRepository gameStatisticRepository;
  private GridPane statisticsGridPane;
  private final String numberStyle = "-fx-font-size: 26px;";
  private final String labelStyle = "-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 26px;";


  public SinglePlayerSettingsSceneController(SinglePlayerSettingsScene singlePlayerSettingsScene) {
    this.singlePlayerSettingsScene = singlePlayerSettingsScene;
    this.sceneController = SceneConfiguration.getSceneController();
  }

  public void init(User user) {
    this.user = user;
    this.root = (AnchorPane) singlePlayerSettingsScene.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/Fonts/Fonts/Segoe UI Bold.ttf"),
        26);
    // Initialize elements
    System.out.println(user.getImagePath());
    createPlayerStackPane(user.getImagePath());
    initGridPane();
    initElements();
  }

  private void initElements() {
    Path arrow = new Path();
    arrow.getElements().add(new MoveTo(7.5, 11.25));
    arrow.getElements().add(new LineTo(22.5, 0));
    arrow.getElements().add(new MoveTo(22.5, 22.5));
    arrow.getElements().add(new LineTo(7.5, 11.25));
    arrow.setStrokeWidth(3);
    arrow.setStroke(Color.BLACK);
    arrow.setFill(Color.TRANSPARENT);

    Label backLabel = new Label("Exit Lobby");
    backLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 20");
    backLabel.setAlignment(Pos.CENTER);

    HBox backHBox = new HBox(arrow, backLabel);
    backHBox.setSpacing(10);
    backHBox.setAlignment(Pos.CENTER);
// Create an invisible button
    Button invisibleButton = new Button();
    invisibleButton.setOpacity(0); // Make it invisible
    invisibleButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Make it fill the entire StackPane
    invisibleButton.setOnAction(e -> sceneController.activate(SceneName.TITLE)); // Set the action

// Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(backHBox, invisibleButton); // Add the invisibleButton here
    backButton.setStyle("-fx-background-color: lightgrey; -fx-background-radius: 20");
    backButton.setPadding(new Insets(10));

    // Initialize the username TextField
    Label title = new Label("Single Player Settings");
    title.setAlignment(Pos.CENTER);
    title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 46px;");

    Button play = new Button("Play");
    play.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: 28; "
        + "-fx-background-color: lightgrey; -fx-background-radius: 10");

    HBox gridPaneContainer = new HBox(statisticsGridPane);
    gridPaneContainer.setAlignment(Pos.CENTER);

    VBox centerVBox = new VBox(gridPaneContainer);
    centerVBox.setSpacing(60);
    centerVBox.setAlignment(Pos.CENTER);

    AnchorPane.setTopAnchor(backButton, 25.0);
    AnchorPane.setLeftAnchor(backButton, 25.0);

    AnchorPane.setBottomAnchor(play, 25.0);
    AnchorPane.setRightAnchor(play, 25.0);

    AnchorPane.setTopAnchor(title, 25.0);
    AnchorPane.setRightAnchor(title, 25.0);
    AnchorPane.setLeftAnchor(title, 25.0);


    root.getChildren().addAll(backButton, title, gridPaneContainer, play);
  }

  private void initGridPane() {
    statisticsGridPane = new GridPane();
  }

  /*private StackPane initBackButton() {
    // Back arrow


    return backButton;
  }*/

  private StackPane createPlayerStackPane(String imagePath) {
    ImageView userImage =
        new ImageView(new Image(getClass().getResource(imagePath).toString()));
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
    StackPane userStackPane = new StackPane();
    userStackPane.getChildren().addAll(circle, userImage);

    userStackPane.setOnMouseClicked(e -> sceneController.activate(SceneName.TITLE));
    return userStackPane;
  }
}
