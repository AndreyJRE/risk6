package com.unima.risk6.network.serialization;

import com.google.gson.*;
import com.unima.risk6.game.logic.*;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.network.message.StandardMessage;
import com.unima.risk6.network.message.enums.ContentType;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class StandardMessageAdapter implements JsonDeserializer<StandardMessage> {

  @Override
  public StandardMessage deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    int statusCode = jsonObject.get("statusCode").getAsInt();
    ContentType contentType = context.deserialize(jsonObject.get("contentType"), ContentType.class);

    StandardMessage<?> message;
    switch (contentType) {
      case ATTACK -> {
        Attack attack = context.deserialize(jsonObject.get("content"), Attack.class);
        message = new StandardMessage<Attack>(attack, statusCode);
      }
      case FORTIFY -> {
        Fortify fortify = context.deserialize(jsonObject.get("content"), Fortify.class);
        message = new StandardMessage<Fortify>(fortify, statusCode);
      }
      case GAME_STATE -> {
        GameState gameState = context.deserialize(jsonObject.get("content"), GameState.class);
        message = new StandardMessage<GameState>(gameState, statusCode);
      }
      case REINFORCE -> {
        Reinforce reinforce = context.deserialize(jsonObject.get("content"), Reinforce.class);
        message = new StandardMessage<Reinforce>(reinforce, statusCode);
      }
      case HAND_IN -> {
        HandIn handin = context.deserialize(jsonObject.get("content"), HandIn.class);
        message = new StandardMessage<HandIn>(handin, statusCode);
      }
      case END_PHASE -> {
        EndPhase endPhase = context.deserialize(jsonObject.get("content"), EndPhase.class);
        message = new StandardMessage<EndPhase>(endPhase, statusCode);
      }
      case ORDER -> {
        HashMap<String, Integer> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.get("content").getAsJsonObject().entrySet()) {
          map.put(entry.getKey(), entry.getValue().getAsInt());
        }
        message = new StandardMessage<HashMap<String, Integer>>(map, statusCode);
      }
      case DEFAULT -> {
        String defaultContent = context.deserialize(jsonObject.get("content"), String.class);
        message = new StandardMessage<String>(defaultContent, statusCode);
      }
      default -> throw new JsonParseException("Unsupported content type: " + contentType);
    }

    message.setContentType(contentType);
    return message;
  }
}
