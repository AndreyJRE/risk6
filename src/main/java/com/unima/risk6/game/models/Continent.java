package com.unima.risk6.game.models;

import java.util.Set;

public class Continent {

  private Set<Country> countries;
  private String continentName;
  private int bonusTroops;

  public Continent(Set<Country> inCountries, String name, int bonus) {
    countries = inCountries;
    continentName = name;
    bonusTroops = bonus;


  }

  public Set<Country> getCountries() {
    return countries;
  }

  public String getContinentName() {
    return continentName;
  }

  public int getBonusTroops() {
    return bonusTroops;
  }
}
