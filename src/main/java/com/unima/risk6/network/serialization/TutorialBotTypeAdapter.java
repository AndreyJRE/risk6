package com.unima.risk6.network.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unima.risk6.game.ai.tutorial.TutorialBot;
import com.unima.risk6.game.models.Player;
import java.lang.reflect.Type;

public class TutorialBotTypeAdapter implements JsonSerializer<TutorialBot> {

  @Override
  public JsonElement serialize(TutorialBot tutorialBot, Type type,
      JsonSerializationContext context) {
    JsonObject jsonObject = context.serialize(tutorialBot, Player.class).getAsJsonObject();
    JsonArray deterministicClaims = new JsonArray();
    tutorialBot.getDeterministicClaims().forEach(
        x -> deterministicClaims.add(context.serialize(x))
    );
    jsonObject.add("deterministicClaims", deterministicClaims);
    JsonArray deterministicReinforce = new JsonArray();
    tutorialBot.getDeterministicReinforces()
        .forEach(x -> deterministicReinforce.add(context.serialize(x)));
    jsonObject.add("deterministicReinforces", deterministicReinforce);

    JsonArray deterministicAttacks = new JsonArray();
    tutorialBot.getDeterministicAttacks()
        .forEach(x -> {
          JsonArray jsonArray = new JsonArray();
          jsonArray.add(x.getOutgoing().getCountryName().toString());
          jsonArray.add(x.getIncoming().getCountryName().toString());
          JsonObject countryPair = new JsonObject();
          countryPair.add("CountryPair", jsonArray);
          deterministicAttacks.add(countryPair);
        });
    jsonObject.add("deterministicAttacks", deterministicAttacks);

    JsonArray deterministicAfterAttacks = new JsonArray();
    tutorialBot.getDeterministicAfterAttacks()
        .forEach(x -> deterministicAfterAttacks.add(context.serialize(x)));
    jsonObject.add("deterministicAfterAttacks", deterministicAfterAttacks);

    JsonArray deterministicFortifies = new JsonArray();
    tutorialBot.getDeterministicFortifies()
        .forEach(x -> deterministicFortifies.add(context.serialize(x)));
    jsonObject.add("deterministicFortifies", deterministicFortifies);
    return jsonObject;
  }

}