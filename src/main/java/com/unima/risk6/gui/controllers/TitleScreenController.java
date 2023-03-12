package com.unima.risk6.gui.controllers;

import com.unima.risk6.gui.SceneConfiguration;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class TitleScreenController {

  @FXML
  private TextField usernameField;

  @FXML
  private Button startGameButton;
  @FXML
  private Button quitGameButton;

  @FXML
  private void handleStartGame() {

  }

  @FXML
  private void handleQuitGame() {
    SceneController sceneController = SceneConfiguration.getSceneController();
    sceneController.close();
  }
}
