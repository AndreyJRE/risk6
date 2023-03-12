package com.unima.risk6.gui.controllers;

import com.unima.risk6.gui.controllers.enums.SceneName;
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
    stage.setScene(scenes.get(name));
  }

  public void close() {
    stage.close();
  }

}

