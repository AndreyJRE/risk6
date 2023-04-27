package com.unima.risk6.network.serialization;

import com.google.gson.*;
import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.ai.bots.MonteCarloBot;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerTypeAdapter implements JsonSerializer<Player>, JsonDeserializer<Player>{

  private final static Logger LOGGER = LoggerFactory.getLogger(PlayerTypeAdapter.class);

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
    jsonObject.addProperty("type", player.getClass().getSimpleName());


    return jsonObject;
  }
  @Override
  public Player deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    Player player;


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

    switch(jsonObject.get("type").getAsString()){
      case "Player":
        player = new Player(user);
        break;
      case "EasyBot":
        player = new EasyBot();
        break;
      case "MediumBot":
        player = new MediumBot();
        break;
      case "HardBot":
        player = new HardBot();
        break;
      default:
        player = new Player();
        LOGGER.debug("PlayerType should be Player, EasyBot, MediumBot or Hardbot");
        break;
    }
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
