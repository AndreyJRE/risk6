package com.unima.risk6.network.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import java.lang.reflect.Type;
import java.util.Set;

public class ContinentTypeAdapter implements JsonSerializer<Continent> {

  @Override
  public JsonElement serialize(Continent continent, Type typeOfSrc,
      JsonSerializationContext context) {
    if (continent == null) {
      return null;
    }

    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("continentName", continent.getContinentName().toString());
    jsonObject.addProperty("bonusTroops", continent.getBonusTroops());

    JsonArray countriesArray = new JsonArray();
    Set<Country> countries = continent.getCountries();
    if (countries != null) {
      for (Country country : countries) {
        countriesArray.add(context.serialize(country, Country.class));
      }
      jsonObject.add("countries", countriesArray);
    }

    return jsonObject;
  }
}
