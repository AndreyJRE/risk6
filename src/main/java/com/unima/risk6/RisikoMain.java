package com.unima.risk6;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.User;
import com.unima.risk6.database.services.UserService;
import com.unima.risk6.gui.scenes.LogInScreen;
import java.io.IOException;
import com.unima.risk6.gui.scenes.SceneConfiguration;
import com.unima.risk6.gui.controllers.SceneController;
import com.unima.risk6.gui.controllers.enums.SceneName;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.stage.Stage;

import java.io.IOException;

public class RisikoMain extends Application {

  private List<User> users;

  private UserService userService;

  @Override
  public void start(Stage stage) throws IOException {

    this.userService = DatabaseConfiguration.getUserService();
    this.users = userService.getAllUsers();
    SceneController sceneController = SceneConfiguration.getSceneController();
    if(users.isEmpty()){
      FXMLLoader fxmlLoader = new FXMLLoader(RisikoMain.class.getResource("fxml/CreateAccount.fxml"));
      SceneConfiguration.startSceneControllerConfiguration(stage);
      Scene scene = new Scene(fxmlLoader.load());
      sceneController.addScene(SceneName.LOGIN_SCREEN, scene);
      sceneController.activate(SceneName.LOGIN_SCREEN);
    }else{
      LogInScreen loginScreen = new LogInScreen(stage); // Create instance of the LogInScreen
    }
    stage.setTitle("RISK");
    stage.show();
  }

  @Override
  public void init() throws Exception {

    Platform.runLater(() -> DatabaseConfiguration.startDatabaseConfiguration());

    // updating of progress bar -> currently disabled for UI testing
    for (int i = 0; i < 100000; i++) {
      double progress = (100 * i) / 100000;
      notifyPreloader(new Preloader.ProgressNotification(progress));
    }
  }

  public static void main(String[] args) {
    System.setProperty("javafx.preloader", RiskPreloader.class.getCanonicalName());
    launch(args);
  }
}