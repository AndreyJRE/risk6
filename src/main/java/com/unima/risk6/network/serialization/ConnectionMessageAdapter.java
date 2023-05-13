package com.unima.risk6.network.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.unima.risk6.game.models.GameLobby;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.ServerLobby;
import com.unima.risk6.game.models.UserDto;
import com.unima.risk6.network.message.ConnectionMessage;
import com.unima.risk6.network.message.enums.ConnectionActions;
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
      case JOIN_GAME_LOBBY, CREATE_GAME_LOBBY, START_GAME, ACCEPT_JOIN_GAME_LOBBY,
          JOIN_BOT_GAME_LOBBY, ACCEPT_CREATE_LOBBY, REMOVE_BOT_FROM_LOBBY,
          START_TUTORIAL, CREATE_TUTORIAL_LOBBY, ACCEPT_TUTORIAL_CREATE_LOBBY -> {
        GameLobby gameLobby = context.deserialize(jsonObject.get("content"), GameLobby.class);
        message = new ConnectionMessage<>(connectionAction, statusCode, gameLobby);
      }
      case ACCEPT_JOIN_SERVER_LOBBY, ACCEPT_UPDATE_SERVER_LOBBY -> {
        ServerLobby serverLobby = context.deserialize(jsonObject.get("content"), ServerLobby.class);
        message = new ConnectionMessage<>(connectionAction, statusCode, serverLobby);
      }
      case ACCEPT_START_GAME -> {
        GameState gameState = context.deserialize(jsonObject.get("content"), GameState.class);
        message = new ConnectionMessage<>(connectionAction, statusCode, gameState);
      }
      case LEAVE_GAME_LOBBY, LEAVE_SERVER_LOBBY -> {
        //GameState gameState = context.deserialize(jsonObject.get("content");
        message = new ConnectionMessage<>(connectionAction, statusCode, "");
      }
      case DROP_USER_GAME_LOBBY, DROP_USER_SERVER_LOBBY, DROP_CREATE_GAME_LOBBY -> {
        String string = jsonObject.get("content").getAsString();
        message = new ConnectionMessage<String>(connectionAction, statusCode, string);
      }

      default -> throw new JsonParseException("Unsupported content type: " + connectionAction);
    }

    return message;
  }


}
