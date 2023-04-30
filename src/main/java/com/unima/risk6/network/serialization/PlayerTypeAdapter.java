package com.unima.risk6.network.serialization;

import com.google.gson.*;
import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.models.*;
import com.unima.risk6.game.models.enums.GamePhase;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerTypeAdapter implements JsonSerializer<Player>, JsonDeserializer<Player>{



  private final static Logger LOGGER = LoggerFactory.getLogger(PlayerTypeAdapter.class);

  private GameState gameState;

  public PlayerTypeAdapter(GameState gameState) {
    this.gameState = gameState;
    System.out.println("PlayerTypeAdapter");
  }
  public PlayerTypeAdapter() {
  }

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
        JsonObject countryObject = new JsonObject();
        countryObject.addProperty("troops", country.getTroops());
        countryObject.addProperty("name", country.getCountryName().toString());
        countriesArray.add(countryObject);
        //countriesArray.add(context.serialize(country, Country.class));
      }
    }
    jsonObject.add("countries", countriesArray);

    JsonArray continentsArray = new JsonArray();
    Set<Continent> continents = player.getContinents();
    if (continents != null) {
      for (Continent continent : continents) {
          JsonObject continentObject = new JsonObject();
          continentObject.addProperty("name", continent.getContinentName().toString());
          continentsArray.add(continentObject);
        //continentsArray.add(context.serialize(continent, Continent.class));
      }
    }
    jsonObject.add("continents", continentsArray);
    jsonObject.add("user", context.serialize(player.getUser(), String.class));
    jsonObject.add("currentPhase", context.serialize(player.getCurrentPhase(), GamePhase.class));
    jsonObject.addProperty("deployableTroops", player.getDeployableTroops());
    jsonObject.addProperty("initialTroops", player.getInitialTroops());
    jsonObject.addProperty("type", player.getClass().getSimpleName());
    jsonObject.addProperty("hashCode", player.hashCode());


    return jsonObject;
  }
  @Override
  public Player deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();

    Player player;


    Hand hand = context.deserialize(jsonObject.get("hand"), Hand.class);

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
    //TODO REFERENZEN Überprüfen
    player.setHand(hand);
    //if the player owns a whole continent the references for the countries in a continent and the
    //for the countries should be the same
    JsonArray countriesArray = jsonObject.getAsJsonArray("countries");
    Set<Country> countries = gameState.getCountries();
    for (JsonElement countryElement : countriesArray) {
      String countryName = countryElement.getAsJsonObject().get("name").getAsString();
      countries.stream().filter(x -> x.getCountryName().toString().equals(countryName)).forEach(x -> {
        //set the owner of the country
        x.setPlayer(player);
        //set the amount of troops in the country
        x.setTroops(countryElement.getAsJsonObject().get("troops").getAsInt());
        //add the country to the players Array
        x.getPlayer().getCountries().add(x);
      });

    }

    JsonArray continentsArray = jsonObject.getAsJsonArray("continents");
    Set<Continent> continents = gameState.getContinents();
    for (JsonElement continentElement : continentsArray) {
      String continentName = continentElement.getAsJsonObject().get("name").getAsString();
      continents.stream().filter(x -> x.getContinentName().toString().equals(continentName)).forEach(x -> {
        //add the continent to the players Array
        player.getContinents().add(x);
      });
    }
    /*
    if(continents.size()>0){
      player.getContinents().addAll(continents);
      continents.forEach((x) ->  player.getCountries().addAll(x.getCountries()));
      //remove all countries from countries set that are already inside continents set
      player.getContinents().forEach((x) -> countries.removeAll(x.getCountries()));
    }

    //add all remaining countries
    player.getCountries().addAll(countries);
    //add all countries from complete continents to the gamestate
    //player.getContinents().forEach((x) -> player.getCountries().addAll(x.getCountries()));
    */

    player.setCurrentPhase(currentPhase);
    player.setDeployableTroops(deployableTroops);
    player.setInitialTroops(initialTroops);

    return player;
  }

}
