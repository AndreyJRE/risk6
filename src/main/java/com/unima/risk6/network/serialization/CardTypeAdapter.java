package com.unima.risk6.network.serialization;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import java.lang.reflect.Type;

public class CardTypeAdapter implements JsonDeserializer<Card>, JsonSerializer<Card> {

  @Override
  public JsonElement serialize(Card src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.add("cardSymbol", context.serialize(src.getCardSymbol(), CardSymbol.class));
    jsonObject.addProperty("hasCountry", src.isHasCountry());

    if (src.isHasCountry()) {
      jsonObject.add("country", context.serialize(src.getCountry(), CountryName.class));
    }

    return jsonObject;
  }

  @Override
  public Card deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    CardSymbol cardSymbol = context.deserialize(jsonObject.get("cardSymbol"), CardSymbol.class);
    boolean hasCountry = jsonObject.get("hasCountry").getAsBoolean();

    if (hasCountry) {
      CountryName country = context.deserialize(jsonObject.get("country"), CountryName.class);
      return new Card(cardSymbol, country);
    } else {
      return new Card(cardSymbol);
    }
  }
}
