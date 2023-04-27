package com.unima.risk6.network.serialization;

import com.google.gson.*;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.logic.Move;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Deck;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.network.message.ContentType;
import com.unima.risk6.network.message.StandardMessage;

import java.lang.reflect.Type;

public class StandardMessageAdapter implements JsonDeserializer<StandardMessage> {
    @Override
    public StandardMessage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int statusCode = jsonObject.get("statusCode").getAsInt();
        ContentType contentType = context.deserialize(jsonObject.get("contentType"), ContentType.class);

        StandardMessage<?> message;
        switch (contentType) {
            case ATTACK:
                Attack attack = context.deserialize(jsonObject.get("content"), Attack.class);
                message = new StandardMessage<Attack>(attack, statusCode);
                break;
            case FORTIFY:
                Fortify fortify = context.deserialize(jsonObject.get("content"), Fortify.class);
                message = new StandardMessage<Fortify>(fortify, statusCode);
                break;
            case GAMESTATE:
                GameState gameState = context.deserialize(jsonObject.get("content"), GameState.class);
                message = new StandardMessage<GameState>(gameState, statusCode);
                break;
            case MOVE:
                Move move = context.deserialize(jsonObject.get("content"), Move.class);
                message = new StandardMessage<Move>(move, statusCode);
                break;
            case REINFORCE:
                Reinforce reinforce = context.deserialize(jsonObject.get("content"), Reinforce.class);
                message = new StandardMessage<Reinforce>(reinforce, statusCode);
                break;
            case DEFAULT:
                String defaultContent = context.deserialize(jsonObject.get("content"), String.class);
                message = new StandardMessage<String>(defaultContent, statusCode);
                break;
            default:
                throw new JsonParseException("Unsupported content type: " + contentType);
        }

        message.setContentType(contentType);
        return message;
    }
}
