package com.unima.risk6.game.models;

public class Statistic {

  private int troopsLost;
  private int troopsGained;
  private int countriesWon;
  private int countriesLost;
  private int numberOfOwnedCountries;
  private int numberOfTroops;


  public Statistic() {
    this.troopsGained = 0;
    this.troopsLost = 0;
    this.countriesWon = 0;
    this.countriesLost = 0;


  }

  @Override
  public String toString() {
    return "Statistic{" +
        "troopsLost=" + troopsLost +
        ", troopsGained=" + troopsGained +
        ", countriesWon=" + countriesWon +
        ", countriesLost=" + countriesLost +
        ", numberOfOwnedCountries=" + numberOfOwnedCountries +
        ", numberOfTroops=" + numberOfTroops +
        '}';
  }

  public int getNumberOfOwnedCountries() {
    return numberOfOwnedCountries;
  }

  public void setNumberOfOwnedCountries(int numberOfOwnedCountries) {
    this.numberOfOwnedCountries = numberOfOwnedCountries;
  }

  public int getNumberOfTroops() {
    return numberOfTroops;
  }

  public void setNumberOfTroops(int numberOfTroops) {
    this.numberOfTroops = numberOfTroops;
  }

  public int getTroopsLost() {
    return troopsLost;
  }

  public void setTroopsLost(int troopsLost) {
    this.troopsLost = troopsLost;
  }

  public int getTroopsGained() {
    return troopsGained;
  }

  public void setTroopsGained(int troopsGained) {
    this.troopsGained = troopsGained;
  }

  public int getCountriesWon() {
    return countriesWon;
  }

  public void setCountriesWon(int countriesWon) {
    this.countriesWon = countriesWon;
  }

  public int getCountriesLost() {
    return countriesLost;
  }

  public void setCountriesLost(int countriesLost) {
    this.countriesLost = countriesLost;
  }
}
