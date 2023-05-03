package com.unima.risk6.network.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.models.Player;
import java.lang.reflect.Type;

public class MediumBotTypeAdapter implements
    JsonSerializer<MediumBot> {

  @Override
  public JsonElement serialize(MediumBot mediumBot, Type type, JsonSerializationContext context) {
    JsonObject jsonObject = context.serialize(mediumBot, Player.class).getAsJsonObject();
    return jsonObject;
  }

}