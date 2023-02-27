package com.unima.risk6.gui.models;

import com.unima.risk6.gui.models.enums.CardSymbol;
import javafx.scene.image.Image;

public class Card {

  private final CardSymbol CARD_SYMBOL;

  private final Image CARD_IMAGE;

  public Card(CardSymbol CARD_SYMBOL, Image cardImage) {
    this.CARD_SYMBOL = CARD_SYMBOL;
    CARD_IMAGE = cardImage;
  }

  public CardSymbol getCARD_SYMBOL() {
    return CARD_SYMBOL;
  }
}
