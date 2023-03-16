package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;

public class Card {

  private final CardSymbol cardSymbol;

  private final CountryName country;
  private final boolean hasCountry;


  public Card(CardSymbol cardSymbol, CountryName country) {
    this.cardSymbol = cardSymbol;
    this.country = country;
    this.hasCountry = true;
  }

  public Card(CardSymbol cardSymbol) {
    this.cardSymbol = cardSymbol;
    this.country = null;
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
