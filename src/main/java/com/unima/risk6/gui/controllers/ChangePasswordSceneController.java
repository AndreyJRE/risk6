package com.unima.risk6.gui.controllers;

import static com.unima.risk6.gui.configurations.StyleConfiguration.applyButtonStyle;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.exceptions.NotValidPasswordException;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.configurations.StyleConfiguration;
import com.unima.risk6.gui.controllers.enums.ImageName;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.ChangePasswordScene;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;

/**
 * Controller for the scene where the password is being changed.
 *
 * @author astoyano
 */
public class ChangePasswordSceneController {

  private final ChangePasswordScene changePasswordScene;

  private final SceneController sceneController;
  private final UserService userService;
  private BorderPane root;

  /**
   * Constructor for the ChangePasswordSceneController.
   *
   * @param changePasswordScene the scene that this controller is responsible for.
   */

  public ChangePasswordSceneController(ChangePasswordScene changePasswordScene) {
    this.changePasswordScene = changePasswordScene;
    this.userService = DatabaseConfiguration.getUserService();
    this.sceneController = SceneConfiguration.getSceneController();
  }

  /**
   * Initializes the controller and sets up the scene.
   */

  public void init() {
    root = (BorderPane) changePasswordScene.getRoot();
    root.setPrefWidth(SceneConfiguration.getWidth());
    root.setPrefHeight(SceneConfiguration.getHeight());
    Path arrow = StyleConfiguration.generateBackArrow();
    StackPane backButton = new StackPane(arrow);
    backButton.setOnMouseClicked(event -> sceneController.activate(SceneName.USER_OPTION));

    // Add some spacing around backButton
    BorderPane.setMargin(backButton, new Insets(10, 0, 0, 10));
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
    BackgroundSize backgroundSize = new BackgroundSize(100, 100, true,
        true, true, true);
    BackgroundImage backgroundImage = new BackgroundImage(semiTransparentImage,
        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
        backgroundSize);
    Background background = new Background(backgroundImage);

    AnchorPane anchorPane = createNewPasswordFields();

    root.setBackground(background);
    root.setCenter(anchorPane);
    root.setLeft(backButton);


  }

  /**
   * Creates the confirmation button.
   *
   * @return the created button.
   */

  private Button createConfirmButton() {
    Button confirmButton = new Button("Confirm");
    confirmButton.setPrefWidth(800);
    confirmButton.setPrefHeight(40);
    confirmButton.setAlignment(Pos.CENTER);
    applyButtonStyle(confirmButton);
    confirmButton.setFont(new Font(18));
    return confirmButton;
  }

  /**
   * Creates the fields for entering the new password.
   *
   * @return an AnchorPane containing the fields for entering the new password.
   */

  private AnchorPane createNewPasswordFields() {
    AnchorPane anchorPane = new AnchorPane();
    anchorPane.setPrefSize(600, 495);
    anchorPane.setPadding(new Insets(150, 200, 150, 200));

    VBox vbox = new VBox();
    vbox.setAlignment(Pos.CENTER);
    vbox.setSpacing(22);
    vbox.setStyle(
        "-fx-opacity: 0.9; -fx-background-color: #FFFFFF; -fx-background-radius: 20; -fx-effect: "
            + "dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");
    AnchorPane.setTopAnchor(vbox, 0.0);
    AnchorPane.setRightAnchor(vbox, 0.0);
    AnchorPane.setBottomAnchor(vbox, 0.0);
    AnchorPane.setLeftAnchor(vbox, 0.0);

    Label selectUser = new Label("Change Password");
    selectUser.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 41px; "
        + "-fx-font-weight: bold; -fx-text-fill: #2D2D2D;");
    PasswordField newPasswordField = new PasswordField();
    newPasswordField.setPromptText("New password");
    newPasswordField.setStyle(
        "-fx-font-size: 20; -fx-background-radius: 20; -fx-border-radius: 20;");
    newPasswordField.setPrefWidth(470);
    PasswordField confirmPasswordField = new PasswordField();
    confirmPasswordField.setPromptText("Confirm password");
    confirmPasswordField.setStyle(
        "-fx-font-size: 20; -fx-background-radius: 20; -fx-border-radius: 20;");
    confirmPasswordField.setPrefWidth(470);

    Label errorMessage = new Label();
    errorMessage.setTextFill(Color.RED);
    errorMessage.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
    Button confirmButton = createConfirmButton();
    confirmButton.setDisable(true); // Disable the button initially

    ChangeListener<String> passwordFieldChangeListener = (observable, oldValue, newValue) -> {
      if (newPasswordField.getText().equals(confirmPasswordField.getText())) {
        // Passwords match, enable the submit button and clear the error message
        confirmButton.setDisable(false);
        errorMessage.setText("");
      } else {
        // Passwords don't match, disable the submit button and display an error message
        confirmButton.setDisable(true);
        errorMessage.setText("Passwords do not match.");
      }
    };
    confirmButton.setOnAction(event -> {
      User user = SessionManager.getUser();
      user.setPassword(newPasswordField.getText());
      try {
        userService.updateUser(user);
        sceneController.activate(SceneName.USER_OPTION);
      } catch (IllegalArgumentException illegalArgumentException) {
        StyleConfiguration.showErrorDialog("Error", "Password should be non-empty!");
      } catch (NotValidPasswordException validationException) {
        StyleConfiguration.showErrorDialog("Error", validationException.getMessage());
      }
    });
    newPasswordField.textProperty().addListener(passwordFieldChangeListener);
    confirmPasswordField.textProperty().addListener(passwordFieldChangeListener);
    vbox.setAlignment(Pos.CENTER);

    Label enterNewPassword = new Label("Enter new password:");
    Label confirmPassword = new Label("Confirm new password:");
    enterNewPassword.setFont(Font.font(18));
    confirmPassword.setFont(Font.font(18));
    enterNewPassword.setAlignment(Pos.CENTER_LEFT);
    confirmPassword.setAlignment(Pos.CENTER_LEFT);
    enterNewPassword.setMinWidth(200);
    confirmPassword.setMinWidth(200);

    GridPane centerGrid = new GridPane();
    centerGrid.add(enterNewPassword, 0, 0);
    centerGrid.add(confirmPassword, 0, 1);
    centerGrid.add(newPasswordField, 1, 0);
    centerGrid.add(confirmPasswordField, 1, 1);
    centerGrid.setHgap(20);
    centerGrid.setVgap(15);

    vbox.getChildren().addAll(selectUser, centerGrid, errorMessage, confirmButton);
    vbox.setPadding(new Insets(15, 20, 15, 20));

    anchorPane.getChildren().add(vbox);
    return anchorPane;
  }
}
