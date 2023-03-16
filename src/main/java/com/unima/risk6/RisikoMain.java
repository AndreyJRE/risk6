package com.unima.risk6;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.gui.scenes.LogInScreen;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.stage.Stage;

public class RisikoMain extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    /**
     FXMLLoader fxmlLoader = new FXMLLoader(RisikoMain.class.getResource("fxml/CreateAccount.fxml"));
     SceneConfiguration.startSceneControllerConfiguration(stage);
     SceneController sceneController = SceneConfiguration.getSceneController();
     Scene scene = new Scene(fxmlLoader.load());
     */
    LogInScreen loginScreen = new LogInScreen(stage); // Create instance of the LogInScreen
    stage.setTitle("RISK");
    /**
     sceneController.addScene(SceneName.LOGIN_SCREEN, scene);
     sceneController.activate(SceneName.LOGIN_SCREEN);
     */
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