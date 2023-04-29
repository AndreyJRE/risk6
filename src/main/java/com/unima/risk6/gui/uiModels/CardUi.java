package com.unima.risk6.gui.uiModels;

import com.unima.risk6.game.models.Card;
import javafx.scene.layout.Pane;

public class CardUi extends Pane {

  private Card card;

  public CardUi(Card card) {
    super();
    this.card = card;
  }

  public Card getCard() {
    return card;
  }

  public void setCard(Card card) {
    this.card = card;
  }
}
