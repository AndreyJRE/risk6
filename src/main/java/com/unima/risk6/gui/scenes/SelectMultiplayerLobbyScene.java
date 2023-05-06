package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.SelectMultiplayerLobbySceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class SelectMultiplayerLobbyScene extends Scene implements InitializableScene {

  private SelectMultiplayerLobbySceneController selectMultiplayerLobbySceneController;

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
