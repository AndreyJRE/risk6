package com.unima.risk6;

import com.unima.risk6.gui.controllers.SplashScreen;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RiskPreloader extends Preloader {

  private Stage preloaderStage;
  private Scene scene;

  public RiskPreloader() {

  }

  @Override
  public void init() throws Exception {
    FXMLLoader fxmlLoader = new FXMLLoader(RiskPreloader.class.getResource("fxml/splashScreen.fxml"));
    scene = new Scene(fxmlLoader.load());
  }

  @Override
  public void start(Stage stage) throws Exception {

    this.preloaderStage = stage;

    preloaderStage.setScene(scene);
    preloaderStage.initStyle(StageStyle.UNDECORATED);
    preloaderStage.show();
  }

  @Override
  public void handleApplicationNotification(Preloader.PreloaderNotification info){
    if (info instanceof ProgressNotification){
      SplashScreen.label.setText("Loading " + ((ProgressNotification) info).getProgress() + "%");
    }
  }

  @Override
  public void handleStateChangeNotification(Preloader.StateChangeNotification info){
    StateChangeNotification.Type type = info.getType();
    switch (type){

      case BEFORE_START:
        System.out.println("BEFORE_START");
        preloaderStage.hide();
        break;

    }

  }

}
