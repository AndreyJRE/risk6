package com.unima.risk6.network.serialization;

import com.google.gson.*;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import java.lang.reflect.Type;
import java.util.Random;

public class EasyBotTypeAdapter implements JsonSerializer<EasyBot>/*, JsonDeserializer<EasyBot>*/ {

  @Override
  public JsonElement serialize(EasyBot easyBot, Type type, JsonSerializationContext context) {
    JsonObject jsonObject = context.serialize(easyBot, Player.class).getAsJsonObject();
    return jsonObject;
  }

  /*@Override
  public EasyBot deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
    Player player = context.deserialize(jsonElement, Player.class);
    //TODO
    //EasyBot easyBot = new EasyBot(GameConfiguration.configureGame(users, bots));

    // Copy fields from the deserialized Player object to the EasyBot object
    /*try {
      FieldUtils.copyFields(player, easyBot);
    } catch (IllegalAccessException e) {
      throw new JsonParseException("Error copying fields from Player to EasyBot", e);
    }*/

    /*return easyBot;
  }*/
}
