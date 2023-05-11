package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;
import static com.unima.risk6.gui.configurations.StyleConfiguration.showConfirmationDialog;

import com.unima.risk6.RisikoMain;
import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.exceptions.UsernameNotUniqueException;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.ChangePasswordScene;
import com.unima.risk6.gui.scenes.LogInScene;
import com.unima.risk6.gui.scenes.UserOptionsScene;
import com.unima.risk6.gui.scenes.UserStatisticsScene;
import java.io.IOException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.util.Duration;


public class UserOptionsSceneController {

  private final UserOptionsScene userOptions;
  private final SceneController sceneController;
  private final UserService userService;
  private User user;
  private BorderPane root;
  private ImageView userImage;
  private StackPane userStackPane;


  public UserOptionsSceneController(UserOptionsScene userOptions) {
    this.userOptions = userOptions;
    this.userService = DatabaseConfiguration.getUserService();
    this.sceneController = SceneConfiguration.getSceneController();
  }

  public void init() {
    this.user = SessionManager.getUser();
    this.root = (BorderPane) userOptions.getRoot();
    Font.loadFont(getClass().getResourceAsStream("/com/unima/risk6/fonts/Segoe UI Bold.ttf"),
        26);
    // Initialize elements
    initUserStackPane();
    initElements();
  }

  private void initElements() {
    BooleanProperty enterPressed = new SimpleBooleanProperty(false);
    // Back arrow
    Path arrow = generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> sceneController.activate(SceneName.TITLE));

