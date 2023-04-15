package com.unima.risk6.network.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.GamePhase;
import java.lang.reflect.Type;
import java.util.Set;

public class PlayerTypeAdapter implements JsonSerializer<Player> {

  @Override
  public JsonElement serialize(Player player, Type typeOfSrc, JsonSerializationContext context) {
    if (player == null) {
      return null;
    }

    JsonObject jsonObject = new JsonObject();

    jsonObject.add("hand", context.serialize(player.getHand(), Hand.class));
    jsonObject.add("user", context.serialize(player.getUser(), String.class));
    jsonObject.add("currentPhase", context.serialize(player.getCurrentPhase(), GamePhase.class));
    jsonObject.addProperty("deployableTroops", player.getDeployableTroops());
    jsonObject.addProperty("initialTroops", player.getInitialTroops());

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

    return jsonObject;
  }
}
