package com.unima.risk6.network.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.models.enums.GamePhase;
import java.lang.reflect.Type;

/**
 * A custom {@link com.google.gson.JsonSerializer} and {@link com.google.gson.JsonDeserializer} for
 * the {@link com.unima.risk6.game.logic.EndPhase} class. This allows for custom serialization and
 * deserialization of EndPhase objects, which is useful for preserving specific information when
 * converting to and from JSON.
 *
 * @author jferch
 */
public class EndPhaseTypeAdapter implements JsonSerializer<EndPhase>, JsonDeserializer<EndPhase> {

  /**
   * Serializes an {@link com.unima.risk6.game.logic.EndPhase} object into a
   * {@link com.google.gson.JsonElement}.
   *
   * @param endPhase  The source {@link com.unima.risk6.game.logic.EndPhase} object to be
   *                  serialized.
   * @param typeOfSrc The specific generalized runtime type of src.
   * @param context   The context for serialization, used to serialize other objects as needed.
   * @return A JsonElement representing the serialized EndPhase data.
   */
  @Override
  public JsonElement serialize(EndPhase endPhase, Type typeOfSrc,
      JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();

    jsonObject.add("phaseToEnd", context.serialize(endPhase.getPhaseToEnd(), GamePhase.class));

    return jsonObject;
  }

  /**
   * Deserializes a {@link com.google.gson.JsonElement} into an
   * {@link com.unima.risk6.game.logic.EndPhase} object.
   *
   * @param json    The JSON element being deserialized.
   * @param typeOfT The specific genericized runtime type of the object being deserialized.
   * @param context The context for deserialization, used to deserialize other objects as needed.
   * @return A deserialized {@link com.unima.risk6.game.logic.EndPhase} object.
   * @throws com.google.gson.JsonParseException If there is a problem parsing the JSON into an
   *                                            {@link com.unima.risk6.game.logic.EndPhase} object.
   */
  @Override
  public EndPhase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    GamePhase phaseToEnd; // = context.deserialize(jsonObject.get("phaseToEnd"), GamePhase.class);
    switch (jsonObject.get("phaseToEnd").getAsString()) {
      case "REINFORCEMENT_PHASE" -> phaseToEnd = GamePhase.REINFORCEMENT_PHASE;
      case "ATTACK_PHASE" -> phaseToEnd = GamePhase.ATTACK_PHASE;
      case "FORTIFY_PHASE" -> phaseToEnd = GamePhase.FORTIFY_PHASE;
      case "CLAIM_PHASE" -> phaseToEnd = GamePhase.CLAIM_PHASE;
      case "ORDER_PHASE" -> phaseToEnd = GamePhase.ORDER_PHASE;
      default -> phaseToEnd = GamePhase.NOT_ACTIVE;
    }
    return new EndPhase(phaseToEnd);
  }
}
