package com.unima.risk6.gui.controllers;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.InitializableScene;
import java.util.HashMap;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneController {

  private final HashMap<SceneName, Scene> scenes;
  private final Stage stage;

  public SceneController(Stage stage) {
    this.stage = stage;
    this.scenes = new HashMap<>();
  }

  public void addScene(SceneName name, Scene scene) {
    scenes.put(name, scene);
  }

  public void removeScene(SceneName name) {
    scenes.remove(name);
  }

  public void activate(SceneName name) {
    Scene scene = scenes.get(name);
    if (scene instanceof InitializableScene scene1) {
      scene1.init();
    }
    stage.setScene(scene);
    fadeIn(scene);
  }

  public void close() {
    DatabaseConfiguration.closeDatabaseConnectionAndServices();
    stage.close();
  }

  private void fadeIn(Scene scene) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(650), scene.getRoot());
    fadeTransition.setFromValue(0.0);
    fadeTransition.setToValue(1.0);

    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(650), scene.getRoot());
    scaleTransition.setFromX(0.95);
    scaleTransition.setFromY(0.95);
    scaleTransition.setToX(1.0);
    scaleTransition.setToY(1.0);

    ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, scaleTransition);
    parallelTransition.play();
  }

  public Stage getStage() {
    return stage;
  }

  public Scene getSceneBySceneName(SceneName sceneName) {
    return scenes.get(sceneName);
  }
}

