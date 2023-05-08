package com.unima.risk6.gui.scenes;


import com.unima.risk6.gui.controllers.GameSceneController;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;

public class GameScene extends Scene implements InitializableScene {

    private GameSceneController gameSceneController;

    private Popup statisticPopup;

    boolean isStatisticsShowing = false;

    public GameScene() {
        super(new BorderPane());
        this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB && !isStatisticsShowing) {
                showStatisticsPopup();
                event.consume();
            }
        });
        this.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                statisticPopup.hide();
                isStatisticsShowing = false;
                event.consume();
            }
        });

    }

    public void setGameSceneController(GameSceneController gameSceneController) {
        this.gameSceneController = gameSceneController;
    }

    @Override
    public void init() {
        if (gameSceneController != null) {
            this.setRoot(new BorderPane());
            gameSceneController.init();
        }
    }

    private void showStatisticsPopup() {

        BorderPane statisticPane = new BorderPane();

        VBox statisticsVBox = new VBox();
        Label statisticsTitleLabel = new Label("This is Statistics popup.");
        statisticsTitleLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white;");
        statisticsVBox.getChildren().addAll(statisticsTitleLabel);
        statisticPane.setCenter(statisticsVBox);
        statisticPane.setPrefSize(this.getWidth() * 0.7, this.getHeight() * 0.7);
        statisticPane.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");

        statisticPopup = new Popup();
        statisticPopup.getContent().add(statisticPane);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.BLACK);
        dropShadow.setRadius(10);
        statisticPane.setEffect(dropShadow);

        double popupWidth = statisticPane.getPrefWidth();
        double popupHeight = statisticPane.getPrefHeight();

        statisticPopup.setX(
                (this.getWindow().getX() + this.getWindow().getWidth() / 2) - popupWidth / 2);
        statisticPopup.setY(
                (this.getWindow().getY() + this.getWindow().getHeight() / 2) - popupHeight / 2);

        statisticPopup.show(this.getWindow());
        isStatisticsShowing = true;
    }
}

