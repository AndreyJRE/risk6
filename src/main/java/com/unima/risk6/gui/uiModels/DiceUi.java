package com.unima.risk6.gui.uiModels;

import java.util.Random;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class DiceUi extends Pane {

  private ImageView diceView;
  private Random random;

  public DiceUi() {
    random = new Random();
    diceView = new ImageView(
        new Image(getClass().getResource("/com/unima/risk6/pictures/dice1.png").toString()));
    diceView.setPreserveRatio(true);
    diceView.setFitWidth(100);
    diceView.setFitHeight(100);
    getChildren().add(diceView);
  }

  public void rollDice() {
    int rollDuration = 1000 + random.nextInt(2000);
    showRollingGif();
    PauseTransition pauseTransition = new PauseTransition(Duration.millis(rollDuration));
    pauseTransition.setOnFinished(e -> showDiceResult(random.nextInt(6) + 1));
    pauseTransition.play();
  }

  public void showRollingGif() {
    diceView.setImage(new Image(
        getClass().getResource("/com/unima/risk6/pictures/diceRollAnimation.gif").toString()));
  }

  private void showDiceResult(int result) {
    diceView.setImage(new Image(
        getClass().getResource("/com/unima/risk6/pictures/dice" + result + ".png").toString()));
  }
}
