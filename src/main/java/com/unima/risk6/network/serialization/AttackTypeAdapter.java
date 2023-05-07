package com.unima.risk6.network.serialization;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class AttackTypeAdapter implements JsonSerializer<Attack>, JsonDeserializer<Attack> {

  private GameState gameState;

  public AttackTypeAdapter(GameState gameState) {
    this.gameState = gameState;
  }

  public AttackTypeAdapter() {

  }


  @Override
  public JsonElement serialize(Attack src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("attackingCountry",
        src.getAttackingCountry().getCountryName().toString());
    jsonObject.addProperty("defendingCountry",
        src.getDefendingCountry().getCountryName().toString());
    //jsonObject.add("attackingCountry", context.serialize(src.getAttackingCountry(), Country.class));
    //jsonObject.add("defendingCountry", context.serialize(src.getDefendingCountry(), Country.class));
    jsonObject.addProperty("troopNumber", src.getTroopNumber());
    jsonObject.addProperty("attackerLosses", src.getAttackerLosses());
    jsonObject.addProperty("defenderLosses", src.getDefenderLosses());
    jsonObject.add("attackDiceResult", context.serialize(src.getAttackDiceResult()));
    jsonObject.add("defendDiceResult", context.serialize(src.getDefendDiceResult()));

    return jsonObject;
  }

  @Override
  public Attack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    //TODO referenzen testen
    /*Country attackingCountry = context.deserialize(jsonObject.get("attackingCountry"),
        Country.class);
    Country defendingCountry = context.deserialize(jsonObject.get("defendingCountry"),
        Country.class);*/
    Country attackingCountry = gameState.getCountries().stream()
        .filter(x -> x.getCountryName().toString()
            .equals(jsonObject.get("attackingCountry").getAsString())).findFirst()
        .get();
    Country defendingCountry = gameState.getCountries().stream()
        .filter(x -> x.getCountryName().toString()
            .equals(jsonObject.get("defendingCountry").getAsString())).findFirst()
        .get();
    int troopNumber = jsonObject.get("troopNumber").getAsInt();

    Attack attack = new Attack(attackingCountry, defendingCountry, troopNumber);
    attack.calculateLosses();

    ArrayList<Integer> attackDiceResult = context.deserialize(jsonObject.get("attackDiceResult"),
        ArrayList.class);
    ArrayList<Integer> defendDiceResult = context.deserialize(jsonObject.get("defendDiceResult"),
        ArrayList.class);

    // Set values for attackDiceResult and defendDiceResult using reflection, as they are final fields
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
