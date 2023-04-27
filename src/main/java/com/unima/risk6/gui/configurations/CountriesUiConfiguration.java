package com.unima.risk6.gui.configurations;

import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.gui.uiModels.CountryUi;
import com.unima.risk6.json.JsonParser;
import com.unima.risk6.json.jsonObjects.CountryUiJsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CountriesUiConfiguration {

  private final CountryUiJsonObject[] countryUiJsonObjects;

  private final Set<CountryUi> countriesUis;

  public CountriesUiConfiguration(String jsonCountriesFilePath) {
    InputStream inputStream = getClass().getResourceAsStream(jsonCountriesFilePath);
    try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
      this.countryUiJsonObjects = JsonParser.parseJsonFile(reader,
          CountryUiJsonObject[].class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    countriesUis = new HashSet<>();
  }

  public void configureCountries(Set<Country> countries) {
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

  public Set<CountryUi> getCountriesUis() {
    return countriesUis;
  }

}
