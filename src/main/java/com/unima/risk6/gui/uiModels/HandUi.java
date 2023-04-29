package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.models.Hand;
import javafx.scene.layout.Pane;

public class HandUi extends Pane {

  private Hand hand;

  public HandUi(Hand hand) {
    super();
    this.hand = hand;
  }

  public Hand getHand() {
    return hand;
  }

  public void setHand(Hand hand) {
    this.hand = hand;
  }
}
