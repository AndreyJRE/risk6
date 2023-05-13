package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;
import static com.unima.risk6.gui.configurations.StyleConfiguration.generateBackArrow;

import com.unima.risk6.database.configurations.PasswordEncryption;
import com.unima.risk6.database.models.User;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.ImageName;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.SelectedUserScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
/**
 * Controller for managing the selected user scene.
 * This includes user interaction such as logging in with a password.
 *
 * @author fisommer
 */
public class SelectedUserSceneController {

  private final SelectedUserScene selectedUserScene;
  private final SceneController sceneController;
  private User user;
  private BorderPane root;
  private ImageView userImage;
  private StackPane userStackPane;
  private Label welcomeBack;
  private VBox passwordEntryBox;
  private PasswordField passwordField;

  /**
   * Constructs a new SelectedUserSceneController.
   *
   * @param selectedUserScene the scene this controller will manage
   */
  public SelectedUserSceneController(SelectedUserScene selectedUserScene) {
    this.selectedUserScene = selectedUserScene;
    this.sceneController = SceneConfiguration.getSceneController();
  }

  /**
   * Initializes the scene. This includes setting up user interface components
   * and loading the user's image.
   */
  public void init() {
    this.user = SessionManager.getUser();
    this.root = (BorderPane) selectedUserScene.getRoot();
    userImage = new ImageView(new Image(getClass().getResource(user.getImagePath()).toString()));
    userImage.setFitHeight(260);
    userImage.setFitWidth(260);

    initUserStackPane();
    initElements();

    Path arrow = generateBackArrow();

    // Wrap the arrow in a StackPane to handle the click event
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(e -> sceneController.activate(SceneName.LOGIN));

    HBox hbox = new HBox(passwordEntryBox);
    hbox.setAlignment(Pos.CENTER);

    VBox vBox = new VBox(welcomeBack, hbox);
    vBox.setAlignment(Pos.CENTER);
    vBox.setSpacing(50);
    root.setLeft(backButton);

    // Add some spacing around backButton
    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));

    // Load the image into an ImageView
    Image originalImage = ImageConfiguration.getImageByName(ImageName.SELECTED_USER_BACKGROUND);
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
    // Add passwordEntryBox to the center of the BorderPane
    root.setCenter(vBox);
  }
  /**
   * Initializes the StackPane that holds the user image.
   */
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
  /**
   * Validates the password entered by the user.
   *
   * @param user the user whose password is being validated
   * @param passwordField the field containing the entered password
   */
  private void passwordValidation(User user, PasswordField passwordField) {
    String enteredPassword = passwordField.getText();
    if (PasswordEncryption.validatePassword(enteredPassword, user.getPassword())) {
      sceneController.activate(SceneName.TITLE);
    } else {
      StyleConfiguration.showErrorDialog("Incorrect Password",
          "The password you entered is incorrect. Please try again.");
      passwordField.setText("");
    }
  }

  /**
   * Initializes various elements of the scene, such as text labels and buttons.
   */
  private void initElements() {
    Label selectedUserName = new Label(user.getUsername());
    selectedUserName.setStyle("-fx-font-size: 40; -fx-text-fill: white");

    passwordField = new PasswordField();
    passwordField.setPromptText("Enter password");
    passwordField.setStyle("-fx-font-size: 20; -fx-background-radius: 20; -fx-border-radius: 20;");
    passwordField.setPrefWidth(470);

    Button loginButton = new Button("Log in with password!");

    loginButton.setPrefWidth(470);
    loginButton.setPrefHeight(40);
    loginButton.setAlignment(Pos.CENTER);
    applyButtonStyle(loginButton);
    loginButton.setFont(new Font(18));

    welcomeBack = new Label("Welcome Back!");
    welcomeBack.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 80px; "
        + "-fx-font-weight: bold; -fx-text-fill: #2D2D2D; -fx-text-fill: white");
    passwordEntryBox = new VBox(userStackPane, selectedUserName, passwordField,
        loginButton);
    passwordEntryBox.setAlignment(Pos.CENTER);
    passwordEntryBox.setSpacing(20);

    passwordField.setOnAction(e ->
        passwordValidation(user, passwordField));

    loginButton.setOnAction(e -> passwordValidation(user, passwordField));
  }
}
