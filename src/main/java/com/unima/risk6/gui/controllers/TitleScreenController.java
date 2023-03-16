package com.unima.risk6.gui.controllers;

import com.unima.risk6.gui.scenes.SceneConfiguration;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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
