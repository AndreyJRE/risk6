package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CountryName;
import java.util.HashSet;
import java.util.Set;

public class Country {
  private final CountryName countryName;
  private Player player;
  private boolean hasPlayer;
  private Integer troops;
  private Set<Country> adjacentCountries;

  public Country(CountryName countryName) {

    this.countryName = countryName;
    adjacentCountries = new HashSet<>();
    hasPlayer = false;

  }
  public CountryName getCountryName() {
    return countryName;
  }


  public void setPlayer(Player nPlayer) {
    player = nPlayer;
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
  public Integer getTroops(){
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
}


