package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.MultiplayerLobbySceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class MultiplayerLobbyScene extends Scene implements InitializableScene {

  private MultiplayerLobbySceneController multiplayerLobbySceneController;

  public MultiplayerLobbyScene() {
    super(new BorderPane());
  }

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
