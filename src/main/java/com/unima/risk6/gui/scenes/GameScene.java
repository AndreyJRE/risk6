package com.unima.risk6.gui.scenes;


import com.unima.risk6.gui.controllers.GameSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class GameScene extends Scene implements InitializableScene {

  private GameSceneController gameSceneController;

  public GameScene() {
    super(new BorderPane());
  }

  public void setGameSceneController(GameSceneController gameSceneController) {
    this.gameSceneController = gameSceneController;
  }

  @Override
  public void init() {
    if (gameSceneController != null) {
      this.setRoot(new BorderPane());
      gameSceneController.init();
    }
  }

  public GameSceneController getGameSceneController() {
    return gameSceneController;
  }
}

