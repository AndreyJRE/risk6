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

/**
 * Controller class for managing scenes in the application. Provides methods to add and activate
 * scenes as well as initialize the title screen. Also includes methods for closing the application
 * and retrieving the current stage and scene.
 *
 * @author astoyano
 * @author fisommer
 */

public class SceneController {

  private final HashMap<SceneName, Scene> scenes;
  private final Stage stage;
  private SceneName currentSceneName;

  public SceneController(Stage stage) {
    this.stage = stage;
    this.scenes = new HashMap<>();
  }

  public void addScene(SceneName name, Scene scene) {
    scenes.put(name, scene);
  }

  /**
   * Activates the scene with the specified name, performs necessary initialization, and sets it as
   * the active scene.
   *
   * @param name the name of the scene to activate
   */
  public void activate(SceneName name) {
    if (name == SceneName.TITLE) {
      initTitleScreen();
      currentSceneName = name;
      SoundConfiguration.playTitleSound();
      return;
    }
    Scene scene = scenes.get(name);
    if (scene instanceof InitializableScene scene1) {
      scene1.init();
    }
    currentSceneName = name;
    stage.setScene(scene);
    stage.setWidth(SceneConfiguration.getWidth() + 0.1);
    stage.setHeight(SceneConfiguration.getHeight() + 0.1);
    stage.setOnCloseRequest((WindowEvent event) -> close());
    switch (name) {
      case GAME -> SoundConfiguration.playInGameMusic();
      default -> {
      }
    }
    fadeIn(scene);
  }

  /**
   * Closes the scene controller, releases any resources, and exits the JavaFX application.
   */
  public void close() {
    DatabaseConfiguration.closeDatabaseConnectionAndServices();
    if (NetworkConfiguration.getGameServerThread() != null
        && NetworkConfiguration.getGameServerThread().isAlive()) {
      NetworkConfiguration.stopGameServer();
    }
    if (LobbyConfiguration.getGameClient() != null) {
      LobbyConfiguration.stopGameClient();
    }
    stage.close();
    Platform.exit();
  }

  /**
   * Fades in the specified scene by applying a fade and scale transition animation.
   *
   * @param scene the scene to fade in
   */
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

  /**
   * Initializes and displays the title screen by loading the associated FXML file and setting it as
   * the active scene.
   */

  public void initTitleScreen() {
    FXMLLoader fxmlLoader = new FXMLLoader(
        RisikoMain.class.getResource("fxml/TitleScene" + ".fxml"));
    Parent root;
    try {
      root = fxmlLoader.load();
      Scene titleScene = new Scene(root);
      titleScene.setRoot(root);
      stage.setScene(titleScene);
      stage.setWidth(SceneConfiguration.getWidth() + 0.1);
      stage.setHeight(SceneConfiguration.getHeight() + 0.1);
      stage.setOnCloseRequest((WindowEvent event) -> close());
      fadeIn(titleScene);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public Scene getSceneBySceneName(SceneName sceneName) {
    return scenes.get(sceneName);
  }

  public SceneName getCurrentSceneName() {
    return currentSceneName;
  }

  public Stage getStage() {
    return stage;
  }
}

