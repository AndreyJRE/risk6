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

public class ChatMessageTypeAdapter implements JsonDeserializer<ChatMessage> {

  private final static Logger LOGGER = LoggerFactory.getLogger(ChatMessageTypeAdapter.class);

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
