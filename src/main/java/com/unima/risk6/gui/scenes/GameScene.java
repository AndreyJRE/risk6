package com.unima.risk6.gui.scenes;


import com.unima.risk6.gui.controllers.GameSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * Represents the actual game scene in the application. Here it gets initialised and then controlled
 * by the gameSceneController, where most logic for controlling the game UI lies.
 *
 * @author mmeider
 */

public class GameScene extends Scene implements InitializableScene {

  private GameSceneController gameSceneController;

  /**
   * Constructs a GameScene object with a default BorderPane as the root.
   */

  public GameScene() {
    super(new BorderPane());
  }

  /**
   * Sets the GameSceneController for the game scene.
   *
   * @param gameSceneController The GameSceneController object.
   */

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

