package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.SelectedUserSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class SelectedUserScene extends Scene implements InitializableScene {

  private SelectedUserSceneController selectedUserSceneController;

  public SelectedUserScene() {
    super(new BorderPane());
  }

  @Override
  public void init() {
    if (selectedUserSceneController != null) {
      selectedUserSceneController.init();
    }
  }

  public void setController(
      SelectedUserSceneController selectedUserSceneController) {
    this.selectedUserSceneController = selectedUserSceneController;
  }
}
