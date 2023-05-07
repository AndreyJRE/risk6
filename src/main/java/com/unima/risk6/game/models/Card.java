package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.Objects;

/**
 * A class representing a Risk card that can either have a country or not.
 *
 * @author wphung
 */
public class Card {

  private final CardSymbol cardSymbol;

  private final CountryName country;
  private final boolean hasCountry;
  private final long id;

  /**
   * Creates a card with a symbol and a country.
   *
   * @param cardSymbol The symbol of the card.
   * @param country    The country represented by the card.
   */
  public Card(CardSymbol cardSymbol, CountryName country, int id) {
    this.cardSymbol = cardSymbol;
    this.country = country;
    this.hasCountry = true;
    this.id = id;
  }

  /**
   * Creates a card with only a symbol.
   *
   * @param cardSymbol The symbol of the card.
   */
  public Card(CardSymbol cardSymbol, int id) {
    this.cardSymbol = cardSymbol;
    this.country = null;
    this.hasCountry = false;
    this.id = id;

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

  public long getId() {
    return id;
  }


  /**
   * Returns whether the card has a country or not.
   *
   * @return True if the card has a country, false otherwise.
   */

  public boolean hasCountry() {
    return hasCountry;
  }

  /**
   * Compares the card to the given object for equality. Returns true if and only if the argument is
   * an object of the same type or has the same id as this card.
   *
   * @param o the object to compare this card against
   * @return true if the given object represents a value equivalent to this object, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Card card)) {
      return false;
    }
    return getId() == card.getId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(cardSymbol.toString()).append(" ");
    if (hasCountry) {
      assert country != null;
      stringBuilder.append(country.toString()).append(" ");
    }
    stringBuilder.append(id);

    return stringBuilder.toString();
  }
}
