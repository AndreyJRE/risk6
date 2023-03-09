package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import javafx.scene.image.Image;

public class Card {

  private final CardSymbol cardSymbol;
  //private final Image cardImage;
  private final CountryName country;
  private final boolean hasCountry;


  public Card(CardSymbol pCardSymbol, CountryName cardCountry) {//, Image cardImage) {
    this.cardSymbol = pCardSymbol;
    country = cardCountry;
    this.hasCountry = true;
  }

  public Card(CardSymbol pCardSymbol) {
    this.cardSymbol = pCardSymbol;
    country= null;
    this.hasCountry = false;
  }

  public CardSymbol getCardSymbol() {
    return cardSymbol;
  }

  public CountryName getCountry() {
    return country;
  }

  public boolean isHasCountry() {
    return hasCountry;
  }
}
