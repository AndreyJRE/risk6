package com.unima.risk6.network.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.unima.risk6.network.message.ChatMessage;
import com.unima.risk6.network.message.enums.ContentType;
import java.lang.reflect.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A custom {@link com.google.gson.JsonDeserializer} for the
 * {@link com.unima.risk6.network.message.ChatMessage} class. This allows for custom deserialization
 * of ChatMessage objects, which is useful when converting from JSON.
 *
 * @author jferch
 */
public class ChatMessageTypeAdapter implements JsonDeserializer<ChatMessage> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageTypeAdapter.class);


  /**
   * Deserializes a {@link com.google.gson.JsonElement} into a
   * {@link com.unima.risk6.network.message.ChatMessage} object.
   *
   * @param jsonElement The JSON element being deserialized.
   * @param type        The specific genericized runtime type of the object being deserialized.
   * @param context     The context for deserialization, used to deserialize other objects as
   *                    needed.
   * @return A deserialized {@link com.unima.risk6.network.message.ChatMessage} object.
   * @throws com.google.gson.JsonParseException If there is a problem parsing the JSON into a
   *                                            {@link com.unima.risk6.network.message.ChatMessage}
   *                                            object.
   */
  @Override
  public ChatMessage deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();

    ContentType contentType = context.deserialize(jsonObject.get("contentType"), ContentType.class);

    ChatMessage message;
    switch (contentType) {
      case CHAT_MESSAGE -> {
        String content;
        try {
          content = jsonObject.get("content").getAsString();
        } catch (Exception e) {
          content = "";
          LOGGER.error("Tried to deserialize faulty ChatMessage: " + e);
        }
        message = new ChatMessage(content);
      }
      default -> throw new JsonParseException("Unsupported content type: " + contentType);
    }

    return message;
  }
}
