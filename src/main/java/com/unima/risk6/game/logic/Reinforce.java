package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Country;

/**
 * Represents a reinforcement move in the game of Risk, where troops are added to a country.
 *
 * @author wphung
 */
public class Reinforce extends Move {

  private Country country;
  private int toAdd;

  /**
   * Constructs a new reinforcement move with the given country and number of troops to add.
   *
   * @param country the country to add troops to
   * @param toAdd   the number of troops to add
   */
  public Reinforce(Country country, int toAdd) {
    this.toAdd = toAdd;
    this.country = country;
  }

  /**
   * Returns the country being reinforced.
   *
   * @return the country being reinforced
   */
  public Country getCountry() {
    return country;
  }

  /**
   * Returns the number of troops being added to the country.
   *
   * @return the number of troops being added
   */
  public int getToAdd() {
    return toAdd;
  }
}
