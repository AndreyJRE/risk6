package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CountryName;
import java.util.Set;

public class Country {

  private final CountryName countryName;
  private Player player;
  private boolean hasPlayer;
  private Integer troops;
  private Set<Country> adjacentCountries;
  private Continent continent;

  public Country(CountryName countryName) {
    this.countryName = countryName;
    this.hasPlayer = false;
    this.troops = 0;
    this.player = null;

  }

  public CountryName getCountryName() {
    return countryName;
  }


  public void setPlayer(Player player) {
    this.player = player;
    hasPlayer = true;
  }

  public Player getPlayer() {
    return player;
  }

  public boolean hasPlayer() {
    return hasPlayer;
  }

  public void setTroops(int troopNumber) {
    troops = troopNumber;
  }

  public Integer getTroops() {
    return troops;
  }

  public void changeTroops(int diff) {
    troops = troops + diff;
  }


  public void setAdjacentCountries(Set<Country> adjacentCountries) {
    this.adjacentCountries = adjacentCountries;
  }

  public Set<Country> getAdjacentCountries() {
    return adjacentCountries;
  }

  public void setContinent(Continent continent) {
    this.continent = continent;
  }

  public Continent getContinent() {
    return continent;
  }


  @Override
  public String toString() {
    return "Country{" +
        "countryName=" + countryName +
        ", player=" + player.getUser() +
        ", hasPlayer=" + hasPlayer +
        ", troops=" + troops +
        //  ", adjacentCountries=" + adjacentCountries. +
        ", continent=" + continent.getContinentName() +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Country country)) {
      return false;
    }
    return this.getCountryName().equals(country.getCountryName());
  }

  public void setHasPlayer(boolean hasPlayer) {
    this.hasPlayer = hasPlayer;
  }
}


