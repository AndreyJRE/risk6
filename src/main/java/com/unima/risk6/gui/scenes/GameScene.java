package com.unima.risk6.gui.scenes;


import com.unima.risk6.gui.controllers.GameSceneController;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

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
}

