package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.GameSceneController;
import com.unima.risk6.gui.controllers.UserStatisticsSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class GameOverScene extends Scene implements InitializableScene {

  private GameSceneController gameSceneController;

  public GameOverScene() {
    super(new BorderPane());
  }

  @Override
  public void init() {
    if (gameSceneController != null) {
      gameSceneController.init();
    }
  }

  public void setController(GameSceneController gameSceneController) {
    this.gameSceneController = gameSceneController;
  }

}

