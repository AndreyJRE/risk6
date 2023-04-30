package com.unima.risk6.network.serialization;

import com.google.gson.*;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CountryName;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class CountryTypeAdapter implements JsonSerializer<Country>, JsonDeserializer<Country>{

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

  @Override

  public Country deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    CountryName countryName = context.deserialize(jsonObject.get("countryName"), CountryName.class);
    Country country = new Country(countryName);

    if (jsonObject.has("player")) {
      Player player = context.deserialize(jsonObject.get("player"), Player.class);
      country.setPlayer(player);
    }

    if (jsonObject.has("troops")) {
      int troops = jsonObject.get("troops").getAsInt();
      country.setTroops(troops);
    }

    if (jsonObject.has("adjacentCountries")) {
      JsonArray adjacentCountriesArray = jsonObject.getAsJsonArray("adjacentCountries");
      Set<Country> adjacentCountries = new HashSet<>();
      for (JsonElement adjacentCountryJson : adjacentCountriesArray) {
        Country adjacentCountry = context.deserialize(adjacentCountryJson, Country.class);
        adjacentCountries.add(adjacentCountry);
      }
      country.setAdjacentCountries(adjacentCountries);
    }

    if (jsonObject.has("continent")) {
      Continent continent = context.deserialize(jsonObject.get("continent"), Continent.class);
      country.setContinent(continent);
    }

    return country;
  }
}