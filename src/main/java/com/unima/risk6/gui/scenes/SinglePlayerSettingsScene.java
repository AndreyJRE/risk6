package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.SinglePlayerSettingsSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * The SinglePlayerSettingsScene class represents the scene for setting up single player games. It
 * implements the InitializableScene interface and uses the SinglePlayerSettingsSceneController to
 * manage the UI elements and actions within the scene.
 *
 * @author fisommer
 * @author astoyano
 * @author eameri
 */

public class SinglePlayerSettingsScene extends Scene implements InitializableScene {

  private SinglePlayerSettingsSceneController singlePlayerSettingsSceneController;
  private boolean tutorial;

  /**
   * Constructs a new SinglePlayerLobbyScene with a BorderPane as its root.
   */
  public SinglePlayerSettingsScene() {
    super(new BorderPane());
  }

  @Override
  public void init() {
    if (singlePlayerSettingsSceneController != null) {
      singlePlayerSettingsSceneController.init();
    }
  }

  public void setController(
      SinglePlayerSettingsSceneController singlePlayerSettingsSceneController) {
    this.singlePlayerSettingsSceneController = singlePlayerSettingsSceneController;
  }

  public void setTutorial(boolean tutorial) {
    this.tutorial = tutorial;
  }

  /**
   * Retrieves the current state of the tutorial flag.
   *
   * @return the current state of the tutorial flag
   */

  public boolean isTutorial() {
    return tutorial;
  }
}

