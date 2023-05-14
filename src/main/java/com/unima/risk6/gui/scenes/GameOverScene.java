package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.GameOverSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class GameOverScene extends Scene implements InitializableScene {

  private GameOverSceneController gameOverSceneController;

  public GameOverScene() {
    super(new BorderPane());
  }

  @Override
  public void init() {
    if (gameOverSceneController != null) {
      gameOverSceneController.init();
    }
  }

  public void setController(GameOverSceneController gameOverSceneController) {
    this.gameOverSceneController = gameOverSceneController;
  }

}

