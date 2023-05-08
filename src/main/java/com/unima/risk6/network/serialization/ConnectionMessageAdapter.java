package com.unima.risk6.network.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.network.message.ConnectionActions;
import com.unima.risk6.network.message.ConnectionMessage;
import java.lang.reflect.Type;

public class ConnectionMessageAdapter implements JsonDeserializer<ConnectionMessage> {

  @Override
  public ConnectionMessage deserialize(JsonElement jsonElement, Type type,
      JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    int statusCode = jsonObject.get("statusCode").getAsInt();
    ConnectionActions connectionAction = context.deserialize(jsonObject.get("connectionActions"),
        ConnectionActions.class);

    ConnectionMessage<?> message = null;
    switch (connectionAction) {
      case JOIN_SERVER_LOBBY -> {
        UserDto userDto = context.deserialize(jsonObject.get("content"), UserDto.class);
        message = new ConnectionMessage<>(connectionAction, statusCode, userDto);
      }
      case JOIN_GAME_LOBBY, CREATE_GAME_LOBBY, START_GAME, ACCEPT_JOIN_LOBBY -> {
        GameLobby gameLobby = context.deserialize(jsonObject.get("content"), GameLobby.class);
        message = new ConnectionMessage<>(connectionAction, statusCode, gameLobby);
      }
      case ACCEPT_SERVER_LOBBY, ACCEPT_CREATE_LOBBY -> {
        ServerLobby serverLobby = context.deserialize(jsonObject.get("content"), ServerLobby.class);
        message = new ConnectionMessage<>(connectionAction, statusCode, serverLobby);
      }
      case ACCEPT_START_GAME -> {

      }

      default -> throw new JsonParseException("Unsupported content type: " + connectionAction);
    }

    return message;
  }


}
