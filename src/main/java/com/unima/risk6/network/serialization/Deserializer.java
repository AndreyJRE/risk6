package com.unima.risk6.network.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.*;
import com.unima.risk6.network.message.Message;
import com.unima.risk6.network.message.StandardMessage;


public class Deserializer {
//TODO
  public static Message deserialize(String json, GameState gameState) {
    return new GsonBuilder()
            .registerTypeAdapter(StandardMessage.class, new StandardMessageAdapter())

            .registerTypeAdapter(GameState.class, new GameStateTypeAdapter(gameState))

            .registerTypeAdapter(Country.class, new CountryTypeAdapter())
            .registerTypeAdapter(Continent.class, new ContinentTypeAdapter())
            .registerTypeAdapter(Card.class, new CardTypeAdapter())
            .registerTypeAdapter(Hand.class, new HandTypeAdapter())
            .registerTypeAdapter(Player.class, new PlayerTypeAdapter(gameState))
            //.registerTypeAdapter(EasyBot.class, new EasyBotTypeAdapter())

            .registerTypeAdapter(Attack.class, new AttackTypeAdapter())
            .registerTypeAdapter(Fortify.class, new FortifyTypeAdapter())
            .registerTypeAdapter(Reinforce.class, new ReinforceTypeAdapter())
        .create()
        .fromJson(json, StandardMessage.class);
    //return new Gson().fromJson(json, StandardMessage.class);
  }

  public static Message deserialize(String json) {
    return new GsonBuilder()
            .registerTypeAdapter(StandardMessage.class, new StandardMessageAdapter())

            .registerTypeAdapter(GameState.class, new GameStateTypeAdapter())

            .registerTypeAdapter(Country.class, new CountryTypeAdapter())
            .registerTypeAdapter(Continent.class, new ContinentTypeAdapter())
            .registerTypeAdapter(Card.class, new CardTypeAdapter())
            .registerTypeAdapter(Hand.class, new HandTypeAdapter())
            .registerTypeAdapter(Player.class, new PlayerTypeAdapter())
            .registerTypeAdapter(EasyBot.class, new EasyBotTypeAdapter())

            .registerTypeAdapter(Attack.class, new AttackTypeAdapter())
            .registerTypeAdapter(Fortify.class, new FortifyTypeAdapter())
            .registerTypeAdapter(Reinforce.class, new ReinforceTypeAdapter())
            .create()
            .fromJson(json, StandardMessage.class);
    //return new Gson().fromJson(json, StandardMessage.class);
  }
}
