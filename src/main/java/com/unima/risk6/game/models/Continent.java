package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.ContinentName;
import java.util.Set;

public class Continent {

  private Set<Country> countries;
  private ContinentName continentName;
  private int bonusTroops;

  public Continent(Set<Country> inCountries, ContinentName pContinentName, int bonus) {
    countries = inCountries;
    continentName = pContinentName;
    bonusTroops = bonus;


  }

  public Set<Country> getCountries() {
    return countries;
  }

  public ContinentName getContinentName() {
    return continentName;
  }

  public int getBonusTroops() {
    return bonusTroops;
  }
}
