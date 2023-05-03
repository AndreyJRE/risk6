package com.unima.risk6.network.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CountryName;
import java.lang.reflect.Type;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountryTypeAdapter implements JsonSerializer<Country>, JsonDeserializer<Country> {

  private final static Logger LOGGER = LoggerFactory.getLogger(CountryTypeAdapter.class);
  private GameState gameState;

  public CountryTypeAdapter(GameState gameState) {
    this.gameState = gameState;
  }

  public CountryTypeAdapter() {
    this.gameState = null;
  }

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

    return jsonObject;
  }

  @Override

  public Country deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    CountryName countryName = context.deserialize(jsonObject.get("countryName"), CountryName.class);
    Country country;
    try {

      country = gameState.getCountries().stream()
          .filter(x -> x.getCountryName().equals(countryName)).findFirst().orElseThrow();
    } catch (NoSuchElementException e) {
      LOGGER.error("No such Country");
      country = new Country(CountryName.ALASKA);
    }

    if (jsonObject.has("player")) {
      Player player = context.deserialize(jsonObject.get("player"), Player.class);
      country.setPlayer(player);
    }

    if (jsonObject.has("troops")) {
      int troops = jsonObject.get("troops").getAsInt();
      country.setTroops(troops);
    }

    return country;
  }
}