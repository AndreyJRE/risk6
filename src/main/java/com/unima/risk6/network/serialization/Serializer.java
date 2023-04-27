package com.unima.risk6.network.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Dice;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Move;
import com.unima.risk6.game.logic.Reinforce;
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


            .registerTypeAdapter(Attack.class, new AttackTypeAdapter())

            .registerTypeAdapter(Fortify.class, new FortifyTypeAdapter())
            .registerTypeAdapter(Reinforce.class, new ReinforceTypeAdapter())
            .create()
            .toJson(obj);
  }


}
