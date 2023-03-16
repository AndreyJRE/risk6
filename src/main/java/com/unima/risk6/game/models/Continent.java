package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.ContinentName;
import java.util.Set;

public class Continent {

  private Set<Country> countries;
  private ContinentName continentName;
  private int bonusTroops;

  public Continent(ContinentName continentName) {
    this.continentName = continentName;

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

  public void setCountries(Set<Country> countries) {
    this.countries = countries;
  }

  public void setBonusTroops(int bonusTroops) {
    this.bonusTroops = bonusTroops;
  }

  @Override
  public String toString() {
    return "Continent{" +
        ", continentName=" + continentName +
        ", bonusTroops=" + bonusTroops +
        '}';
  }
}
