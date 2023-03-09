package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Country;

public class Reinforce extends Move {

  Country country;
  int toAdd;

  public Reinforce(Country pCountry, int pToAdd) {
    this.toAdd = pToAdd;
    this.country = pCountry;
  }

  public Country getCountry() {
    return country;
  }

  public int getToAdd() {
    return toAdd;
  }
}
