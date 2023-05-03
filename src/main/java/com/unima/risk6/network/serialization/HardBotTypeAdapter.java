package com.unima.risk6.network.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.models.Player;

import java.lang.reflect.Type;

public class HardBotTypeAdapter implements JsonSerializer<HardBot>/*, JsonDeserializer<HardBot>*/ {

    @Override
    public JsonElement serialize(HardBot hardBot, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = context.serialize(hardBot, Player.class).getAsJsonObject();
        return jsonObject;
    }

  /*@Override
  public HardBot deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
    Player player = context.deserialize(jsonElement, Player.class);
    //TODO
    //HardBot hardBot = new HardBot(GameConfiguration.configureGame(users, bots));

    // Copy fields from the deserialized Player object to the HardBot object
    /*try {
      FieldUtils.copyFields(player, hardBot);
    } catch (IllegalAccessException e) {
      throw new JsonParseException("Error copying fields from Player to HardBot", e);
    }*/

    /*return hardBot;
  }*/
}