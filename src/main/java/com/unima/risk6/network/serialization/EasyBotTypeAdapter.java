package com.unima.risk6.network.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.models.Player;
import java.lang.reflect.Type;

/**
 * This class is a custom serializer for the {@link com.unima.risk6.game.ai.bots.EasyBot} class in
 * the context of JSON serialization and deserialization.
 *
 * @author jferch
 */
public class EasyBotTypeAdapter implements JsonSerializer<EasyBot> {

  /**
   * This method is used to convert an {@link com.unima.risk6.game.ai.bots.EasyBot} object into its
   * JSON representation.
   *
   * @param easyBot The EasyBot object to be serialized.
   * @param type    The specific genericized type of src.
   * @param context Context for serialization that is also used to serialize src's fields.
   * @return A JsonElement corresponding to the specified EasyBot.
   */
  @Override
  public JsonElement serialize(EasyBot easyBot, Type type, JsonSerializationContext context) {
    JsonObject jsonObject = context.serialize(easyBot, Player.class).getAsJsonObject();
    return jsonObject;
  }

}
