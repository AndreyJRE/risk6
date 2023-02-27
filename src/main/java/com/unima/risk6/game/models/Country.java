package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CountryName;
import java.util.Set;

public class Country {

  private final int ID;
  private final CountryName NAME;
  private final Card CARD;

  private Player player;

  private Integer troops;

  private Set<Country> adjacentCountries;

  public Country(CountryName name, int id, Card card) {
    ID = id;
    NAME = name;
    CARD = card;
  }

  public int getID() {
    return ID;
  }

  public CountryName getNAME() {
    return NAME;
  }

  public Card getCARD() {
    return CARD;
  }
}
