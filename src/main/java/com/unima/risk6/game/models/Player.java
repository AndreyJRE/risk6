package com.unima.risk6.game.models;

import java.util.HashSet;
import java.util.Set;

public class Player {

  private Hand hand;
  private Set<Country> countries;
  private Set<Continent> continents;
  private String user;
  private boolean isActive;
  private int deployableTroops;
  private int initialTroops;


  public Player() {
    this.hand = new Hand();
    countries = new HashSet<>();
    continents = new HashSet<>();
  }

  public Player(String user) {
    this.hand = new Hand();
    countries = new HashSet<>();
    continents = new HashSet<>();
    this.user = user;

  }
  /*
  //TODO Move to PlayerController
  public Map<Country, List<Country>> getAllAttackableCountryPairs() {
    Map<Country, List<Country>> countriesAttackable = new HashMap<>();
    for (Country country : countries) {
      List<Country> attackable = getValidAttackFromCountry(country);
      if (attackable.size() > 0) {
        countriesAttackable.put(country, attackable);
      }
    }
    return countriesAttackable;
  }

  //TODO Move to PlayerController

  public List<Country> getValidAttackFromCountry(Country country) {
    // TODO
    List<Country> attackable = new ArrayList<>();
    if (country.getTroops() >= 2) {
      for (Country adjacentCountry : country.getAdjacentCountries()) {
        if (!this.equals(adjacentCountry.getPlayer())) {
          attackable.add(country);
        }
      }
    }
    return attackable;
  }

  //TODO move to PlayerController
  public Map<Country, List<Country>> getAllValidFortifies() {
    Map<Country, List<Country>> countriesFortifiable = new HashMap<>();
    for (Country country : countries) {
      List<Country> fortifiable = getValidFortifiesFromCountry(country);
      countriesFortifiable.put(country, fortifiable);
    }
    return countriesFortifiable;
  }


   */

  public Set<Continent> getContinents() {
    return continents;
  }


  public int numberOfCountries() {
    return countries.size();
  }

  public Set<Country> getCountries() {
    return countries;
  }

  public void addCountry(Country countryToAdd) {
    countries.add(countryToAdd);
    countryToAdd.setPlayer(this);
  }

  public void removeCountry(Country countryToRemove) {
    countries.remove(countryToRemove);
  }

  public int getNumberOfCountries() {
    return countries.size();
  }

  public Hand getHand() {
    return hand;
  }

  public int getInitialTroops() {
    return initialTroops;
  }

  public void setInitialTroops(int initialTroops) {
    this.initialTroops = initialTroops;
  }

  public int getDeployableTroops() {
    return deployableTroops;
  }

  public void setDeployableTroops(int deployableTroops) {
    this.deployableTroops = deployableTroops;
  }

  public String getUser() {
    return user;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }
}

