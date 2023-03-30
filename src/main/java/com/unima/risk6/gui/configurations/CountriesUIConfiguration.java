package com.unima.risk6.gui.configurations;

import com.unima.risk6.game.models.Country;
import com.unima.risk6.gui.scenes.CountryUI;
import com.unima.risk6.json.JsonParser;
import com.unima.risk6.json.jsonObjects.CountryUIJsonObject;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class CountriesUIConfiguration {

  private final CountryUIJsonObject[] countryUIJsonObjects;

  private final Set<CountryUI> countriesUIs;

  public CountriesUIConfiguration(String jsonCountriesFilePath) {
    File countriesJsonFile = new File(jsonCountriesFilePath);
    this.countryUIJsonObjects = JsonParser.parseJsonFile(countriesJsonFile,
        CountryUIJsonObject[].class);
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
