package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.JoinOnlineSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class JoinOnlineScene extends Scene implements InitializableScene {

  private JoinOnlineSceneController joinOnlineSceneController;

  public JoinOnlineScene() {
    super(new BorderPane());
  }

  @Override
  public void init() {
    if (joinOnlineSceneController != null) {
      joinOnlineSceneController.init();
    }
  }

  public void setController(JoinOnlineSceneController joinOnlineSceneController) {
    this.joinOnlineSceneController = joinOnlineSceneController;
  }
}
