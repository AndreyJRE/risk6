package com.unima.risk6.gui.controllers;


import com.unima.risk6.RisikoMain;
import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.SceneConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class CreateAccountController implements Initializable {

  private UserService userService;

  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Button loginButton;

  @FXML
  private void handleSkipToTitleScreen() {
    FXMLLoader fxmlLoader = new FXMLLoader(RisikoMain.class.getResource("fxml/TitleScreen.fxml"));
    Scene scene = null;
    try {
      scene = new Scene(fxmlLoader.load());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    SceneController sceneController = SceneConfiguration.getSceneController();
    sceneController.addScene(SceneName.TITLE_SCREEN, scene);
    sceneController.activate(SceneName.TITLE_SCREEN);
  }

  @FXML
  private void handleLoginButton() {
    String username = usernameField.getText();
    String password = passwordField.getText();

    // Change the style of the button to simulate a press
    loginButton.setStyle(
        "-fx-background-color: linear-gradient(#FFA07A, #FFDAB9); -fx-text-fill: #FFFFFF; -fx-background-radius: 20; -fx-border-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");
    // Change it back after 500 milliseconds
    PauseTransition pause = new PauseTransition(Duration.millis(500));
    pause.setOnFinished(e -> loginButton.setStyle("-fx-background-color: linear-gradient(#FFDAB9, #FFA07A); -fx-text-fill: #FFFFFF; -fx-background-radius: 20; -fx-border-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);"));
    pause.play();

    if (isValidCredentials(username, password)) {
      System.out.println("Login successful!");
      //TODO: Weiterleitung zur Hauptansicht oder ähnliches
    } else {
      System.out.println("Invalid credentials. Please try again.");
    }
  }

  private boolean isValidCredentials(String username, String password) {
    //TODO: Implementierung der Überprüfung der Anmeldeinformationen
    //Hier ein Beispiel:
    return username.equals("admin") && password.equals("admin123");
  }

  @FXML
  private void handleMouseEntered(MouseEvent event) {
    Label label = (Label) event.getSource();
    label.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 12px; -fx-text-fill: #007FFF; -fx-underline: true; -fx-cursor: hand");
  }

  @FXML
  private void handleMouseExited(MouseEvent event) {
    Label label = (Label) event.getSource();
    label.setStyle(
        "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 12px; -fx-text-fill: #BEBEBE; -fx-underline: true;");
  }

  @FXML
  private void handleForgotPasswordClicked(MouseEvent event) {
    // TODO: Implement password reset functionality here
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    userService = DatabaseConfiguration.getUserService();
  }
}
