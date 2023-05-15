package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.GameOverSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * The GameOverScene class represents the end-of-game scene, displayed when the game is over. It
 * implements the InitializableScene interface and utilizes the GameOverSceneController for managing
 * the UI elements and actions within the scene.
 *
 * @author fisommer
 */

public class GameOverScene extends Scene implements InitializableScene {

  private GameOverSceneController gameOverSceneController;

  /**
   * Constructor for GameOverScene. It initializes a new scene with a BorderPane layout.
   */

  public GameOverScene() {
    super(new BorderPane());
  }

  /**
   * Initializes the scene by calling the controller's init method if the controller is not null.
   */

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

