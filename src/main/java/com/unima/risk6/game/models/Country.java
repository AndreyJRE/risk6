package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CountryName;
import java.util.Set;

/**
 * Represents a country in the risk game with its name, player who owns the country,its number of
 * troops occupies that occupy the country, its adjecentCountries and the continent it belongs to.
 *
 * @author wphung
 */
public class Country {

  private final CountryName countryName;
  private Player player;
  private boolean hasPlayer;
  private Integer troops;
  private Set<Country> adjacentCountries;
  private Continent continent;

  /**
   * Constructs a Country object using the given countryName.
   */
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

  /**
   * Changes the amount of troops on the country by the specified amount.
   *
   * @param diff the amount the troops should be changed by.
   */
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
    String userName;
    if (hasPlayer) {
      userName = player.getUser();
    } else {
      userName = "No player yet";
    }
    return "Country{"
        + "countryName=" + countryName
        + ", player=" + userName
        + ", hasPlayer=" + hasPlayer
        + ", troops=" + troops
        + ", continent=" + continent.getContinentName() + '}';
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


