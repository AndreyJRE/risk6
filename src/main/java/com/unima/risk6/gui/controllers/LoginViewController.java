package com.unima.risk6.gui.controllers;


import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class LoginViewController {

  @FXML
  private TextField usernameField;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Button loginButton;

  @FXML
  private void handleLoginButton() {
    String username = usernameField.getText();
    String password = passwordField.getText();


    // Change the style of the button to simulate a press
    loginButton.setStyle("-fx-background-color: linear-gradient(#FFA07A, #FFDAB9); -fx-text-fill: #FFFFFF; -fx-background-radius: 20; -fx-border-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.14), 10, 0, 0, 0);");
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
    label.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 12px; -fx-text-fill: #BEBEBE; -fx-underline: true;");
  }

  @FXML
  private void handleForgotPasswordClicked(MouseEvent event) {
    // TODO: Implement password reset functionality here
  }

}
