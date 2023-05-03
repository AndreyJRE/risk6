package com.unima.risk6.network.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.models.Player;
import java.lang.reflect.Type;

public class HardBotTypeAdapter implements JsonSerializer<HardBot> {

  @Override
  public JsonElement serialize(HardBot hardBot, Type type, JsonSerializationContext context) {
    JsonObject jsonObject = context.serialize(hardBot, Player.class).getAsJsonObject();
    return jsonObject;
  }

}