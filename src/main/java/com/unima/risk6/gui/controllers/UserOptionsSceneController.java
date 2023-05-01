package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.LogInScene;
import com.unima.risk6.gui.scenes.UserOptionsScene;
import com.unima.risk6.gui.scenes.UserStatisticsScene;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.util.Duration;


public class UserOptionsSceneController {

  private final UserOptionsScene userOptions;
  private final SceneController sceneController;
  private User user;
  private BorderPane root;
  private ImageView userImage;
  private StackPane userStackPane;
  private final UserService userService;


  public UserOptionsSceneController(UserOptionsScene userOptions) {
    this.userOptions = userOptions;
    this.userService = DatabaseConfiguration.getUserService();
    this.sceneController = SceneConfiguration.getSceneController();
  }

  public void init() {
    this.user = SessionManager.getUser();
    this.root = (BorderPane) userOptions.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/Fonts/Fonts/Segoe UI Bold.ttf"),
        26);
    // Initialize elements
    initUserStackPane();
    initElements();
  }

  private void initElements() {
    BooleanProperty enterPressed = new SimpleBooleanProperty(false);
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

    // Initialize the user name TextField
    TextField userNameField = new TextField(user.getUsername());
    userNameField.setEditable(false);
    userNameField.setStyle("-fx-font-size: 40; -fx-background-color: transparent; "
        + "-fx-border-color: transparent;");
    userNameField.setAlignment(Pos.CENTER);

    // Add a Tooltip to the userNameField
    Tooltip tooltip = new Tooltip("Click twice to change the name");
    tooltip.setShowDelay(Duration.millis(50)); // Set the delay to 100 milliseconds
    Tooltip.install(userNameField, tooltip);

    // Add a click event listener to make the TextField editable
    EventHandler<MouseEvent> mouseClickedHandler = event -> {
      if (event.getClickCount() == 2) {
        userNameField.setEditable(true);
        userNameField.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; "
            + "-fx-font-size: 70px; -fx-background-color: lightgrey; "
            + "-fx-border-color: transparent;");
        userNameField.requestFocus();
      }
    };

    userNameField.setOnMouseClicked(mouseClickedHandler);

    userNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue && !enterPressed.get()) {
        userNameField.setEditable(false);

        if (!user.getUsername().equals(userNameField.getText())) {
          boolean confirm = showConfirmationDialog("Change username",
              "Do you really want to change the name of your user?");
          if (confirm) {
            user.setUsername(userNameField.getText());
            // Save the updated user name to the database
          } else {
            userNameField.setText(user.getUsername());
          }
        }
        userNameField.setStyle("-fx-font-size: 40; -fx-background-color: transparent; "
            + "-fx-border-color: transparent;");
      }
    });

    userNameField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        userNameField.setEditable(false);
        enterPressed.set(true);
        if (!user.getUsername().equals(userNameField.getText())) {
          boolean confirm = showConfirmationDialog("Change username",
              "Do you really want to change the name of your user?");
          if (confirm) {
            user.setUsername(userNameField.getText());
            userService.updateUser(user);
          } else {
            userNameField.setText(user.getUsername());
          }
        }
        userNameField.setStyle("-fx-font-size: 40; -fx-background-color: transparent; "
            + "-fx-border-color: transparent;");
        userNameField.getParent().requestFocus(); // Move the focus to the parent
      }
      enterPressed.set(false);
    });

    // Wrap the userNameField in an HBox to center it
    HBox userNameFieldContainer = new HBox(userNameField);
    userNameFieldContainer.setAlignment(Pos.CENTER);

    Button changeUserButton = new Button("Change User");

    changeUserButton.setPrefWidth(470);
    changeUserButton.setPrefHeight(40);
    changeUserButton.setAlignment(Pos.CENTER);
    applyButtonStyle(changeUserButton);
    changeUserButton.setFont(new Font(18));

    changeUserButton.setOnAction(e -> {
      if (sceneController.getSceneBySceneName(SceneName.LOGIN) == null) {
        LogInScene loginScene = new LogInScene(); // Create instance of the LogInScene
        LoginSceneController loginSceneController = new LoginSceneController(loginScene);
        loginScene.setLoginSceneController(loginSceneController);
        sceneController.addScene(SceneName.LOGIN, loginScene);
      }
      sceneController.activate(SceneName.LOGIN);
    });

    Button showStatisticsButton = new Button("Statistics");

    showStatisticsButton.setPrefWidth(470);
    showStatisticsButton.setPrefHeight(40);
    showStatisticsButton.setAlignment(Pos.CENTER);
    applyButtonStyle(showStatisticsButton);
    showStatisticsButton.setFont(new Font(18));

    showStatisticsButton.setOnAction(e -> {
      UserStatisticsScene scene = (UserStatisticsScene) SceneConfiguration.getSceneController()
          .getSceneBySceneName(SceneName.USER_STATISTICS);
      if (scene == null) {
        scene = new UserStatisticsScene();
        UserStatisticsSceneController userStatisticsSceneController = new UserStatisticsSceneController(
            scene);
        scene.setController(userStatisticsSceneController);
        sceneController.addScene(SceneName.USER_STATISTICS, scene);
      }
      sceneController.activate(SceneName.USER_STATISTICS);
    });

    // Create a VBox to hold the userNameField, userStackPane, and the labels
    VBox centerVBox = new VBox(userNameFieldContainer, userStackPane, showStatisticsButton,
        changeUserButton);
    centerVBox.setSpacing(20);
    centerVBox.setAlignment(Pos.CENTER);

    root.setLeft(backButton);
    // Add some spacing around backButton
    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    root.setCenter(centerVBox);
  }

  private void initUserStackPane() {
    userImage = new ImageView(new Image(getClass().getResource(user.getImagePath()).toString()));
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

  private boolean showConfirmationDialog(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);

    // Set custom button types
    ButtonType yesButton = new ButtonType("Yes");
    ButtonType noButton = new ButtonType("No");
    alert.getButtonTypes().setAll(noButton, yesButton);

    // Set styles directly in JavaFX
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.setStyle("-fx-background-color: linear-gradient(to top, #ffffff, #f2f2f2);"
        + " -fx-border-color: #bbb;"
        + " -fx-border-width: 1;"
        + " -fx-border-style: solid;");
    dialogPane.lookup(".label").setStyle("-fx-font-size: 14;"
        + " -fx-font-weight: bold;"
        + " -fx-text-fill: #444;");

    // Apply styles to buttons
    Button yesButtonNode = (Button) dialogPane.lookupButton(yesButton);
    Button noButtonNode = (Button) dialogPane.lookupButton(noButton);

    for (Button button : new Button[]{yesButtonNode, noButtonNode}) {
      button.setStyle("-fx-background-color: linear-gradient(#FFDAB9, #FFA07A);"
          + " -fx-text-fill: white;"
          + " -fx-background-radius: 5;"
          + " -fx-padding: 5 15 5 15;");

      button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: #FFDAB9; "
          + "-fx-text-fill: white;"));
      button.setOnMouseExited(event -> button.setStyle("-fx-background-color: linear-gradient"
          + "(#FFDAB9, #FFA07A); -fx-text-fill: white;"));
      button.setOnMousePressed(event -> button.setStyle("-fx-background-color: #FFA07A; "
          + "-fx-text-fill: white;"));
      button.setOnMouseReleased(event -> button.setStyle("-fx-background-color: linear-gradient"
          + "(#FFDAB9, #FFA07A); -fx-text-fill: white;"));
    }

    Optional<ButtonType> result = alert.showAndWait();
    return result.isPresent() && result.get() == yesButton;
  }

}