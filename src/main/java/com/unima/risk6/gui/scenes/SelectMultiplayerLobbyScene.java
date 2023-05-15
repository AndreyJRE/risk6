package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.SelectMultiplayerLobbySceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * The SelectMultiplayerLobbyScene class represents the scene for selecting a multiplayer lobby.
 * It implements the InitializableScene interface and uses the SelectMultiplayerLobbySceneController
 * to manage the UI elements and actions within the scene.
 *
 * @author fisommer
 */

public class SelectMultiplayerLobbyScene extends Scene implements InitializableScene {

  private SelectMultiplayerLobbySceneController selectMultiplayerLobbySceneController;

  /**
   * Constructs a new SelectMultiplayerLobbyScene with a BorderPane as its root.
   */
  public SelectMultiplayerLobbyScene() {
    super(new BorderPane());
  }

  @Override
  public void init() {
    if (selectMultiplayerLobbySceneController != null) {
      selectMultiplayerLobbySceneController.init();
    }
  }

  public void setController(
      SelectMultiplayerLobbySceneController selectMultiplayerLobbySceneController) {
    this.selectMultiplayerLobbySceneController = selectMultiplayerLobbySceneController;
  }

}
