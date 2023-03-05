package com.unima.risk6;

import com.unima.risk6.gui.controllers.SplashScreen;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RisikoMain extends Application {

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(RisikoMain.class.getResource("fxml/hello-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 320, 240);
    stage.setTitle("Hello!");
    stage.setScene(scene);
    stage.show();
  }

  @Override
  public void init() throws Exception {
    //TODO (database start, etc.)
    //Dummy test
    for(int i = 0; i < 100000; i++){
      double progress = (100 * i) / 100000;
      notifyPreloader(new Preloader.ProgressNotification(progress));
    }
  }

  public static void main(String[] args) {
    System.setProperty("javafx.preloader", RiskPreloader.class.getCanonicalName());
    launch(args);
  }
}