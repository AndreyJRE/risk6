package com.unima.risk6.gui.scenes;

import com.unima.risk6.gui.controllers.SceneController;
import javafx.stage.Stage;

public class SceneConfiguration {

  private static SceneController sceneController;

  public static void startSceneControllerConfiguration(Stage stage) {
    sceneController = new SceneController(stage);
  }


  public static SceneController getSceneController() {
    return sceneController;
  }

}
