package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.GamePhase;
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
  private final Statistic statistic;


  public Player() {
    this.hand = new Hand();
    countries = new HashSet<>();
    continents = new HashSet<>();
    this.statistic = new Statistic();
  }

  public Player(String user) {
    this.hand = new Hand();
    countries = new HashSet<>();
    continents = new HashSet<>();
    this.user = user;
    this.statistic = new Statistic();

  }
  /*
  //TODO Move to PlayerController


  //TODO Move to PlayerController



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

