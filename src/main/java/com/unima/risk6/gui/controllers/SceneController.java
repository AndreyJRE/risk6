package com.unima.risk6.gui.controllers;

import com.unima.risk6.RisikoMain;
import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.game.configurations.LobbyConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SoundConfiguration;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.InitializableScene;
import com.unima.risk6.network.configurations.NetworkConfiguration;
import java.io.IOException;
import java.util.HashMap;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class SceneController {

  private final HashMap<SceneName, Scene> scenes;
  private final Stage stage;
  private boolean previousWindowFullscreen;


  private SceneName currentSceneName;

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
    if (name == SceneName.TITLE) {
      initTitleScreen();
      currentSceneName = name;
      return;
    }
    Scene scene = scenes.get(name);
    if (scene instanceof InitializableScene scene1) {
      scene1.init();
    }
    stage.setScene(scene);

    stage.setWidth(SceneConfiguration.getWidth() + 0.1);
    stage.setHeight(SceneConfiguration.getHeight() + 0.1);
    stage.setOnCloseRequest((WindowEvent event) -> close());
    switch (name) {
      case TITLE -> SoundConfiguration.playTitleSound();
      case GAME -> SoundConfiguration.playInGameMusic();
    }
    fadeIn(scene);
  }

  public void close() {
    DatabaseConfiguration.closeDatabaseConnectionAndServices();
    if (NetworkConfiguration.getGameServerThread() != null) {
      NetworkConfiguration.stopGameServer();
    }
    if (LobbyConfiguration.getGameClient() != null) {
      LobbyConfiguration.stopGameClient();
    }
    stage.close();
    Platform.exit();
  }

  private void fadeIn(Scene scene) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(400), scene.getRoot());
    fadeTransition.setFromValue(0.0);
    fadeTransition.setToValue(1.0);

    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(400), scene.getRoot());
    scaleTransition.setFromX(0.98);
    scaleTransition.setFromY(0.98);
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

  public SceneName getCurrentSceneName() {
    return currentSceneName;
  }

  public void initTitleScreen() {
    FXMLLoader fxmlLoader = new FXMLLoader(RisikoMain.class.getResource("fxml/TitleScene"
        + ".fxml"));
    Parent root;
    try {
      root = fxmlLoader.load();
      Scene titleScene = new Scene(root);
      titleScene.setRoot(root);
      stage.setScene(titleScene);
      fadeIn(titleScene);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}

