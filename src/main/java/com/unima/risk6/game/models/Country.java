package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CountryName;
import java.util.Set;

public class Country {

  private final int id;
  private final CountryName countryName;
  private final Card card;

  private Player player;

  private Integer troops;

  private Set<Country> adjacentCountries;

  public Country(CountryName countryName, int id, Card card) {
    this.id = id;
    this.countryName = countryName;
    this.card = card;
  }

  public int getId() {
    return id;
  }

  public CountryName getCountryName() {
    return countryName;
  }

  public Card getCard() {
    return card;
  }
}
