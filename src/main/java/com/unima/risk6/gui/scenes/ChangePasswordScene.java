package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.ChangePasswordSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class ChangePasswordScene extends Scene implements InitializableScene {

  private ChangePasswordSceneController changePasswordSceneController;

  public ChangePasswordScene() {
    super(new BorderPane());
  }

  @Override
  public void init() {
    if (changePasswordSceneController != null) {
      changePasswordSceneController.init();
    }
  }

  public void setController(
      ChangePasswordSceneController changePasswordSceneController) {
    this.changePasswordSceneController = changePasswordSceneController;
  }
}
