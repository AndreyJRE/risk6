package com.unima.risk6.network.serialization;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import java.lang.reflect.Type;

public class ReinforceTypeAdapter implements JsonSerializer<Reinforce>,
    JsonDeserializer<Reinforce> {

  private GameState gameState;

  public ReinforceTypeAdapter(GameState gameState) {
    this.gameState = gameState;
  }


  @Override
  public JsonElement serialize(Reinforce reinforce, Type typeOfSrc,
      JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("country", reinforce.getCountry().getCountryName().toString());
    jsonObject.addProperty("toAdd", reinforce.getToAdd());
    return jsonObject;
  }

  @Override
  public Reinforce deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    //TODO referencen tesetn
    Country country = gameState.getCountries().stream()
        .filter(x -> x.getCountryName().toString().equals(jsonObject.get("country").getAsString()))
        .findFirst()
        .get();
    int toAdd = jsonObject.get("toAdd").getAsInt();
    return new Reinforce(country, toAdd);
  }
}

