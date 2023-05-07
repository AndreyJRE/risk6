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

public class EndPhaseTypeAdapter implements JsonSerializer<EndPhase>, JsonDeserializer<EndPhase> {

  @Override
  public JsonElement serialize(EndPhase endPhase, Type typeOfSrc,
      JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();

    jsonObject.add("phaseToEnd", context.serialize(endPhase.getPhaseToEnd(), GamePhase.class));

    return jsonObject;
  }

  @Override
  public EndPhase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    GamePhase phaseToEnd; // = context.deserialize(jsonObject.get("phaseToEnd"), GamePhase.class);
    switch (jsonObject.get("phaseToEnd").getAsString()) {
      case "REINFORCEMENT_PHASE" -> phaseToEnd = GamePhase.REINFORCEMENT_PHASE;
      case "ATTACK_PHASE" -> phaseToEnd = GamePhase.ATTACK_PHASE;
      case "FORTIFY_PHASE" -> phaseToEnd = GamePhase.FORTIFY_PHASE;
      case "NOT_ACTIVE" -> phaseToEnd = GamePhase.NOT_ACTIVE;
      case "CLAIM_PHASE" -> phaseToEnd = GamePhase.CLAIM_PHASE;
      case "ORDER_PHASE" -> phaseToEnd = GamePhase.ORDER_PHASE;
      default -> phaseToEnd = GamePhase.NOT_ACTIVE;
    }
    return new EndPhase(phaseToEnd);
  }
}
