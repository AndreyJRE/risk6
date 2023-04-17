package com.unima.risk6.gui.configurations;

import com.unima.risk6.gui.controllers.SceneController;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

public class SceneConfiguration {

  private static SceneController sceneController;
  private static double width;
  private static double height;

  public static void startSceneControllerConfiguration(Stage stage) {
    configureListeners(stage);
    sceneController = new SceneController(stage);
  }

  private static void configureListeners(Stage stage) {
    ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
      width = stage.getWidth();
      height = stage.getHeight();
    };
    stage.widthProperty().addListener(stageSizeListener);
    stage.heightProperty().addListener(stageSizeListener);
  }


  public static SceneController getSceneController() {
    return sceneController;
  }

  public static double getWidth() {
    return width;
  }

  public static double getHeight() {
    return height;
  }
}
