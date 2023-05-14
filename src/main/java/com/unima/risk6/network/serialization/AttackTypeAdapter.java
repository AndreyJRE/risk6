package com.unima.risk6.network.serialization;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * A custom {@link com.google.gson.JsonSerializer} and {@link com.google.gson.JsonDeserializer} for
 * the {@link com.unima.risk6.game.logic.Attack} class. This allows for custom serialization and
 * deserialization of Attack objects, which is useful for preserving specific information when
 * converting to and from JSON.
 *
 * @author jferch
 */
public class AttackTypeAdapter implements JsonSerializer<Attack>, JsonDeserializer<Attack> {

  private GameState gameState;

  /**
   * Constructs a new {@link com.unima.risk6.network.serialization.AttackTypeAdapter} with a
   * reference to a {@link com.unima.risk6.game.models.GameState}.
   *
   * @param gameState The game state, which is used for resolving references during
   *                  deserialization.
   */

  public AttackTypeAdapter(GameState gameState) {
    this.gameState = gameState;
  }

  /**
   * Default constructor for the {@link com.unima.risk6.network.serialization.AttackTypeAdapter} if
   * no {@link com.unima.risk6.game.models.GameState} is provided.
   */
  public AttackTypeAdapter() {

  }

  /**
   * Serializes an {@link com.unima.risk6.game.logic.Attack} object into a
   * {@link com.google.gson.JsonElement}.
   *
   * @param src       The source {@link com.unima.risk6.game.logic.Attack} object to be serialized.
   * @param typeOfSrc The specific generalized runtime type of src.
   * @param context   The context for serialization, used to serialize other objects as needed.
   * @return A JsonElement representing the serialized Attack data.
   */
  @Override
  public JsonElement serialize(Attack src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("attackingCountry",
        src.getAttackingCountry().getCountryName().toString());
    jsonObject.addProperty("defendingCountry",
        src.getDefendingCountry().getCountryName().toString());
    jsonObject.addProperty("troopNumber", src.getTroopNumber());
    jsonObject.addProperty("attackerLosses", src.getAttackerLosses());
    jsonObject.addProperty("defenderLosses", src.getDefenderLosses());
    jsonObject.add("attackDiceResult", context.serialize(src.getAttackDiceResult()));
    jsonObject.add("defendDiceResult", context.serialize(src.getDefendDiceResult()));
    jsonObject.add("hasConquered", context.serialize(src.getHasConquered()));

    return jsonObject;
  }

  /**
   * Deserializes a {@link com.google.gson.JsonElement} into an
   * {@link com.unima.risk6.game.logic.Attack} object.
   *
   * @param json    The JSON element being deserialized.
   * @param typeOfT The specific genericized runtime type of the object being deserialized.
   * @param context The context for deserialization, used to deserialize other objects as needed.
   * @return A deserialized {@link com.unima.risk6.game.logic.Attack} object.
   * @throws com.google.gson.JsonParseException If there is a problem parsing the JSON into an
   *                                            {@link com.unima.risk6.game.logic.Attack} object.
   */
  @Override
  public Attack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    Country attackingCountry = gameState.getCountries().stream()
        .filter(x -> x.getCountryName().toString()
            .equals(jsonObject.get("attackingCountry").getAsString()))
        .findFirst().get();
    Country defendingCountry = gameState.getCountries().stream()
        .filter(x -> x.getCountryName().toString()
            .equals(jsonObject.get("defendingCountry").getAsString()))
        .findFirst().get();
    int troopNumber = jsonObject.get("troopNumber").getAsInt();
    //TODO
    Attack attack = new Attack(attackingCountry, defendingCountry, troopNumber);
    attack.setAttackerLosses(jsonObject.get("attackerLosses").getAsInt());
    attack.setDefenderLosses(jsonObject.get("defenderLosses").getAsInt());
    attack.setHasConquered(jsonObject.get("hasConquered").getAsBoolean());
    TypeToken<ArrayList<Integer>> integerListTypeToken = new TypeToken<>() {
    };
    ArrayList<Integer> attackDiceResult = context.deserialize(jsonObject.get("attackDiceResult"),
        integerListTypeToken.getType());
    ArrayList<Integer> defendDiceResult = context.deserialize(jsonObject.get("defendDiceResult"),
        integerListTypeToken.getType());
    //Set values for attackDiceResult and defendDiceResult using reflection,
    //as they are final fields
    try {
      Field attackDiceResultField = Attack.class.getDeclaredField("attackDiceResult");
      Field defendDiceResultField = Attack.class.getDeclaredField("defendDiceResult");
      attackDiceResultField.setAccessible(true);
      defendDiceResultField.setAccessible(true);

      attackDiceResultField.set(attack, attackDiceResult);
      defendDiceResultField.set(attack, defendDiceResult);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new JsonParseException("Error deserializing Attack", e);
    }

    return attack;
  }
}
