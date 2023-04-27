package com.unima.risk6.network.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.network.message.Message;
import com.unima.risk6.network.message.StandardMessage;


public class Deserializer {

  public static Message deserialize(String json) {
    return new GsonBuilder()
        .registerTypeAdapter(StandardMessage.class, new StandardMessageAdapter())
        .registerTypeAdapter(Player.class, new PlayerTypeAdapter())
        .registerTypeAdapter(Hand.class, new HandTypeAdapter())
        .registerTypeAdapter(Card.class, new CardTypeAdapter())
        .registerTypeAdapter(GameState.class, new GameStateTypeAdapter())
        .create()
        .fromJson(json, StandardMessage.class);
    //return new Gson().fromJson(json, StandardMessage.class);
  }
}
