package com.unima.risk6.game.configurations;

import com.unima.risk6.json.JsonParser;
import com.unima.risk6.json.jsonObjects.CountryJsonObject;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The CountriesConfiguration class represents a configuration of continents and countries parsed
 * from a JSON file. It has the following methods:
 *
 * @author astoyano
 */

public class CountriesConfiguration {

  private final CountryJsonObject[] countryJsonObjects;

  private final Set<Country> countries;

  private final Set<Continent> continents;


  /**
   * This constructor initializes the CountriesConfiguration object and takes a string parameter
   * jsonCountriesFilePath representing the file path of the JSON file containing the country and
   * continent data.
   *
   * @param jsonCountriesFilePath
   */
  public CountriesConfiguration(String jsonCountriesFilePath) {
    File countriesJsonFile = new File(jsonCountriesFilePath);
    this.countryJsonObjects = JsonParser.parseJsonFile(countriesJsonFile,CountryJsonObject[].class);
    countries = new HashSet<>();
    continents = new HashSet<>();
  }

  /**
   * This method configures the countries and continents from the parsed JSON file. It first creates
   * Country and Continent objects from the parsed JSON objects and adds them to the countries and
   * continents sets respectively. Then, it sets the adjacent countries for each Country object by
   * matching the names of the countries in the parsed JSON objects. Finally, it sets the countries
   * for each Continent object.
   */
  public void configureCountriesAndContinents() {

    for (CountryJsonObject countryJson : countryJsonObjects) {
      Country country = new Country(countryJson.getCountryName());
      countries.add(country);
      Continent continent;
      if (continents.stream().noneMatch(x ->
          countryJson.getContinent().equals(x.getContinentName()))) {
        continent = new Continent(countryJson.getContinent());
        continents.add(continent);
      } else {
        continent =
            continents.stream().filter(cont -> cont
                    .getContinentName().equals(countryJson.getContinent()))
                .findFirst().get();
      }
      country.setContinent(continent);

    }
    setCountriesForContinents();
    setNeighboursCountriesForEachCountry();
  }

  /**
   * This method sets the adjacent countries for each Country object. It matches the names of the
   * countries in the parsed JSON objects to the Country objects in the countries set and sets the
   * adjacent countries for each Country object.
   */
  private void setNeighboursCountriesForEachCountry() {
    Arrays.stream(countryJsonObjects).forEach(countryJsonObject -> {
      Country c = countries.stream()
          .filter(country -> country
              .getCountryName().equals(countryJsonObject.getCountryName()))
          .findFirst()
          .get();
      c.setAdjacentCountries(countries
          .stream()
          .filter(x -> Arrays.stream(countryJsonObject
                  .getAdjacentCountries())
              .toList()
              .contains(x.getCountryName()))
          .collect(Collectors.toSet()));
    });
  }

  /**
   * This method sets the countries for each Continent object. It filters the countries set for each
   * Continent object and sets the corresponding Country objects to the countries set of the
   * Continent object.
   */
  private void setCountriesForContinents() {
    continents.forEach(continent -> continent
        .setCountries(countries
            .stream()
            .filter(c -> c.getContinent().equals(continent))
            .collect(Collectors.toSet())));
  }

  /**
   * @return Set of countries objects
   */
  public Set<Country> getCountries() {
    return countries;
  }

  /**
   * @return Set of continents objects
   */
  public Set<Continent> getContinents() {
    return continents;
  }

}
