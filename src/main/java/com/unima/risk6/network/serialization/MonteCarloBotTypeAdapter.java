package com.unima.risk6.network.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.ai.montecarlo.MonteCarloBot;
import com.unima.risk6.game.models.Player;
import java.lang.reflect.Type;

public class MonteCarloBotTypeAdapter implements JsonSerializer<MonteCarloBot> {

  @Override
  public JsonElement serialize(MonteCarloBot monteCarloBot, Type type,
      JsonSerializationContext context) {
    JsonObject jsonObject = context.serialize(monteCarloBot, Player.class).getAsJsonObject();
    return jsonObject;
  }

}