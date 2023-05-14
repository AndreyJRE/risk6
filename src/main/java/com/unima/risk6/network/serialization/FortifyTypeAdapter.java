package com.unima.risk6.network.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import java.lang.reflect.Type;

/**
 * A custom {@link com.google.gson.JsonSerializer} and {@link com.google.gson.JsonDeserializer} for
 * the {@link com.unima.risk6.game.logic.Fortify} class. This allows for custom serialization and
 * deserialization of Fortify objects, which is useful for preserving specific information when
 * converting to and from JSON.
 *
 * @author jferch
 */
public class FortifyTypeAdapter implements JsonSerializer<Fortify>, JsonDeserializer<Fortify> {

  private GameState gameState;

  /**
   * Constructs a new {@link com.unima.risk6.network.serialization.FortifyTypeAdapter} with a
   * reference to a {@link com.unima.risk6.game.models.GameState}.
   *
   * @param gameState The game state, which is used for resolving references during
   *                  deserialization.
   */
  public FortifyTypeAdapter(GameState gameState) {
    this.gameState = gameState;
  }

  /**
   * Default constructor for the {@link com.unima.risk6.network.serialization.FortifyTypeAdapter} if
   * no {@link com.unima.risk6.game.models.GameState} is provided.
   */
  public FortifyTypeAdapter() {

  }

  /**
   * Serializes an {@link com.unima.risk6.game.logic.Fortify} object into a
   * {@link com.google.gson.JsonElement}.
   *
   * @param fortify   The source {@link com.unima.risk6.game.logic.Fortify} object to be
   *                  serialized.
   * @param typeOfSrc The specific generalized runtime type of src.
   * @param context   The context for serialization, used to serialize other objects as needed.
   * @return A {@link com.google.gson.JsonElement} representing the serialized
   * {@link com.unima.risk6.game.logic.Fortify} data.
   */
  @Override
  public JsonElement serialize(Fortify fortify, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("outgoing", fortify.getOutgoing().getCountryName().toString());
    jsonObject.addProperty("incoming", fortify.getIncoming().getCountryName().toString());
    //jsonObject.add("outgoing", context.serialize(fortify.getOutgoing()));
    //jsonObject.add("incoming", context.serialize(fortify.getIncoming()));
    jsonObject.addProperty("troopsToMove", fortify.getTroopsToMove());
    return jsonObject;
  }

  /**
   * Deserializes a {@link com.google.gson.JsonElement} into an
   * {@link com.unima.risk6.game.logic.Fortify} object.
   *
   * @param json    The JSON element being deserialized.
   * @param typeOfT The specific genericized runtime type of the object being deserialized.
   * @param context The context for deserialization, used to deserialize other objects as needed.
   * @return A deserialized {@link com.unima.risk6.game.logic.Fortify} object.
   * @throws com.google.gson.JsonParseException If there is a problem parsing the JSON into an
   *                                            {@link com.unima.risk6.game.logic.Fortify} object.
   */
  @Override
  public Fortify deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    //TODO referenzen testen
    Country incoming = gameState.getCountries().stream()
        .filter(x -> x.getCountryName().toString().equals(jsonObject.get("incoming").getAsString()))
        .findFirst()
        .get();
    Country outgoing = gameState.getCountries().stream()
        .filter(x -> x.getCountryName().toString().equals(jsonObject.get("outgoing").getAsString()))
        .findFirst()
        .get();
    //Country outgoing = context.deserialize(jsonObject.get("outgoing"), Country.class);
    //Country incoming = context.deserialize(jsonObject.get("incoming"), Country.class);
    int troopsToMove = jsonObject.get("troopsToMove").getAsInt();
    return new Fortify(outgoing, incoming, troopsToMove);
  }
}
