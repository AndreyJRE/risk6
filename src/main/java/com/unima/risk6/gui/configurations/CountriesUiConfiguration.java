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

public class CountriesUiConfiguration {

  private static final String COUNTRIES_JSON_PATH = "/com/unima/risk6/json/countriesUI.json";

  private static Set<CountryUi> countriesUis;

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
