package com.unima.risk6.network.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.models.Country;
import java.lang.reflect.Type;
import java.util.Set;

public class CountryTypeAdapter implements JsonSerializer<Country> {

  @Override
  public JsonElement serialize(Country country, Type typeOfSrc, JsonSerializationContext context) {
    if (country == null) {
      return null;
    }

    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("countryName", country.getCountryName().toString());

    if (country.getPlayer() != null) {
      jsonObject.addProperty("player", country.getPlayer().toString());
    } else {
      jsonObject.add("player", null);
    }

    jsonObject.addProperty("hasPlayer", country.hasPlayer());
    jsonObject.addProperty("troops", country.getTroops());

    JsonArray adjacentCountriesArray = new JsonArray();
    Set<Country> adjacentCountries = country.getAdjacentCountries();
    if (adjacentCountries != null) {
      for (Country adjacentCountry : adjacentCountries) {
        adjacentCountriesArray.add(adjacentCountry.getCountryName().toString());
      }
    }
    jsonObject.add("adjacentCountries", adjacentCountriesArray);

    jsonObject.addProperty("continent", country.getContinent().getContinentName().toString());

    return jsonObject;
  }
}