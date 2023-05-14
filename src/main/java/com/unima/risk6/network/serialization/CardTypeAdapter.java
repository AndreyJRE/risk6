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

/**
 * A custom {@link com.google.gson.JsonSerializer} and {@link com.google.gson.JsonDeserializer} for
 * the {@link com.unima.risk6.game.models.Card} class. This allows for custom serialization and
 * deserialization of Card objects, which is useful for preserving specific information when
 * converting to and from JSON.
 *
 * @author jferch
 */
public class CardTypeAdapter implements JsonDeserializer<Card>, JsonSerializer<Card> {

  /**
   * Serializes a {@link com.unima.risk6.game.models.Card} object into a
   * {@link com.google.gson.JsonElement}.
   *
   * @param src       The source {@link com.unima.risk6.game.models.Card} object to be serialized.
   * @param typeOfSrc The specific generalized runtime type of src.
   * @param context   The context for serialization, used to serialize other objects as needed.
   * @return A {@link com.google.gson.JsonElement} representing the serialized
   * {@link com.unima.risk6.game.models.Card} data.
   */
  @Override
  public JsonElement serialize(Card src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.add("cardSymbol", context.serialize(src.getCardSymbol(), CardSymbol.class));
    jsonObject.addProperty("hasCountry", src.hasCountry());

    if (src.hasCountry()) {
      jsonObject.add("country", context.serialize(src.getCountry(), CountryName.class));
    }
    jsonObject.addProperty("id", src.getId());

    return jsonObject;
  }

  /**
   * Deserializes a {@link com.google.gson.JsonElement} into a
   * {@link com.unima.risk6.game.models.Card} object.
   *
   * @param json    The JSON element being deserialized.
   * @param typeOfT The specific generalized runtime type of the object being deserialized.
   * @param context The context for deserialization, used to deserialize other objects as needed.
   * @return A deserialized {@link com.unima.risk6.game.models.Card} object.
   * @throws com.google.gson.JsonParseException If there is a problem parsing the JSON into a
   *                                            {@link com.unima.risk6.game.models.Card} object.
   */
  @Override
  public Card deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    CardSymbol cardSymbol = context.deserialize(jsonObject.get("cardSymbol"), CardSymbol.class);
    boolean hasCountry = jsonObject.get("hasCountry").getAsBoolean();
    int id = jsonObject.get("id").getAsInt();

    if (hasCountry) {
      CountryName country = context.deserialize(jsonObject.get("country"), CountryName.class);
      return new Card(cardSymbol, country, id);
    } else {
      return new Card(cardSymbol, id);
    }
  }
}
