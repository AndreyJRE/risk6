package com.unima.risk6.gui.configurations;

import com.unima.risk6.game.models.Country;
import com.unima.risk6.gui.scenes.CountryUI;
import com.unima.risk6.json.JsonParser;
import com.unima.risk6.json.jsonObjects.CountryUIJsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class CountriesUIConfiguration {

  private final CountryUIJsonObject[] countryUIJsonObjects;

  private final Set<CountryUI> countriesUIs;

  public CountriesUIConfiguration(String jsonCountriesFilePath) {
    InputStream inputStream = getClass().getResourceAsStream(jsonCountriesFilePath);
    try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
      this.countryUIJsonObjects = JsonParser.parseJsonFile(reader,
          CountryUIJsonObject[].class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    countriesUIs = new HashSet<>();
  }

  public void configureCountries() {

    for (CountryUIJsonObject countryUIJson : countryUIJsonObjects) {
      Country country = new Country(countryUIJson.getCountryName());
      countriesUIs.add(new CountryUI(country, countryUIJson.getPath()));
    }
  }

  public Set<CountryUI> getCountriesUIs() {
    return countriesUIs;
  }

}
