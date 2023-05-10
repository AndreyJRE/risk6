package com.unima.risk6.network.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.network.message.ContentType;
import com.unima.risk6.network.message.StandardMessage;
import java.lang.reflect.Type;

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
