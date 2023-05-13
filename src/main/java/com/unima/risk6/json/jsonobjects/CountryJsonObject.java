package com.unima.risk6.json.jsonobjects;

import com.unima.risk6.game.models.enums.ContinentName;
import com.unima.risk6.game.models.enums.CountryName;

/**
 * Wrapper class for parsing json file with all countries
 *
 * @author astoyano
 */

public class CountryJsonObject {

  private CountryName countryName;

  private CountryName[] adjacentCountries;

  private ContinentName continent;


  /**
   * Constructor with all attributes
   *
   * @param countryName       - name of the country
   * @param adjacentCountries - array of adjacent countries
   * @param continent         - continent the country belongs to
   */
  public CountryJsonObject(CountryName countryName, CountryName[] adjacentCountries,
      ContinentName continent) {
    this.countryName = countryName;
    this.adjacentCountries = adjacentCountries;
    this.continent = continent;
  }

  public CountryName getCountryName() {
    return countryName;
  }

  public void setCountryName(CountryName countryName) {
    this.countryName = countryName;
  }

  public CountryName[] getAdjacentCountries() {
    return adjacentCountries;
  }

  public void setAdjacentCountries(CountryName[] adjacentCountries) {
    this.adjacentCountries = adjacentCountries;
  }

  public ContinentName getContinent() {
    return continent;
  }

  public void setContinent(ContinentName continent) {
    this.continent = continent;
  }


}
