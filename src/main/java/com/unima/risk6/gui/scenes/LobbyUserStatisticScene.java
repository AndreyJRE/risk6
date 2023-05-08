package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.LobbyUserStatisticSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class LobbyUserStatisticScene extends Scene implements InitializableScene {

  private LobbyUserStatisticSceneController lobbyUserStatisticSceneController;

  public LobbyUserStatisticScene() {
    super(new BorderPane());
  }

  @Override
  public void init() {
    if (lobbyUserStatisticSceneController != null) {
      lobbyUserStatisticSceneController.init();
    }
  }

  public void setController(LobbyUserStatisticSceneController lobbyUserStatisticSceneController) {
    this.lobbyUserStatisticSceneController = lobbyUserStatisticSceneController;
  }

}

