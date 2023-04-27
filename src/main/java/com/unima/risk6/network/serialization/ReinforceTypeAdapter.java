package com.unima.risk6.network.serialization;


import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSerializationContext;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Country;

import java.lang.reflect.Type;

public class ReinforceTypeAdapter implements JsonSerializer<Reinforce>, JsonDeserializer<Reinforce> {

    @Override
    public JsonElement serialize(Reinforce reinforce, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("country", context.serialize(reinforce.getCountry()));
        jsonObject.addProperty("toAdd", reinforce.getToAdd());
        return jsonObject;
    }

    @Override
    public Reinforce deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Country country = context.deserialize(jsonObject.get("country"), Country.class);
        int toAdd = jsonObject.get("toAdd").getAsInt();
        return new Reinforce(country, toAdd);
    }
}

