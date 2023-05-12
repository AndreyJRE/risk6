package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.SinglePlayerSettingsSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class SinglePlayerSettingsScene extends Scene implements InitializableScene {

  private SinglePlayerSettingsSceneController singlePlayerSettingsSceneController;
  private boolean tutorial;

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

  public boolean isTutorial() {
    return tutorial;
  }
}

