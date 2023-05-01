package com.unima.risk6.gui.controllers;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.InitializableScene;
import java.util.HashMap;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
  }

  public void close() {
    DatabaseConfiguration.closeDatabaseConnectionAndServices();
    stage.close();
  }

  public Stage getStage() {
    return stage;
  }

  public Scene getSceneBySceneName(SceneName sceneName) {
    return scenes.get(sceneName);
  }
}

