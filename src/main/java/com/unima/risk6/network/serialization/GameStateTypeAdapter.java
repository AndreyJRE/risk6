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
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Move;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A JsonSerializer and JsonDeserializer implementation for
 * {@link com.unima.risk6.game.models.GameState} objects. This class defines how GameState objects
 * are converted to and from their JSON representations.
 *
 * @author jferch
 */
public class GameStateTypeAdapter implements JsonSerializer<GameState>,
    JsonDeserializer<GameState> {

  private GameState gameState;

  /**
   * Constructor for a GameStateTypeAdapter with a given GameState for context.
   *
   * @param gameState The GameState object to be used for context during serialization/deserialization.
   */
  public GameStateTypeAdapter(GameState gameState) {
    this.gameState = gameState;
  }

  /**
   * Default constructor for a GameStateTypeAdapter.
   */
  public GameStateTypeAdapter() {
  }

  /**
   * Serializes a GameState object to its corresponding JSON representation.
   *
   * @param gameState The GameState object to be serialized.
   * @param typeOfSrc The actual generic type of the source object.
   * @param context The context for serialization.
   * @return A JsonElement corresponding to the specified GameState.
   */
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
    jsonObject.add("currentPlayer",
        context.serialize(context.serialize(gameState.getCurrentPlayer())));

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
    jsonObject.addProperty("chatEnabled", gameState.isChatEnabled());

    return jsonObject;
  }

  /**
   * Deserializes a JsonElement into a GameState object.
   *
   * @param json    The JsonElement being deserialized.
   * @param typeOfT The type of the Object to deserialize to.
   * @param context The context for deserialization.
   * @return A GameState object corresponding to the specified JsonElement.
   * @throws JsonParseException if json is not in the expected format of GameState.
   */
  @Override
  public GameState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    JsonArray activePlayersJsonArray = jsonObject.getAsJsonArray("activePlayers");
    activePlayersJsonArray.forEach(x -> {
      Player p = context.deserialize(x, Player.class);
      gameState.getActivePlayers().add(p);
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
    boolean chatEnabled = jsonObject.get("chatEnabled").getAsBoolean();

    gameState.setChatEnabled(chatEnabled);
    gameState.setNumberOfHandIns(numberOfHandIns);

    JsonArray jsonArray = jsonObject.get("lastMoves").getAsJsonArray();
    for (JsonElement element : jsonArray) {

      if (element.getAsJsonObject().has("attackingCountry")) {
        //Attack
        gameState.getLastMoves().add(context.deserialize(element, Attack.class));
      } else if (element.getAsJsonObject().has("phaseToEnd")) {
        //EndPhase
        gameState.getLastMoves().add(context.deserialize(element, EndPhase.class));
      } else if (element.getAsJsonObject().has("outgoing")) {
        //Fortify
        gameState.getLastMoves().add(context.deserialize(element, Fortify.class));
      } else if (element.getAsJsonObject().has("cards")) {
        //HandIn
        gameState.getLastMoves().add(context.deserialize(element, HandIn.class));
      } else if (element.getAsJsonObject().has("toAdd")) {
        //Reinforce
        gameState.getLastMoves().add(context.deserialize(element, Reinforce.class));
      }

    }
    ArrayList<Card> currentDeck = gameState.getDeck().getDeckCards();
    currentDeck.addAll(deckArray);

    gameState.setGameOver(isGameOver);

    gameState.setCurrentPlayer(context.deserialize(jsonObject.get("currentPlayer"), Player.class));

    return gameState;
  }
}
