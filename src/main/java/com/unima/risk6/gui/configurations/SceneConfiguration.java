package com.unima.risk6.gui.configurations;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;

import com.unima.risk6.game.models.GameState;
import com.unima.risk6.gui.controllers.GameOverSceneController;
import com.unima.risk6.gui.controllers.GameSceneController;
import com.unima.risk6.gui.controllers.MultiplayerLobbySceneController;
import com.unima.risk6.gui.controllers.SceneController;
import com.unima.risk6.gui.controllers.SelectMultiplayerLobbySceneController;
import com.unima.risk6.gui.controllers.SinglePlayerSettingsSceneController;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.GameOverScene;
import com.unima.risk6.gui.scenes.GameScene;
import com.unima.risk6.gui.scenes.MultiplayerLobbyScene;
import com.unima.risk6.gui.scenes.SelectMultiplayerLobbyScene;
import com.unima.risk6.gui.scenes.SinglePlayerSettingsScene;
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

    stage.setFullScreenExitHint("");
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
    gameSceneController = gameScene.getGameSceneController();
    sceneController.activate(SceneName.GAME);
    sceneController.getStage().setMaximized(true);

    sceneController.getStage().setOnCloseRequest((WindowEvent event) -> {
      event.consume();
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("Warning: Exiting Game");
      alert.setHeaderText("Are you sure you want to to leave the game?");
      alert.setContentText(
          "If you leave, you cannot rejoin the game! Your place will be replaced " + "by a bot.");
      ButtonType buttonYes = new ButtonType("Yes, exit game");
      ButtonType buttonNo = new ButtonType("No, continue playing");
      alert.getButtonTypes().setAll(buttonYes, buttonNo);
      alert.showAndWait().ifPresent(buttonType -> {
        if (buttonType == buttonYes) {
          sceneController.close();
        }
        if (buttonType == buttonNo) {
          alert.close();
        }
      });
    });
    SoundConfiguration.playStartGameSound();
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    assert gameSceneController != null;
    gameSceneController.showOrderPopup();

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

  public static void startSinglePlayer() {
    SinglePlayerSettingsScene scene = (SinglePlayerSettingsScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.SINGLE_PLAYER_SETTINGS);
    if (scene == null) {
      scene = new SinglePlayerSettingsScene();
      SinglePlayerSettingsSceneController singlePlayerSettingsSceneController = new SinglePlayerSettingsSceneController(
          scene);
      scene.setController(singlePlayerSettingsSceneController);
      sceneController.addScene(SceneName.SINGLE_PLAYER_SETTINGS, scene);
    }
    pauseTitleSound();
    scene.setTutorial(false);
    sceneController.activate(SceneName.SINGLE_PLAYER_SETTINGS);
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

  public static void joinTutorialLobbyScene() {
    SinglePlayerSettingsScene scene = (SinglePlayerSettingsScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.SINGLE_PLAYER_SETTINGS);
    if (scene == null) {
      scene = new SinglePlayerSettingsScene();
      SinglePlayerSettingsSceneController singlePlayerSettingsSceneController = new SinglePlayerSettingsSceneController(
          scene);
      scene.setController(singlePlayerSettingsSceneController);
      sceneController.addScene(SceneName.SINGLE_PLAYER_SETTINGS, scene);
    }
    pauseTitleSound();
    scene.setTutorial(true);
    sceneController.activate(SceneName.SINGLE_PLAYER_SETTINGS);
  }

  public static void gameOverScene(GameState gameState) {
    GameOverScene scene = (GameOverScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.GAME_OVER);
    if (scene == null) {
      scene = new GameOverScene();
      GameOverSceneController gameOverSceneController = new GameOverSceneController(scene,
          gameState);
      scene.setController(gameOverSceneController);
      sceneController.addScene(SceneName.GAME_OVER, scene);
    }
    pauseTitleSound();
    sceneController.activate(SceneName.GAME_OVER);
  }
}
