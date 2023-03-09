package com.unima.risk6.gui.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

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
    label.setStyle("-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 12px; -fx-text-fill: #007FFF; -fx-underline: true;");
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
