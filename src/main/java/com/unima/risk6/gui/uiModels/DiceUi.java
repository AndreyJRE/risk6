package com.unima.risk6.gui.uiModels;

import com.unima.risk6.gui.configurations.SoundConfiguration;
import java.util.Random;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class DiceUi extends Pane {

  private ImageView diceView;

  private Random random;

  private int result;

  private boolean isAttackingDice;

  public DiceUi(boolean isAttackingDice) {
    random = new Random();
    this.isAttackingDice = isAttackingDice;
    if (isAttackingDice) {
      diceView = new ImageView(
          new Image(
              getClass().getResource("/com/unima/risk6/pictures/attackDicePreview.png")
                  .toString()));
    } else {
      diceView = new ImageView(
          new Image(
              getClass().getResource("/com/unima/risk6/pictures/dicePreview.png").toString()));
    }
    diceView.setPreserveRatio(true);
    diceView.setFitWidth(100);
    diceView.setFitHeight(100);
    getChildren().add(diceView);
  }

  public void rollDice() {
    int rollDuration = 1000 + random.nextInt(1000);
    showRollingGif();
    SoundConfiguration.playRollDiceSound();
    PauseTransition pauseTransition = new PauseTransition(Duration.millis(rollDuration));
    this.result = random.nextInt(6) + 1;
    pauseTransition.setOnFinished(e -> showDiceResult(result));
    pauseTransition.play();
  }

  public void showRollingGif() {
    if (this.isAttackingDice) {
      diceView.setImage(new Image(
          getClass().getResource("/com/unima/risk6/pictures/attackDiceRollAnimation.gif")
              .toString()));
    } else {
      diceView.setImage(new Image(
          getClass().getResource("/com/unima/risk6/pictures/diceRollAnimation.gif").toString()));
    }

  }

  private void showDiceResult(int result) {
    if (this.isAttackingDice) {
      diceView.setImage(new Image(
          getClass().getResource("/com/unima/risk6/pictures/attackDice" + result + ".png")
              .toString()));
    } else {
      diceView.setImage(new Image(
          getClass().getResource("/com/unima/risk6/pictures/dice" + result + ".png").toString()));
    }
  }


}
