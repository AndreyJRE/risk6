package com.unima.risk6.network.serialization;

import com.google.gson.*;
import com.unima.risk6.game.logic.Dice;
import com.unima.risk6.game.logic.GameState;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;

public class GameStateTypeAdapter implements JsonSerializer<GameState>, JsonDeserializer<GameState> {

    @Override
    public JsonElement serialize(GameState gameState, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        JsonArray countriesJsonArray = new JsonArray();
        gameState.getCountries()
                .forEach(x -> countriesJsonArray.add(context.serialize(x))
                );
        jsonObject.add("countries", countriesJsonArray);

        JsonArray continentsJsonArray = new JsonArray();
        gameState.getContinents()
                .forEach(x -> continentsJsonArray.add(context.serialize(x))
                );
        jsonObject.add("continents", continentsJsonArray);

        JsonArray activePlayersJsonArray = new JsonArray();
        gameState.getActivePlayers()
                .forEach(x -> activePlayersJsonArray.add(context.serialize(x))
                );
        jsonObject.add("activePlayers", activePlayersJsonArray);

        jsonObject.add("currentPlayer", context.serialize(gameState.getCurrentPlayer()));
        jsonObject.add("dice", context.serialize(gameState.getDice()));
        jsonObject.addProperty("numberOfHandIns", gameState.getNumberOfHandIns());
        jsonObject.addProperty("currentPhase", gameState.getCurrentPhase().name());

        return jsonObject;
    }

    @Override
    public GameState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Set<Country> countries = context.deserialize(jsonObject.get("countries"), Set.class);
        Set<Continent> continents = context.deserialize(jsonObject.get("continents"), Set.class);
        Queue<Player> activePlayers = context.deserialize(jsonObject.get("activePlayers"), Queue.class);

        GameState gameState = new GameState(countries, continents, activePlayers);

        gameState.setCurrentPlayer(context.deserialize(jsonObject.get("currentPlayer"), Player.class));
        //gameState.setDice(context.deserialize(jsonObject.get("dice"), Dice.class));
        //gameState.setNumberOfHandIns(jsonObject.get("numberOfHandIns").getAsInt());
        gameState.setCurrentPhase(GamePhase.valueOf(jsonObject.get("currentPhase").getAsString()));

        return gameState;
    }
}
