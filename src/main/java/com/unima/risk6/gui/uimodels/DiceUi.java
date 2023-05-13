package com.unima.risk6.gui.uimodels;

import com.unima.risk6.gui.configurations.ImageConfiguration;
import com.unima.risk6.gui.configurations.SoundConfiguration;
import com.unima.risk6.gui.controllers.enums.ImageName;
import java.util.Random;
import javafx.animation.PauseTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Represents a graphical user interface (UI) representation of a dice.
 *
 * @author mmeider
 */

public class DiceUi extends Pane {

  private ImageView diceView;

  private Random random;

  private final int result;

  private final boolean isAttackingDice;

  /**
   * Creates the UI representation of a dice.
   *
   * @param isAttackingDice Indicates whether it is an attacking dice or not.
   * @param result          The result of the dice roll.
   */

  public DiceUi(boolean isAttackingDice, int result) {
    random = new Random();
    this.isAttackingDice = isAttackingDice;
    if (isAttackingDice) {
      diceView = new ImageView(ImageConfiguration.getImageByName(ImageName.ATTACK_DICE_PREVIEW));
    } else {
      diceView = new ImageView(ImageConfiguration.getImageByName(ImageName.DICE_PREVIEW));
    }
    this.result = result;
    diceView.setPreserveRatio(true);
    diceView.setFitWidth(100);
    diceView.setFitHeight(100);
    getChildren().add(diceView);
  }

  /**
   * Rolls the dice by calling the rolling animation and the sound of rolling dice.
   */

  public void rollDice() {
    int rollDuration = 1000 + random.nextInt(750);
    showRollingGif();
    SoundConfiguration.playRollDiceSound();
    PauseTransition pauseTransition = new PauseTransition(Duration.millis(rollDuration));
    pauseTransition.setOnFinished(e -> showDiceResult(result));
    pauseTransition.play();
  }

  /**
   * Displays the rolling animation for the dice.
   */

  public void showRollingGif() {
    if (this.isAttackingDice) {
      diceView.setImage(ImageConfiguration.getImageByName(ImageName.ATTACK_DICE_ROLLING));
    } else {
      diceView.setImage(ImageConfiguration.getImageByName(ImageName.DICE_ROLLING));
    }

  }

  /**
   * Displays the result of the dice roll.
   *
   * @param result The result of the dice roll.
   */

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
