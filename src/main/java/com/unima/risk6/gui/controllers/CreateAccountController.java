package com.unima.risk6.gui.controllers;


import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.exceptions.NotValidPasswordException;
import com.unima.risk6.database.exceptions.UsernameNotUniqueException;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SessionManager;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.SelectedUserScene;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class CreateAccountController implements Initializable {

  private UserService userService;
  @FXML
  private VBox root;
  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Button loginButton;

  @FXML
  private PasswordField checkPasswordField;

  @FXML
  private Label passwordMismatchLabel;

  private SceneController sceneController;

  @FXML
  private void handleSkipToTitleScreen() {
    sceneController = SceneConfiguration.getSceneController();
    sceneController.activate(SceneName.LOGIN);
  }

  @FXML
  private void handleCreateButton() {
    String username = usernameField.getText();
    String password = passwordField.getText();
    String checkPassword = checkPasswordField.getText();

    // Change the style of the button to simulate a press
    loginButton.setStyle("-fx-background-color: linear-gradient(#FFA07A, #FFDAB9); "
        + "-fx-text-fill: #FFFFFF; -fx-background-radius: 20;"
        + " -fx-border-radius: 20; -fx-effect:"
        + " dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");
    // Change it back after 200 milliseconds
    PauseTransition pause = new PauseTransition(Duration.millis(200));
    pause.setOnFinished(e -> loginButton.setStyle(
        "-fx-background-color:" + " linear-gradient(#FFDAB9, #FFA07A); -fx-text-fill: #FFFFFF;"
            + " -fx-background-radius: 20;" + " -fx-border-radius: 20;"
            + " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);"));
    pause.play();

    pause = new PauseTransition(Duration.millis(500));
    User user = new User(username, password, "/com/unima/risk6/pictures/747376.png");
    try {
      userService.saveUser(user);
      sceneController = SceneConfiguration.getSceneController();
      SelectedUserScene scene = (SelectedUserScene) SceneConfiguration.getSceneController()
          .getSceneBySceneName(SceneName.SELECTED_USER);
      if (scene == null) {
        scene = new SelectedUserScene();
        SelectedUserSceneController selectedUserSceneController = new SelectedUserSceneController(
            scene);
        scene.setController(selectedUserSceneController);
        sceneController.addScene(SceneName.SELECTED_USER, scene);
      }
      SessionManager.setUser(user);

      sceneController.activate(SceneName.SELECTED_USER);
    } catch (IllegalArgumentException illegalArgumentException) {
      showErrorDialog("Error", "Password and username should be non-empty!");
    } catch (UsernameNotUniqueException | NotValidPasswordException validationException) {
      showErrorDialog("Error", validationException.getMessage());
    }
  }

  private void showErrorDialog(String title, String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  @FXML
  private void handleMouseEntered(MouseEvent event) {
    Label label = (Label) event.getSource();
    label.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-text-fill: "
        + "#007FFF; -fx-underline: true; -fx-cursor: hand");
  }

  @FXML
  private void handleMouseExited(MouseEvent event) {
    Label label = (Label) event.getSource();
    label.setStyle(
        "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 14px; -fx-text-fill: #BEBEBE; "
            + "-fx-underline: true;");
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    userService = DatabaseConfiguration.getUserService();
    checkPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.equals(passwordField.getText())) {
        passwordMismatchLabel.setText("Passwords do not match!");
      } else {
        passwordMismatchLabel.setText("");
      }
    });
    root.setPrefHeight(SceneConfiguration.getHeight());
    root.setPrefHeight(SceneConfiguration.getWidth());

  }
}
