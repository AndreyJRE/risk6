package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.UserOptionsSceneController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class UserOptionsScene extends Scene implements InitializableScene {

  private UserOptionsSceneController userOptionsSceneController;

  public UserOptionsScene() {
    super(new BorderPane());
  }

  @Override
  public void init() {
    if (userOptionsSceneController != null) {
      userOptionsSceneController.init();
    }
  }

  public void setController(UserOptionsSceneController userOptionsSceneController) {
    this.userOptionsSceneController = userOptionsSceneController;
  }
}

