package com.unima.risk6;

import com.unima.risk6.gui.controllers.SplashScreenController;
import java.util.Objects;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * A custom preloader class for our Risk application. This will be shown while our database gets configured and our images and sounds get
 * loaded.
 */
public class RiskPreloader extends Preloader {

    private Stage preloaderStage;
    private Scene scene;
    private SplashScreenController splashScreenController;

    public RiskPreloader() {

    }

    @Override
    public void init() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
            RiskPreloader.class.getResource("fxml/splashScreen.fxml"));
        scene = new Scene(fxmlLoader.load());
        splashScreenController = fxmlLoader.getController();
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.preloaderStage = stage;
        preloaderStage.getIcons().add(new Image(Objects.requireNonNull(RisikoMain.class.getResourceAsStream("pictures/logo.png"))));
        preloaderStage.setScene(scene);
        preloaderStage.initStyle(StageStyle.UNDECORATED);
        preloaderStage.show();
    }

    @Override
    public void handleApplicationNotification(Preloader.PreloaderNotification info) {
        if (info instanceof ProgressNotification i) {
            splashScreenController.getProgress()
                .setText("Version 1.1        Loading " + (i.getProgress() + "%"));
        }
    }

    @Override
    public void handleStateChangeNotification(Preloader.StateChangeNotification info) {
        StateChangeNotification.Type type = info.getType();
        if (Objects.requireNonNull(type) == Type.BEFORE_START) {
            preloaderStage.hide();
        }

    }

}