    // Initialize the username TextField
    TextField userNameField = new TextField(user.getUsername());
    userNameField.setEditable(false);
    userNameField.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: "
        + "70px; -fx-background-color: transparent; "
        + "-fx-border-color: transparent;");
    userNameField.setAlignment(Pos.CENTER);

    // Add a Tooltip to the userNameField
    Tooltip tooltip = new Tooltip("Click twice to change the name");
    tooltip.setShowDelay(Duration.millis(20)); // Set the delay to 100 milliseconds
    Tooltip.install(userNameField, tooltip);
    tooltip.setStyle("-fx-font-size: 26px");

    // Add a click event listener to make the TextField editable
    EventHandler<MouseEvent> mouseClickedHandler = event -> {
      if (event.getClickCount() == 2) {
        userNameField.setEditable(true);
        userNameField.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: "
            + "70px; -fx-background-color: lightgrey; "
            + "-fx-border-color: transparent;");
        userNameField.requestFocus();
      }
    };

    userNameField.setOnMouseClicked(mouseClickedHandler);

    userNameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue && !enterPressed.get()) {
        userNameField.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: "
            + "70px; -fx-background-color: transparent; "
            + "-fx-border-color: transparent;");
        userNameField.setEditable(false);
        changeUsernameConfirmation(userNameField);
      }
    });

    userNameField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        userNameField.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: "
            + "70px; -fx-background-color: transparent; "
            + "-fx-border-color: transparent;");
        userNameField.setEditable(false);
        enterPressed.set(true);
        changeUsernameConfirmation(userNameField);
        userNameField.getParent().requestFocus(); // Move the focus to the parent
      }
      enterPressed.set(false);
    });

    // Wrap the userNameField in an HBox to center it
    HBox userNameFieldContainer = new HBox(userNameField);
    userNameFieldContainer.setAlignment(Pos.CENTER);

    Button changeUserButton = new Button("Switch account");

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
      SessionManager.logout();
      sceneController.activate(SceneName.LOGIN);
    });

    Button showStatisticsButton = new Button("View Statistics");

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
        UserStatisticsSceneController userStatisticsSceneController =
            new UserStatisticsSceneController(scene);
        scene.setController(userStatisticsSceneController);
        sceneController.addScene(SceneName.USER_STATISTICS, scene);
      }
      sceneController.activate(SceneName.USER_STATISTICS);
    });

    Button changePasswordButton = createChangePasswordButton();
    Button deleteUser = createDeleteUserButton();
    // Create a VBox to hold the userNameField, userStackPane, and the labels
    VBox centerVBox = new VBox(userNameFieldContainer, userStackPane, showStatisticsButton,
        changePasswordButton,
        changeUserButton, deleteUser);
    centerVBox.setSpacing(20);
    centerVBox.setAlignment(Pos.CENTER);

    root.setLeft(backButton);
    // Add some spacing around backButton
    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
    root.setCenter(centerVBox);
  }

  private Button createChangePasswordButton() {
    Button changePasswordButton = new Button("Change Password");
    changePasswordButton.setPrefWidth(470);
    changePasswordButton.setPrefHeight(40);
    changePasswordButton.setAlignment(Pos.CENTER);
    applyButtonStyle(changePasswordButton);
    changePasswordButton.setFont(new Font(18));

    changePasswordButton.setOnAction(e -> {
      ChangePasswordScene changePasswordScene = (ChangePasswordScene) sceneController
          .getSceneBySceneName(SceneName.CHANGE_PASSWORD);
      if (changePasswordScene == null) {
        changePasswordScene = new ChangePasswordScene();
        ChangePasswordSceneController changePasswordSceneController = new ChangePasswordSceneController(
            changePasswordScene);
        changePasswordScene.setController(changePasswordSceneController);
        sceneController.addScene(SceneName.CHANGE_PASSWORD, changePasswordScene);
      }
      sceneController.activate(SceneName.CHANGE_PASSWORD);
    });
    return changePasswordButton;
  }

  private Button createDeleteUserButton() {
    Button deleteUserButton = new Button("Delete User");
    deleteUserButton.setPrefWidth(470);
    deleteUserButton.setPrefHeight(40);
    deleteUserButton.setAlignment(Pos.CENTER);
    applyButtonStyle(deleteUserButton);
    deleteUserButton.setFont(new Font(18));

    deleteUserButton.setOnAction(e -> {
      boolean confirm = showConfirmationDialog("Delete User",
          "Are you sure that you want to delete your user? All Statistics will be permanently deleted and you will be redirected to the Log-in View.");
      if (confirm) {
        UserService userService = DatabaseConfiguration.getUserService();
        Long id = SessionManager.getUser().getId();
        userService.deleteUserById(id);
        if (userService.getAllUsers().size() == 0) {
          FXMLLoader fxmlLoader = new FXMLLoader(
              RisikoMain.class.getResource("fxml/CreateAccount" + ".fxml"));
          Parent root;
          try {
            root = fxmlLoader.load();
            Scene createScene = sceneController.getSceneBySceneName(SceneName.CREATE_ACCOUNT);
            if (createScene == null) {
              createScene = new Scene(root);
              sceneController.addScene(SceneName.CREATE_ACCOUNT, createScene);
            } else {
              createScene.setRoot(root);
            }
            sceneController.activate(SceneName.CREATE_ACCOUNT);

          } catch (IOException exception) {
            throw new RuntimeException(exception);
          }
        } else {
          if (sceneController.getSceneBySceneName(SceneName.LOGIN) == null) {
            LogInScene loginScene = new LogInScene(); // Create instance of the LogInScene
            LoginSceneController loginSceneController = new LoginSceneController(loginScene);
            loginScene.setLoginSceneController(loginSceneController);
            sceneController.addScene(SceneName.LOGIN, loginScene);
          }
          SessionManager.logout();
          sceneController.activate(SceneName.LOGIN);
        }
      }
    });
    return deleteUserButton;
  }

  private void changeUsernameConfirmation(TextField userNameField) {
    if (!user.getUsername().equals(userNameField.getText())) {
      boolean confirm = showConfirmationDialog("Change username",
          "Do you really want to change the name of your user?");
      if (confirm) {
        user.setUsername(userNameField.getText());
        try {
          userService.updateUser(user);
        } catch (UsernameNotUniqueException e) {
          StyleConfiguration.showErrorDialog("Error", e.getMessage());
          userNameField.setText(user.getUsername());
        }
      } else {
        userNameField.setText(user.getUsername());
      }
    }
    userNameField.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-weight: bold; -fx-font-size: "
        + "70px; -fx-background-color: transparent; "
        + "-fx-border-color: transparent;");
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

}