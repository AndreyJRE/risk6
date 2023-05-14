package com.unima.risk6.network.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.ai.tutorial.TutorialBot;
import com.unima.risk6.game.models.Player;
import java.lang.reflect.Type;

/**
 * This class is a custom serializer for the {@link com.unima.risk6.game.ai.tutorial.TutorialBot}
 * class in the context of JSON serialization and deserialization.
 *
 * @author eameri
 */
public class TutorialBotTypeAdapter implements JsonSerializer<TutorialBot> {

  /**
   * This method is used to convert an {@link com.unima.risk6.game.ai.tutorial.TutorialBot} object
   * into its JSON representation.
   *
   * @param tutorialBot The TutorialBot object to be serialized.
   * @param type        The specific genericized type of src.
   * @param context     Context for serialization that is also used to serialize src's fields.
   * @return A JsonElement corresponding to the specified TutorialBot.
   */
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