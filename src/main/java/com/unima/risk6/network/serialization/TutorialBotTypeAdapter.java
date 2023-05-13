package com.unima.risk6.network.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.ai.tutorial.TutorialBot;
import com.unima.risk6.game.models.Player;
import java.lang.reflect.Type;

public class TutorialBotTypeAdapter implements JsonSerializer<TutorialBot> {

  @Override
  public JsonElement serialize(TutorialBot tutorialBot, Type type,
      JsonSerializationContext context) {
    JsonObject jsonObject = context.serialize(tutorialBot, Player.class).getAsJsonObject();
    JsonArray deterministicClaims = new JsonArray();
    tutorialBot.getDeterministicClaims().forEach(
        x -> deterministicClaims.add(context.serialize(x))
    );
    jsonObject.add("deterministicClaims", deterministicClaims);
    return jsonObject;
  }

}