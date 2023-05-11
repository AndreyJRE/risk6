package com.unima.risk6.network.serialization;

import com.google.gson.GsonBuilder;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.ai.tutorial.TutorialBot;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.network.message.Message;

public class Serializer {

  public static String serialize(Message obj) {
    return new GsonBuilder()
        .registerTypeAdapter(GameState.class, new GameStateTypeAdapter())

        .registerTypeAdapter(Country.class, new CountryTypeAdapter())
        .registerTypeAdapter(Continent.class, new ContinentTypeAdapter())
        .registerTypeAdapter(Card.class, new CardTypeAdapter())
        .registerTypeAdapter(Hand.class, new HandTypeAdapter())
        .registerTypeAdapter(Player.class, new PlayerTypeAdapter())
        .registerTypeAdapter(EasyBot.class, new EasyBotTypeAdapter())
        .registerTypeAdapter(MediumBot.class, new MediumBotTypeAdapter())
        .registerTypeAdapter(HardBot.class, new HardBotTypeAdapter())
        .registerTypeAdapter(TutorialBot.class, new TutorialBotTypeAdapter())

        .registerTypeAdapter(Attack.class, new AttackTypeAdapter())
        .registerTypeAdapter(Fortify.class, new FortifyTypeAdapter())
        .registerTypeAdapter(Reinforce.class, new ReinforceTypeAdapter())
        .registerTypeAdapter(HandIn.class, new HandInTypeAdapter())
        .registerTypeAdapter(EndPhase.class, new EndPhaseTypeAdapter())
        .create()
        .toJson(obj);
  }


}
