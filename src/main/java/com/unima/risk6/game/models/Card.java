package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;

/**
 * A class representing a Risk card that can either have a country or not.
 *
 * @author wphung
 */
public class Card {

  private final CardSymbol cardSymbol;

  private final CountryName country;
  private final boolean hasCountry;

  /**
   * Creates a card with a symbol and a country.
   *
   * @param cardSymbol The symbol of the card.
   * @param country    The country represented by the card.
   */
  public Card(CardSymbol cardSymbol, CountryName country) {
    this.cardSymbol = cardSymbol;
    this.country = country;
    this.hasCountry = true;
  }

  /**
   * Creates a card with only a symbol.
   *
   * @param cardSymbol The symbol of the card.
   */
  public Card(CardSymbol cardSymbol) {
    this.cardSymbol = cardSymbol;
    this.country = null;
    this.hasCountry = false;
  }

  /**
   * Returns the symbol of the card.
   *
   * @return The symbol of the card.
   */
  public CardSymbol getCardSymbol() {
    return cardSymbol;
  }

  /**
   * Returns the country represented by the card.
   *
   * @return The country represented by the card.
   */
  public CountryName getCountry() {
    return country;
  }

  /**
   * Returns whether the card has a country or not.
   *
   * @return True if the card has a country, false otherwise.
   */

  public boolean isHasCountry() {
    return hasCountry;
  }
}
