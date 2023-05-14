package com.unima.risk6;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SceneConfiguration;
import com.unima.risk6.gui.configurations.SoundConfiguration;
import com.unima.risk6.gui.controllers.LoginSceneController;
import com.unima.risk6.gui.controllers.SceneController;
import com.unima.risk6.gui.controllers.enums.SceneName;
import com.unima.risk6.gui.scenes.LogInScene;
import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RisikoMain extends Application {

  @Override
  public void start(Stage stage) throws IOException {

    UserService userService = DatabaseConfiguration.getUserService();
    List<User> users = userService.getAllUsers();
    stage.setMinWidth(1080);
    stage.setMinHeight(720);
    stage.setWidth(1080);
    stage.setHeight(720);
    SceneConfiguration.startSceneControllerConfiguration(stage);
    SceneController sceneController = SceneConfiguration.getSceneController();
    LogInScene loginScene = new LogInScene(); // Create instance of the LogInScene
    LoginSceneController loginSceneController = new LoginSceneController(loginScene);
    loginScene.setLoginSceneController(loginSceneController);
    sceneController.addScene(SceneName.LOGIN, loginScene);
    if (users.isEmpty()) {
      FXMLLoader fxmlLoader = new FXMLLoader(
          RisikoMain.class.getResource("fxml/CreateAccount.fxml"));
      Scene scene = new Scene(fxmlLoader.load());
      sceneController.addScene(SceneName.CREATE_ACCOUNT, scene);
      sceneController.activate(SceneName.CREATE_ACCOUNT);
    } else {
      sceneController.activate(SceneName.LOGIN);
    }
    stage.setTitle("RISK");
    stage.show();
  }

  @Override
  public void init() throws Exception {
    Task<Void> task = new Task<>() {
      @Override
      protected Void call() {
        DatabaseConfiguration.startDatabaseConfiguration();
        SoundConfiguration.loadSounds();
        ImageConfiguration.loadImages();
        return null;
      }
    };
    Thread thread = new Thread(task);
    thread.start();
    double progress = 0;
    while (!task.isDone()) {
      progress += 0.001;
      if (progress > 100) {
        progress = 100;
      }
      notifyPreloader(new Preloader.ProgressNotification(Math.round(progress)));
    }

  }

  public static void main(String[] args) {
    System.setProperty("javafx.preloader", RiskPreloader.class.getCanonicalName());
    launch(args);
  }
}