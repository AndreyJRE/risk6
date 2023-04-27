package com.unima.risk6.network.serialization;

import com.google.gson.*;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class PlayerTypeAdapter implements JsonSerializer<Player>, JsonDeserializer<Player>{

  @Override
  public JsonElement serialize(Player player, Type typeOfSrc, JsonSerializationContext context) {
    if (player == null) {
      return null;
    }

    JsonObject jsonObject = new JsonObject();

    jsonObject.add("hand", context.serialize(player.getHand(), Hand.class));
    JsonArray countriesArray = new JsonArray();
    Set<Country> countries = player.getCountries();
    if (countries != null) {
      for (Country country : countries) {
        countriesArray.add(context.serialize(country, Country.class));
      }
    }
    jsonObject.add("countries", countriesArray);

    JsonArray continentsArray = new JsonArray();
    Set<Continent> continents = player.getContinents();
    if (continents != null) {
      for (Continent continent : continents) {
        continentsArray.add(context.serialize(continent, Continent.class));
      }
    }
    jsonObject.add("continents", continentsArray);
    jsonObject.add("user", context.serialize(player.getUser(), String.class));
    jsonObject.add("currentPhase", context.serialize(player.getCurrentPhase(), GamePhase.class));
    jsonObject.addProperty("deployableTroops", player.getDeployableTroops());
    jsonObject.addProperty("initialTroops", player.getInitialTroops());



    return jsonObject;
  }
  @Override
  public Player deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    Hand hand = context.deserialize(jsonObject.get("hand"), Hand.class);

    JsonArray countriesArray = jsonObject.getAsJsonArray("countries");
    Set<Country> countries = new HashSet<>();
    for (JsonElement countryElement : countriesArray) {
      countries.add(context.deserialize(countryElement, Country.class));
    }

    JsonArray continentsArray = jsonObject.getAsJsonArray("continents");
    Set<Continent> continents = new HashSet<>();
    for (JsonElement continentElement : continentsArray) {
      continents.add(context.deserialize(continentElement, Continent.class));
    }

    String user = context.deserialize(jsonObject.get("user"), String.class);
    GamePhase currentPhase = context.deserialize(jsonObject.get("currentPhase"), GamePhase.class);
    int deployableTroops = jsonObject.get("deployableTroops").getAsInt();
    int initialTroops = jsonObject.get("initialTroops").getAsInt();

    Player player = new Player(user);
    //TODO
    /*player.setHand(hand);
    player.setCountries(countries);
    player.setContinents(continents);

     */
    player.setCurrentPhase(currentPhase);
    player.setDeployableTroops(deployableTroops);
    player.setInitialTroops(initialTroops);

    return player;
  }

}
