package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.SinglePlayerSettingsSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;

public class SinglePlayerSettingsScene extends Scene implements InitializableScene {

  private SinglePlayerSettingsSceneController singlePlayerSettingsSceneController;

  public SinglePlayerSettingsScene() {
    super(new AnchorPane());
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

}

