package com.unima.risk6.gui.configurations;

import static com.unima.risk6.gui.configurations.SoundConfiguration.pauseTitleSound;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.game.configurations.GameConfiguration;
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
import javafx.stage.Stage;

/**
 * This class manages the configuration and control of scenes in the application. It provides
 * methods to start different scenes. It also handles the configuration of the stage and manages the
 * scene controller.
 *
 * @author astoyano
 * @author fisommer
 */

public class SceneConfiguration {

  private static SceneController sceneController;
  private static double width;
  private static double height;


  /**
   * Configures the scene controller and listeners for the stage.
   *
   * @param stage The stage for the application.
   */
  public static void startSceneControllerConfiguration(Stage stage) {
    configureListeners(stage);
    width = stage.getWidth();
    height = stage.getHeight();
    sceneController = new SceneController(stage);
  }


  /**
   * Configures listeners for the stage to update the width and height properties.
   *
   * @param stage The stage for the application.
   */
  private static void configureListeners(Stage stage) {
    ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
      width = stage.getWidth();
      height = stage.getHeight();
    };
    stage.widthProperty().addListener(stageSizeListener);
    stage.heightProperty().addListener(stageSizeListener);

    stage.setFullScreenExitHint("");
  }

  /**
   * Starts the game scene and activates it in the scene controller.
   */
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

    SoundConfiguration.playStartGameSound();
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    assert gameSceneController != null;
    if (GameConfiguration.getTutorial() == null) {
      GameStatistic gameStatistic = new GameStatistic(SessionManager.getUser());
      DatabaseConfiguration.getGameStatisticService().saveGameStatistic(gameStatistic);
      GameConfiguration.setCurrentGameStatistic(gameStatistic);
      gameSceneController.showOrderPopup();
    } else {
      gameSceneController.updateActivePlayerUi();
    }
  }

  /**
   * Joins the server lobby scene and activates it in the scene controller.
   */
  public static void joinServerLobbyScene() {
    SelectMultiplayerLobbyScene scene =
        (SelectMultiplayerLobbyScene) SceneConfiguration.getSceneController()
            .getSceneBySceneName(SceneName.SELECT_LOBBY);
    if (scene == null) {
      scene = new SelectMultiplayerLobbyScene();
      SelectMultiplayerLobbySceneController selectMultiplayerLobbySceneController =
          new SelectMultiplayerLobbySceneController(scene);
      scene.setController(selectMultiplayerLobbySceneController);
      sceneController.addScene(SceneName.SELECT_LOBBY, scene);
    }
    pauseTitleSound();
    sceneController.activate(SceneName.SELECT_LOBBY);
  }

  /**
   * Joins the multiplayer lobby scene and activates it in the scene controller.
   */

  public static void joinMultiplayerLobbyScene() {
    MultiplayerLobbyScene scene = (MultiplayerLobbyScene) SceneConfiguration.getSceneController()
        .getSceneBySceneName(SceneName.MULTIPLAYER_LOBBY);
    if (scene == null) {
      scene = new MultiplayerLobbyScene();
      //TODO Overwrite game lobby owner
      MultiplayerLobbySceneController multiplayerLobbySceneController =
          new MultiplayerLobbySceneController(scene);
      scene.setController(multiplayerLobbySceneController);
      sceneController.addScene(SceneName.MULTIPLAYER_LOBBY, scene);
    }
    pauseTitleSound();
    sceneController.activate(SceneName.MULTIPLAYER_LOBBY);
  }

  /**
   * Starts the single player game settings scene and activates it in the scene controller.
   */

  public static void startSinglePlayer() {
    SinglePlayerSettingsScene scene =
        (SinglePlayerSettingsScene) SceneConfiguration.getSceneController()
            .getSceneBySceneName(SceneName.SINGLE_PLAYER_SETTINGS);
    if (scene == null) {
      scene = new SinglePlayerSettingsScene();
      SinglePlayerSettingsSceneController singlePlayerSettingsSceneController =
          new SinglePlayerSettingsSceneController(scene);
      scene.setController(singlePlayerSettingsSceneController);
      sceneController.addScene(SceneName.SINGLE_PLAYER_SETTINGS, scene);
    }
    pauseTitleSound();
    scene.setTutorial(false);
    sceneController.activate(SceneName.SINGLE_PLAYER_SETTINGS);
  }

  /**
   * Joins the tutorial lobby scene and activates it in the scene controller.
   */

  public static void joinTutorialLobbyScene() {
    SinglePlayerSettingsScene scene =
        (SinglePlayerSettingsScene) SceneConfiguration.getSceneController()
            .getSceneBySceneName(SceneName.SINGLE_PLAYER_SETTINGS);
    if (scene == null) {
      scene = new SinglePlayerSettingsScene();
      SinglePlayerSettingsSceneController singlePlayerSettingsSceneController =
          new SinglePlayerSettingsSceneController(scene);
      scene.setController(singlePlayerSettingsSceneController);
      sceneController.addScene(SceneName.SINGLE_PLAYER_SETTINGS, scene);
    }
    pauseTitleSound();
    scene.setTutorial(true);
    sceneController.activate(SceneName.SINGLE_PLAYER_SETTINGS);
  }

  /**
   * Shows the game over scene with the specified game state.
   *
   * @param gameState The game state to display in the game over scene.
   */

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
