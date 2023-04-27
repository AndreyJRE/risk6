package com.unima.risk6.network.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.unima.risk6.game.configurations.CountriesConfiguration;
import com.unima.risk6.game.configurations.PlayersConfiguration;
import com.unima.risk6.game.logic.Dice;
import com.unima.risk6.game.logic.Move;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Deck;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.GamePhase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;

public class GameStateTypeAdapter implements JsonSerializer<GameState>, JsonDeserializer<GameState> {
    private static final String COUNTRIES_JSON_PATH = "/com/unima/risk6/json/countries.json";
    //TODO nur Continents Set Serialisieren und bei deserialiierung aus continents countries generieren.
    @Override
    public JsonElement serialize(GameState gameState, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        /*
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
        */
        JsonArray activePlayersJsonArray = new JsonArray();
        gameState.getActivePlayers()
                .forEach(x -> activePlayersJsonArray.add(context.serialize(x))
                );
        jsonObject.add("activePlayers", activePlayersJsonArray);
        JsonArray lostPlayersJsonArray = new JsonArray();
        gameState.getLostPlayers()
            .forEach(x -> lostPlayersJsonArray.add(context.serialize(x))
            );
        jsonObject.add("lostPlayers", lostPlayersJsonArray);

        jsonObject.add("currentPlayer", context.serialize(gameState.getCurrentPlayer()));

        //TODO jsonObject.add("dice", context.serialize(gameState.getDice()));
        jsonObject.addProperty("numberOfHandIns", gameState.getNumberOfHandIns());
        JsonArray lastMovesJsonArray = new JsonArray();
        gameState.getLastMoves()
            .forEach(x -> lastMovesJsonArray.add(context.serialize(x))
            );
        jsonObject.add("lastMoves", lastMovesJsonArray);
        JsonArray deckJsonArray = new JsonArray();
        gameState.getDeck().getDeckCards()
            .forEach(x -> deckJsonArray.add(context.serialize(x))
            );
        jsonObject.add("deck", deckJsonArray);
        jsonObject.addProperty("isGameOver", gameState.isGameOver());
        //jsonObject.addProperty("currentPhase", gameState.getCurrentPhase().name());

        return jsonObject;
    }

    @Override
    public GameState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        //Set<Country> countries = context.deserialize(jsonObject.get("countries"), Set.class);
        //Set<Continent> continents = context.deserialize(jsonObject.get("continents"), Set.class);
        //TODO Bots und Players unterscheiden
        Queue<Player> activePlayers = context.deserialize(jsonObject.get("activePlayers"),
            new TypeToken<Queue<Player>>(){}.getType());
        ArrayList<Player> lostPlayers = context.deserialize(jsonObject.get("lostPlayers"),
            new TypeToken<ArrayList<Player>>(){}.getType());
        Player currentPlayer = context.deserialize(jsonObject.get("currentPlayer"), Player.class);
        int numberOfHandIns = jsonObject.get("numberOfHandIns").getAsInt();
        ArrayList<Move> lastMoves = context.deserialize(jsonObject.get("lastMoves"),
            new TypeToken<ArrayList<Move>>(){}.getType());
        ArrayList<Card> deckArray = context.deserialize(jsonObject.get("deck"),
            new TypeToken<ArrayList<Card>>(){}.getType());
        boolean isGameOver = jsonObject.get("isGameOver").getAsBoolean();


        //TODO veränderte Länder aus Players Liste einfügen
        CountriesConfiguration countriesConfiguration = new CountriesConfiguration(COUNTRIES_JSON_PATH);
        countriesConfiguration.configureCountriesAndContinents();
        Set<Country> countries = countriesConfiguration.getCountries();
        Set<Continent> continents = countriesConfiguration.getContinents();

        GameState gameState =  new GameState(countries, continents, activePlayers);

        //TODO Funktioniert die referenz?
        ArrayList<Player> currentLostPlayers = gameState.getLostPlayers();
        currentLostPlayers = lostPlayers;

        gameState.setCurrentPlayer(currentPlayer);

        for(int j = 0; j < numberOfHandIns; j++){
            gameState.setNumberOfHandIns();
        }

        ArrayList<Move> currentLastMoves = gameState.getLastMoves();
        currentLastMoves = lastMoves;

        ArrayList<Card> currentDeck = gameState.getDeck().getDeckCards();
        currentDeck.addAll(deckArray);
        //TODO isGameOver setzten

        //gameState.setCurrentPlayer(context.deserialize(jsonObject.get("currentPlayer"), Player
        // .class));
        //gameState.setDice(context.deserialize(jsonObject.get("dice"), Dice.class));
        //gameState.setNumberOfHandIns(jsonObject.get("numberOfHandIns").getAsInt());
        //gameState.setCurrentPhase(GamePhase.valueOf(jsonObject.get("currentPhase").getAsString()));

        return gameState;
    }
}
