package com.unima.risk6.network.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.logic.GameState;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.network.message.Message;

public class Serializer {

  public static String serialize(Message obj ){
    return new GsonBuilder()
            .registerTypeAdapter(GameState.class,new GameStateTypeAdapter())
            .registerTypeAdapter(Country.class,new CountryTypeAdapter())
            .registerTypeAdapter(Continent.class,new ContinentTypeAdapter())
            .registerTypeAdapter(Hand.class,new HandTypeAdapter())
            .registerTypeAdapter(Player.class,new PlayerTypeAdapter())
            .registerTypeAdapter(EasyBot.class,new EasyBotTypeAdapter())
            .create()
            .toJson(obj);
  }


}
