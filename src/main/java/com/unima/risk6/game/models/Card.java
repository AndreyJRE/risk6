package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import javafx.scene.image.Image;

public class Card {

  private final CardSymbol cardSymbol;

  //private final Image cardImage;

  private final CountryName country;

  public Card(CardSymbol cardSymbol, CountryName cardCountry) {//, Image cardImage) {
    this.cardSymbol = cardSymbol;
    //this.cardImage = cardImage;
    country = cardCountry;
  }

  public CardSymbol getCardSymbol() {
    return cardSymbol;
  }

  public CountryName getCountry() {
    return country;
  }
/*
  public Image getCardImage() {
    return cardImage;
  }
  */

}
