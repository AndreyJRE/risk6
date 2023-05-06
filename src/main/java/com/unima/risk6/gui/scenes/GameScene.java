package com.unima.risk6.gui.scenes;


import com.unima.risk6.gui.controllers.GameSceneController;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

        BorderPane statisticPane = new BorderPane();

        VBox cardsBox = new VBox();
        Label cardTitleLabel = new Label("This is cards popup.");
        cardTitleLabel.setStyle("-fx-font-size: 18px; -fx-background-color: white;");
        cardsBox.getChildren().addAll(cardTitleLabel);
        statisticPane.setCenter(cardsBox);
        statisticPane.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 10;");

        statisticPopup = new Popup();
        statisticPopup.getContent().add(statisticPane);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.BLACK);
        dropShadow.setRadius(10);
        statisticPane.setEffect(dropShadow);

        statisticPopup.setX(this.getX());
        statisticPopup.setY(this.getY());
        statisticPopup.show(this.getWindow());
        isStatisticsShowing = true;
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
}

