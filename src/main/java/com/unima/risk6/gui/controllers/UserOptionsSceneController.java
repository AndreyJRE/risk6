package com.unima.risk6.gui.controllers;

import com.unima.risk6.database.models.User;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.UserOptionsScene;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class UserOptionsSceneController {
  private final UserOptionsScene userOptions;
  private User user;
  private final SceneController sceneController;
  private BorderPane root;
  private ImageView userImage;
  private StackPane userStackPane;


  public UserOptionsSceneController(UserOptionsScene userOptions) {
    this.userOptions = userOptions;
    this.sceneController = SceneConfiguration.getSceneController();
  }

  public void init(User user) {
    this.user = user;
    this.root = (BorderPane) userOptions.getRoot();
    userImage = new ImageView(new Image(getClass().getResource("/com/unima/risk6/pictures"
        + "/747376.png").toString()));
    userImage.setFitHeight(200);
    userImage.setFitWidth(200);
    // Initialize elements
    initUserStackPane();
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
    backButton.setOnMouseClicked(e -> sceneController.activate(SceneName.TITLE));


    // Initialize three other labels
    Label label1 = new Label("Label 1");
    Label label2 = new Label("Label 2");
    Label label3 = new Label("Label 3");

    // Create a VBox to hold the userStackPane and the labels
    VBox centerVBox = new VBox(label1, userStackPane, label2, label3);
    centerVBox.setSpacing(20);
    centerVBox.setAlignment(Pos.CENTER);

    root.setLeft(backButton);
    root.setCenter(centerVBox);
  }

  private void initUserStackPane() {
    Circle circle = new Circle();
    circle.setRadius(125);
    circle.setStroke(Color.BLACK);
    circle.setFill(Color.LIGHTGRAY);
    circle.setStrokeWidth(5.0);

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

