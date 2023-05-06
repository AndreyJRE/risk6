package com.unima.risk6.network.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.models.Card;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HandInTypeAdapter implements JsonSerializer<HandIn>, JsonDeserializer<HandIn> {

  @Override
  public JsonElement serialize(HandIn handIn, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();
    JsonArray cardsArray = new JsonArray();

    for (Card card : handIn.getCards()) {
      cardsArray.add(context.serialize(card));
    }

    jsonObject.add("cards", cardsArray);

    return jsonObject;
  }

  @Override
  public HandIn deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    JsonArray cardsArray = jsonObject.getAsJsonArray("cards");
    List<Card> cards = new ArrayList<>();
    //TODO Vielleicht Referenzen????
    for (JsonElement cardElement : cardsArray) {
      cards.add(context.deserialize(cardElement, Card.class));
    }

    return new HandIn(cards);
  }

}
