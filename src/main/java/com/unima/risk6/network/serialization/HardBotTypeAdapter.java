package com.unima.risk6.network.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.models.Player;
import java.lang.reflect.Type;

/**
 * This class is a custom serializer for the {@link com.unima.risk6.game.ai.bots.HardBot} class in
 * the context of JSON serialization and deserialization.
 *
 * @author jferch
 */
public class HardBotTypeAdapter implements JsonSerializer<HardBot> {

  /**
   * This method is used to convert an {@link com.unima.risk6.game.ai.bots.HardBot} object into its
   * JSON representation.
   *
   * @param hardBot The HardBot object to be serialized.
   * @param type    The specific genericized type of src.
   * @param context Context for serialization that is also used to serialize src's fields.
   * @return A JsonElement corresponding to the specified HardBot.
   */
  @Override
  public JsonElement serialize(HardBot hardBot, Type type, JsonSerializationContext context) {
    JsonObject jsonObject = context.serialize(hardBot, Player.class).getAsJsonObject();
    return jsonObject;
  }

}