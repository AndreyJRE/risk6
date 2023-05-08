package com.unima.risk6.gui.configurations;

import com.unima.risk6.gui.controllers.GameSceneController;
import com.unima.risk6.gui.controllers.SceneController;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.GameScene;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

public class SceneConfiguration {

  private static SceneController sceneController;
  private static double width;
  private static double height;

  public static void startSceneControllerConfiguration(Stage stage) {
    configureListeners(stage);
    width = stage.getWidth();
    height = stage.getHeight();
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

  public static void startGame() {
    GameScene gameScene = (GameScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.GAME);
    if (gameScene == null) {
      gameScene = new GameScene();
      GameSceneController gameSceneController = new GameSceneController(gameScene);
      gameScene.setGameSceneController(gameSceneController);
      sceneController.addScene(SceneName.GAME, gameScene);
    }
    sceneController.activate(SceneName.GAME);
    //TODO If we want to go full screen we can use this
    sceneController.getStage().setFullScreen(true);
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
