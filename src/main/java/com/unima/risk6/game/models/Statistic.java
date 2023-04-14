package com.unima.risk6.game.models;

public class Statistic {

  private int troopsLost;
  private int troopsGained;
  private int countriesWon;
  private int countriesLost;


  public Statistic() {
    this.troopsGained = 0;
    this.troopsLost = 0;
    this.countriesWon = 0;
    this.countriesLost = 0;
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
