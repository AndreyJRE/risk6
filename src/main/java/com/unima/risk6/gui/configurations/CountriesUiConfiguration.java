package com.unima.risk6.gui.configurations;

import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.gui.uimodels.CountryUi;
import com.unima.risk6.json.JsonParser;
import com.unima.risk6.json.jsonobjects.CountryUiJsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Configuration class for managing the UI representation of countries. Provides methods to load
 * country UI data from a JSON file, configure countries based on the data, and retrieve the set of
 * country UI objects.
 *
 * @author mmeider
 */

public class CountriesUiConfiguration {

  private static final String COUNTRIES_JSON_PATH = "/com/unima/risk6/json/countriesUI.json";

  private static Set<CountryUi> countriesUis;


  /**
   * Retrieves the JSON objects representing the UI configuration for countries.
   *
   * @return an array of CountryUiJsonObject containing the UI configuration for countries.
   */

  private static CountryUiJsonObject[] getCountryUiJsonObjects() {
    CountryUiJsonObject[] countryUiJsonObjects;
    InputStream inputStream = CountriesUiConfiguration.class.getResourceAsStream(
        COUNTRIES_JSON_PATH);
    try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
      countryUiJsonObjects = JsonParser.parseJsonFile(reader,
          CountryUiJsonObject[].class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    countriesUis = new HashSet<>();
    return countryUiJsonObjects;
  }

  /**
   * Configures the UI for countries based on the provided set of Country objects.
   *
   * @param countries the set of Country objects for which to configure the UI.
   */

  public static void configureCountries(Set<Country> countries) {
    CountryUiJsonObject[] countryUiJsonObjects = getCountryUiJsonObjects();
    for (CountryUiJsonObject countryUiJson : countryUiJsonObjects) {
      CountryName countryName = countryUiJson.getCountryName();
      for (Country country : countries) {
        if (country.getCountryName().equals(countryName)) {
          countriesUis.add(new CountryUi(country, countryUiJson.getPath()));
        }
      }
    }
    setNeighboursCountryUisForEachCountry();
  }

  /**
   * Sets the adjacent CountryUi objects for each configured CountryUi object.
   */

  private static void setNeighboursCountryUisForEachCountry() {
    countriesUis.forEach(countryUi -> {
      countryUi.setAdjacentCountryUis(countriesUis.stream()
          .filter(countryUi1 -> countryUi1.getCountry().getAdjacentCountries()
              .contains(countryUi.getCountry()))
          .collect(Collectors.toSet()));
    });
  }

  public static Set<CountryUi> getCountriesUis() {
    return countriesUis;
  }

}
