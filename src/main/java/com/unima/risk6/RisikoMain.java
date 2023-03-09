package com.unima.risk6;

import com.unima.risk6.gui.controllers.SceneController;
import com.unima.risk6.gui.controllers.enums.SceneName;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RisikoMain extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(RisikoMain.class.getResource("fxml/Log-In-View.fxml"));
    SceneController sceneController = new SceneController(stage);
    Scene scene = new Scene(fxmlLoader.load());
    stage.setTitle("Hello!");
    sceneController.addScene(SceneName.LOGIN_SCREEN, scene);
    sceneController.activate(SceneName.LOGIN_SCREEN);
    stage.show();
  }

  @Override
  public void init() throws Exception {
    //TODO (database start, etc.)
    //Dummy test
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