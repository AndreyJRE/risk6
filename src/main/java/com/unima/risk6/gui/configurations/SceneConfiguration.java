package com.unima.risk6.gui.configurations;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;

import com.unima.risk6.gui.controllers.GameSceneController;
import com.unima.risk6.gui.controllers.MultiplayerLobbySceneController;
import com.unima.risk6.gui.controllers.SceneController;
import com.unima.risk6.gui.controllers.SelectMultiplayerLobbySceneController;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.GameScene;
import com.unima.risk6.gui.scenes.MultiplayerLobbyScene;
import com.unima.risk6.gui.scenes.SelectMultiplayerLobbyScene;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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

    GameSceneController gameSceneController = null;
    GameScene gameScene = (GameScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.GAME);
    if (gameScene == null) {
      gameScene = new GameScene();
      gameSceneController = new GameSceneController(gameScene);
      gameScene.setGameSceneController(gameSceneController);
      sceneController.addScene(SceneName.GAME, gameScene);
    }
    sceneController.activate(SceneName.GAME);
    //TODO If we want to go full screen we can use this
    sceneController.getStage().setFullScreen(true);
    gameSceneController.showOrderPopup();
    sceneController.getStage().setOnCloseRequest((WindowEvent event) -> {
      event.consume();
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("Warning: Exiting Game");
      alert.setHeaderText("Are you sure you want to to leave the game?");
      alert.setContentText("If you leave, you cannot rejoin the game! Your place will be replaced "
          + "by a bot.");
      ButtonType buttonYes = new ButtonType("Yes, exit game");
      ButtonType buttonNo = new ButtonType("No, continue playing");
      alert.getButtonTypes().setAll(buttonYes, buttonNo);
      alert.showAndWait().ifPresent(buttonType -> {
        if (buttonType == buttonYes) {
          sceneController.getStage().close();
        }
        if (buttonType == buttonNo) {
          alert.close();
        }
      });
    });
    SoundConfiguration.playStartGameSound();
  }

  public static void joinServerLobbyScene() {
    SelectMultiplayerLobbyScene scene = (SelectMultiplayerLobbyScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.SELECT_LOBBY);
    if (scene == null) {
      scene = new SelectMultiplayerLobbyScene();
      SelectMultiplayerLobbySceneController selectMultiplayerLobbySceneController = new SelectMultiplayerLobbySceneController(
          scene);
      scene.setController(selectMultiplayerLobbySceneController);
      sceneController.addScene(SceneName.SELECT_LOBBY, scene);
    }
    pauseTitleSound();
    sceneController.activate(SceneName.SELECT_LOBBY);
  }

  public static void joinMultiplayerLobbyScene() {
    MultiplayerLobbyScene scene = (MultiplayerLobbyScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.MULTIPLAYER_LOBBY);
    if (scene == null) {
      scene = new MultiplayerLobbyScene();
      //TODO Overwrite game lobby owner
      MultiplayerLobbySceneController multiplayerLobbySceneController = new MultiplayerLobbySceneController(
          scene);
      scene.setController(multiplayerLobbySceneController);
      sceneController.addScene(SceneName.MULTIPLAYER_LOBBY, scene);
    }
    pauseTitleSound();
    sceneController.activate(SceneName.MULTIPLAYER_LOBBY);
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
