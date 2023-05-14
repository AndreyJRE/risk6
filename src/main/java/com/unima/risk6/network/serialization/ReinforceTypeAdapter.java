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

/**
 * A custom {@link com.google.gson.JsonSerializer} and {@link com.google.gson.JsonDeserializer} for
 * the {@link com.unima.risk6.game.logic.Reinforce} class. This allows for custom serialization and
 * deserialization of Reinforce objects, which is useful for preserving specific information when
 * converting to and from JSON.
 *
 * @author jferch
 */
public class ReinforceTypeAdapter implements JsonSerializer<Reinforce>,
    JsonDeserializer<Reinforce> {

  private GameState gameState;

  /**
   * Constructs a new {@link com.unima.risk6.network.serialization.ReinforceTypeAdapter} with a reference to a {@link com.unima.risk6.game.models.GameState}.
   *
   * @param gameState The game state, which is used for resolving references during deserialization.
   */

  public ReinforceTypeAdapter(GameState gameState) {
    this.gameState = gameState;
  }

  /**
   * Default constructor for the {@link com.unima.risk6.network.serialization.ReinforceTypeAdapter} if no {@link com.unima.risk6.game.models.GameState} is provided.
   */
  public ReinforceTypeAdapter() {
  }

  /**
   * Serializes an {@link com.unima.risk6.game.logic.Reinforce} object into a {@link com.google.gson.JsonElement}.
   *
   * @param reinforce The source {@link com.unima.risk6.game.logic.Reinforce} object to be serialized.
   * @param typeOfSrc The specific generalized runtime type of src.
   * @param context The context for serialization, used to serialize other objects as needed.
   * @return A {@link com.google.gson.JsonElement} representing the serialized {@link com.unima.risk6.game.logic.Reinforce} data.
   */
  @Override
  public JsonElement serialize(Reinforce reinforce, Type typeOfSrc,
      JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("country", reinforce.getCountry().getCountryName().toString());
    jsonObject.addProperty("toAdd", reinforce.getToAdd());
    return jsonObject;
  }

  /**
   * Deserializes a {@link com.google.gson.JsonElement} into an {@link com.unima.risk6.game.logic.Reinforce} object.
   *
   * @param json The JSON element being deserialized.
   * @param typeOfT The specific genericized runtime type of the object being deserialized.
   * @param context The context for deserialization, used to deserialize other objects as needed.
   * @return A deserialized {@link com.unima.risk6.game.logic.Reinforce} object.
   * @throws com.google.gson.JsonParseException If there is a problem parsing the JSON into an {@link com.unima.risk6.game.logic.Reinforce} object.
   */
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

