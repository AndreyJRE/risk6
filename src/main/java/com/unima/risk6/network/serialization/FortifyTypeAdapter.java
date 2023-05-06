package com.unima.risk6.network.serialization;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.models.Country;
import java.lang.reflect.Type;


public class FortifyTypeAdapter implements JsonSerializer<Fortify>, JsonDeserializer<Fortify> {

  @Override
  public JsonElement serialize(Fortify fortify, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.add("outgoing", context.serialize(fortify.getOutgoing()));
    jsonObject.add("incoming", context.serialize(fortify.getIncoming()));
    jsonObject.addProperty("troopsToMove", fortify.getTroopsToMove());
    return jsonObject;
  }

  @Override
  public Fortify deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObject = json.getAsJsonObject();
    //TODO referenzen
    Country outgoing = context.deserialize(jsonObject.get("outgoing"), Country.class);
    Country incoming = context.deserialize(jsonObject.get("incoming"), Country.class);
    int troopsToMove = jsonObject.get("troopsToMove").getAsInt();
    return new Fortify(outgoing, incoming, troopsToMove);
  }
}
