package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.MultiplayerLobbySceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * The MultiplayerLobbyScene class represents the multiplayer game lobby scene. It implements the
 * InitializableScene interface and uses the MultiplayerLobbySceneController to manage the UI
 * elements and actions within the scene.
 *
 * @author fisommer
 */

public class MultiplayerLobbyScene extends Scene implements InitializableScene {

  private MultiplayerLobbySceneController multiplayerLobbySceneController;

  /**
   * Constructor for MultiplayerLobbyScene. It initializes a new scene with a BorderPane layout.
   */

  public MultiplayerLobbyScene() {
    super(new BorderPane());
  }

  /**
   * Initializes the scene by calling the controller's init method if the controller is not null.
   */

  @Override
  public void init() {
    if (multiplayerLobbySceneController != null) {
      multiplayerLobbySceneController.init();
    }
  }

  public void setController(MultiplayerLobbySceneController multiplayerLobbySceneController) {
    this.multiplayerLobbySceneController = multiplayerLobbySceneController;
  }

}
