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
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RisikoMain extends Application {

  @Override
  public void start(Stage stage) throws IOException {

    UserService userService = DatabaseConfiguration.getUserService();
    List<User> users = userService.getAllUsers();
    /*for (User tempUser : users) {
      tempUser.setImagePath("/com/unima/risk6/pictures/playerIcon.png");
      userService.updateUser(tempUser);
    }*/
    stage.setMinWidth(900);
    stage.setMinHeight(700);
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

    Platform.runLater(() -> {
      DatabaseConfiguration.startDatabaseConfiguration();
      SoundConfiguration.loadSounds();
      ImageConfiguration.loadImages();
    });

    // updating of progress bar -> currently disabled for UI testing
//    for (int i = 0; i < 100000; i++) {
//      double progress = (100 * i) / 100000;
//      notifyPreloader(new Preloader.ProgressNotification(progress));
//    }
  }

  public static void main(String[] args) {
    System.setProperty("javafx.preloader", RiskPreloader.class.getCanonicalName());
    launch(args);
  }
}