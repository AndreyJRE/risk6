package com.unima.risk6.network.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import java.lang.reflect.Type;
import java.util.Set;

/**
 * A JsonSerializer implementation for {@link com.unima.risk6.game.models.Continent} objects. This
 * class defines how Continent objects are converted to their JSON representations.
 *
 * @author jferch
 */
public class ContinentTypeAdapter implements JsonSerializer<Continent> {

  /**
   * Serializes a Continent object to its corresponding JSON representation.
   *
   * @param continent The Continent object to be serialized.
   * @param typeOfSrc The actual generic type of the source object.
   * @param context The context for serialization.
   * @return A JsonElement corresponding to the specified Continent.
   */
  @Override
  public JsonElement serialize(Continent continent, Type typeOfSrc,
      JsonSerializationContext context) {
    if (continent == null) {
      return null;
    }

    JsonObject jsonObject = new JsonObject();

    jsonObject.addProperty("continentName", continent.getContinentName().toString());
    jsonObject.addProperty("bonusTroops", continent.getBonusTroops());

    JsonArray countriesArray = new JsonArray();
    Set<Country> countries = continent.getCountries();
    if (countries != null) {
      for (Country country : countries) {
        countriesArray.add(context.serialize(country, Country.class));
      }
      jsonObject.add("countries", countriesArray);
    }

    return jsonObject;
  }
}
