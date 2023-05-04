package com.unima.risk6.network.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.unima.risk6.game.logic.Move;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class GameStateTypeAdapter implements JsonSerializer<GameState>,
    JsonDeserializer<GameState> {

  private static final String COUNTRIES_JSON_PATH = "/com/unima/risk6/json/countries.json";
  private GameState gameState;

  public GameStateTypeAdapter(GameState gameState) {
    this.gameState = gameState;
  }

  public GameStateTypeAdapter() {
  }

  //TODO nur Continents Set Serialisieren und bei deserialiierung aus continents countries generieren.
  @Override
  public JsonElement serialize(GameState gameState, Type typeOfSrc,
      JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();

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

    //to ensure that the current player and the players in the activePlayers List have the right reference
    jsonObject.add("currentPlayer", context.serialize(gameState.getCurrentPlayer().hashCode()));

    jsonObject.addProperty("numberOfHandIns", gameState.getNumberOfHandIns());

    JsonArray moveArray = new JsonArray();
    for (Move move : gameState.getLastMoves()) {
      moveArray.add(context.serialize(move));
    }

    jsonObject.add("lastMoves", moveArray);

    JsonArray deckJsonArray = new JsonArray();
    gameState.getDeck().getDeckCards()
        .forEach(x -> deckJsonArray.add(context.serialize(x))
        );
    jsonObject.add("deck", deckJsonArray);
    jsonObject.addProperty("isGameOver", gameState.isGameOver());

    return jsonObject;
  }

  @Override
  public GameState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    int currentPlayerReference = jsonObject.get("currentPlayer").getAsInt();

    JsonArray activePlayersJsonArray = jsonObject.getAsJsonArray("activePlayers");
    activePlayersJsonArray.forEach(x -> {
      Player p = context.deserialize(x, Player.class);
      gameState.getActivePlayers().add(p);
      if (x.getAsJsonObject().get("hashCode").getAsInt() == currentPlayerReference) {
        gameState.setCurrentPlayer(p);
      }
    });
    ArrayList<Player> lostPlayers = context.deserialize(jsonObject.get("lostPlayers"),
        new TypeToken<ArrayList<Player>>() {
        }.getType());

    gameState.getLostPlayers().addAll(lostPlayers);

    int numberOfHandIns = jsonObject.get("numberOfHandIns").getAsInt();
    ArrayList<Card> deckArray = context.deserialize(jsonObject.get("deck"),
        new TypeToken<ArrayList<Card>>() {
        }.getType());
    boolean isGameOver = jsonObject.get("isGameOver").getAsBoolean();

    for (int j = 0; j < numberOfHandIns; j++) {
      gameState.setNumberOfHandIns();
    }
    //TODO Testen, ob reihenfolge stimmt
    JsonArray jsonArray = jsonObject.get("lastMoves").getAsJsonArray();
    for (JsonElement element : jsonArray) {
      Move move = context.deserialize(element, Move.class);
      gameState.getLastMoves().add(move);
    }
    ArrayList<Card> currentDeck = gameState.getDeck().getDeckCards();
    currentDeck.addAll(deckArray);

    gameState.setGameOver(isGameOver);

    //gameState.setCurrentPlayer(context.deserialize(jsonObject.get("currentPlayer"), Player
    // .class));
    //gameState.setDice(context.deserialize(jsonObject.get("dice"), Dice.class));
    //gameState.setNumberOfHandIns(jsonObject.get("numberOfHandIns").getAsInt());
    //gameState.setCurrentPhase(GamePhase.valueOf(jsonObject.get("currentPhase").getAsString()));

    return gameState;
  }
}
