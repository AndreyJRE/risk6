package com.unima.risk6.network.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.models.Player;
import java.lang.reflect.Type;

/**
 * This class is a custom serializer for the {@link com.unima.risk6.game.ai.bots.MediumBot} class in
 * the context of JSON serialization and deserialization.
 *
 * @author jferch
 */
public class MediumBotTypeAdapter implements
    JsonSerializer<MediumBot> {

  /**
   * This method is used to convert an {@link com.unima.risk6.game.ai.bots.MediumBot} object into
   * its JSON representation.
   *
   * @param mediumBot The MediumBot object to be serialized.
   * @param type      The specific genericized type of src.
   * @param context   Context for serialization that is also used to serialize src's fields.
   * @return A JsonElement corresponding to the specified MediumBot.
   */
  @Override
  public JsonElement serialize(MediumBot mediumBot, Type type, JsonSerializationContext context) {
    JsonObject jsonObject = context.serialize(mediumBot, Player.class).getAsJsonObject();
    return jsonObject;
  }

}