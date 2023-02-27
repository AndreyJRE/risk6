package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CardSymbol;
import javafx.scene.image.Image;

public class Card {

  private final CardSymbol cardSymbol;

  private final Image cardImage;

  public Card(CardSymbol cardSymbol, Image cardImage) {
    this.cardSymbol = cardSymbol;
    this.cardImage = cardImage;
  }

  public CardSymbol getCardSymbol() {
    return cardSymbol;
  }

  public Image getCardImage() {
    return cardImage;
  }
}
