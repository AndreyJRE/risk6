package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.ContinentName;
import java.util.Set;

/**
 * Represents a continent in the game with its name, countries, and bonus troops.
 *
 * @author wphung
 */
public class Continent {

  private Set<Country> countries;
  private final ContinentName continentName;
  private int bonusTroops;

  /**
   * Creates a new continent with the specified name.
   *
   * @param continentName the name of the continent
   */
  public Continent(ContinentName continentName) {
    this.continentName = continentName;

  }

  /**
   * Returns the countries in this continent.
   *
   * @return the countries in this continent
   */
  public Set<Country> getCountries() {
    return countries;
  }

  /**
   * Returns the name of this continent.
   *
   * @return the name of this continent
   */
  public ContinentName getContinentName() {
    return continentName;
  }

  /**
   * Returns the bonus troops awarded for owning this continent.
   *
   * @return the bonus troops awarded for owning this continent
   */
  public int getBonusTroops() {
    return bonusTroops;
  }

  /**
   * Sets the countries in this continent.
   *
   * @param countries the countries to set
   */
  public void setCountries(Set<Country> countries) {
    this.countries = countries;
  }

  /**
   * Sets the bonus troops awarded for owning this continent.
   *
   * @param bonusTroops the bonus troops to set
   */
  public void setBonusTroops(int bonusTroops) {
    this.bonusTroops = bonusTroops;
  }

  /**
   * Returns a String representation of this continent.
   *
   * @return  Returns a String representation of this continent
   */
  @Override
  public String toString() {
    return "Continent{" +
        ", continentName=" + continentName +
        ", bonusTroops=" + bonusTroops +
        '}';
  }
}
