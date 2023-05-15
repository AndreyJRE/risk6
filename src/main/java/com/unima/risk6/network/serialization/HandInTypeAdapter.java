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

/**
 * A custom {@link com.google.gson.JsonSerializer} and {@link com.google.gson.JsonDeserializer} for
 * the {@link com.unima.risk6.game.logic.HandIn} class. This allows for custom serialization and
 * deserialization of HandIn objects, which is useful for preserving specific information when
 * converting to and from JSON.
 *
 * @author jferch
 */
public class HandInTypeAdapter implements JsonSerializer<HandIn>, JsonDeserializer<HandIn> {

  /**
   * Serializes an {@link com.unima.risk6.game.logic.HandIn} object into a
   * {@link com.google.gson.JsonElement}.
   *
   * @param handIn    The source {@link com.unima.risk6.game.logic.HandIn} object to be serialized.
   * @param typeOfSrc The specific generalized runtime type of src.
   * @param context   The context for serialization, used to serialize other objects as needed.
   * @return A JsonElement representing the serialized HandIn data.
   */
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

  /**
   * Deserializes a {@link com.google.gson.JsonElement} into an
   * {@link com.unima.risk6.game.logic.HandIn} object.
   *
   * @param json    The JSON element being deserialized.
   * @param typeOfT The specific genericized runtime type of the object being deserialized.
   * @param context The context for deserialization, used to deserialize other objects as needed.
   * @return A deserialized {@link com.unima.risk6.game.logic.HandIn} object.
   * @throws com.google.gson.JsonParseException If there is a problem parsing the JSON into an
   *                                            {@link com.unima.risk6.game.logic.HandIn} object.
   */
  @Override
  public HandIn deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    JsonArray cardsArray = jsonObject.getAsJsonArray("cards");
    List<Card> cards = new ArrayList<>();
    for (JsonElement cardElement : cardsArray) {
      cards.add(context.deserialize(cardElement, Card.class));
    }

    return new HandIn(cards);
  }

}
