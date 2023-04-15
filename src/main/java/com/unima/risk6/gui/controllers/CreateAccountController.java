package com.unima.risk6.gui.controllers;


import com.unima.risk6.RisikoMain;
import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.exceptions.NotValidPasswordException;
import com.unima.risk6.database.exceptions.UsernameNotUniqueException;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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

  @FXML
  private void handleSkipToTitleScreen() {
    SecurityQuestionsController.activateTitleScreen();
  }

  @FXML
  private void handleCreateButton() {
    String username = usernameField.getText();
    String password = passwordField.getText();
    String checkPassword = checkPasswordField.getText();

    // Change the style of the button to simulate a press
    loginButton.setStyle(
        "-fx-background-color: linear-gradient(#FFA07A, #FFDAB9); "
            + "-fx-text-fill: #FFFFFF; -fx-background-radius: 20;"
            + " -fx-border-radius: 20; -fx-effect:"
            + " dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");
    // Change it back after 200 milliseconds
    PauseTransition pause = new PauseTransition(Duration.millis(200));
    pause.setOnFinished(e -> loginButton.setStyle("-fx-background-color:"
        + " linear-gradient(#FFDAB9, #FFA07A); -fx-text-fill: #FFFFFF;"
        + " -fx-background-radius: 20;"
        + " -fx-border-radius: 20;"
        + " -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);"));
    pause.play();

    pause = new PauseTransition(Duration.millis(500));
    User user = new User(username, password,
        "src/main/resources/Pictures/AdobeStock_259394679.png");
    try {
      userService.saveUser(user);
      FXMLLoader fxmlLoader = new FXMLLoader(RisikoMain.class.getResource("fxml/TitleScreen"
          + ".fxml"));
      Scene scene = null;

        scene = new Scene(fxmlLoader.load());

      SceneController sceneController = SceneConfiguration.getSceneController();
      sceneController.addScene(SceneName.TITLE, scene);
      sceneController.activate(SceneName.TITLE);
    } catch (IllegalArgumentException illegalArgumentException) {

    } catch (UsernameNotUniqueException usernameNotUniqueException) {
      System.out.println(usernameNotUniqueException.getMessage());
    } catch (NotValidPasswordException notValidPasswordException) {
      System.out.println(notValidPasswordException.getMessage());
    }catch (IOException ioException){
      throw new RuntimeException(ioException);
    }
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
