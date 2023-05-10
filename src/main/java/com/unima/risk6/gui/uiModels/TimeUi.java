package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.gui.controllers.GameSceneController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class TimeUi extends Group {

  private final int phaseTime;

  private int currentTimer;

  private Label timerLabel = new Label();

  private Timeline timeline = null;

  private Rectangle timerRectangle = new Rectangle(80, 30);

  public TimeUi(double rectangleWidth, double rectangleHeight, int phaseTime) {
    //TODO: ADAPT TURNTIME DEPENDING ON LOBBY SETTINGS AND IF MULTIPLAYER
    this.phaseTime = phaseTime;

    VBox timerBox = new VBox();
    timerBox.setAlignment(Pos.CENTER);

    Image reinforcementImage = new Image(
        getClass().getResource("/com/unima/risk6/pictures/hourglass.gif").toString());
    ImagePattern reinforcementImagePattern = new ImagePattern(reinforcementImage);
    Rectangle hourglassRectangle = new Rectangle(rectangleWidth, rectangleHeight);
    hourglassRectangle.setFill(reinforcementImagePattern);

    timerRectangle.setFill(Color.WHITE);
    timerRectangle.setArcWidth(30.0);
    timerRectangle.setArcHeight(30.0);
    timerRectangle.setStroke(Color.BLACK);

    StackPane stackPane = new StackPane(timerRectangle, timerLabel);
    stackPane.setPadding(new Insets(5));

    currentTimer = this.phaseTime;

    timerLabel.setText(String.format("%02d:%02d", currentTimer / 60, currentTimer % 60));
    timerLabel.setTextFill(Color.BLACK);
    timerLabel.setStyle(
        "-fx-font-size: 18px; -fx-background-color: white; -fx-font-weight: bold;");

    timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
      currentTimer--;
      if (currentTimer <= 0) {
        currentTimer = phaseTime;
        if (GameSceneController.getMyPlayerUi().getPlayer().getCurrentPhase()
            != GamePhase.NOT_ACTIVE) {
          //TODO Ask elliya to get reinforce and attack phase to work
          /*GameSceneController.getPlayerController().sendEndPhase(
              GameSceneController.getPlayerController().getPlayer().getCurrentPhase()); */
        }
        //timeline.stop();
      } else {
        timerLabel.setText(String.format("%02d:%02d", currentTimer / 60, currentTimer % 60));
      }
    }));
    timeline.setCycleCount(Timeline.INDEFINITE);

    timerLabel.setText(String.format("%02d:%02d", currentTimer / 60, currentTimer % 60));
    timerLabel.setTextFill(Color.BLACK);

    timeline.play();

    timerBox.getChildren().addAll(hourglassRectangle, stackPane);

    getChildren().addAll(timerBox);
  }

  public void restartTimer() {
    currentTimer = phaseTime;
  }

}
