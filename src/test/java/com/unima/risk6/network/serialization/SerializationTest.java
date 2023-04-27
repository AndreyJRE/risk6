package com.unima.risk6.network.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.database.configurations.DatabaseConfiguration;
import com.unima.risk6.database.models.User;
import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.ContinentName;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.network.message.StandardMessage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Tests for object serialization
 *
 * @author jferch
 */


public class SerializationTest {
  @BeforeAll
  static void setUp() {
    try {

    } catch (Exception e) {
    }

  }


  @Test
  void testSerializationWithStatus() {
    assertEquals("{\"statusCode\":200,\"content\":\"tetest\",\"contentType\":\"DEFAULT\"}", Serializer.serialize(new StandardMessage("tetest", 200)));
  }

  @Test
  void testSerializationWithoutStatus() {
    assertEquals("{\"statusCode\":-1,\"content\":\"tetest\",\"contentType\":\"DEFAULT\"}", Serializer.serialize(new StandardMessage("tetest")));

  }

  @Test
  void testSerializationAndDeserialization() {
    StandardMessage standard =  new StandardMessage("tetest");
    String json = Serializer.serialize(standard);
    StandardMessage s2 = (StandardMessage) Deserializer.deserialize(json);
    //System.out.println(s2.getStatusCode() + (String)s2.getContent());

    //assertEquals(standard, s2);
    assertTrue(standard.equals(s2));

  }

  @Test
  void testPlayerSerialization() {
    Player player = new Player("Jakob");
    StandardMessage<Player> s = new StandardMessage<Player>(player);
    String json = Serializer.serialize(s);
    System.out.println(json);
    //StandardMessage s2 = (StandardMessage) Deserializer.deserialize(json);
    //System.out.println(s2.getStatusCode() + (String)s2.getContent());
    //StandardMessage message = (StandardMessage<Player>) Deserializer.deserialize(json);
    //Player player2 = (Player) message.getContent();
    //System.out.println(M);
    //TODO
    assertTrue(false);
    //assertTrue(player.equals(player2));

  }
  /*
  @Test
  void testEasyBotSerialization() {
    System.out.println("EasyBotSrialization");
    ArrayList<String> users =  new ArrayList<String>(Arrays.asList("Andrey","Max","Fung"));
    ArrayList<AiBot> bots = new ArrayList<AiBot>();
    GameState gameState = GameConfiguration.configureGame(users, bots);
    gameState.setCurrentPhase(GamePhase.CLAIMPHASE);
    EasyBot bot = new EasyBot(gameState);
    StandardMessage<Player> s = new StandardMessage<Player>(bot);
    String json = Serializer.serialize(s);
    System.out.println(json);
    //StandardMessage s2 = (StandardMessage) Deserializer.deserialize(json);
    //System.out.println(s2.getStatusCode() + (String)s2.getContent());
    StandardMessage message = (StandardMessage) Deserializer.deserialize(json);
    //TODO
    assertTrue(bot.equals(message.getContent()));

  }

 */
  @Test
  void testAttackSerialization(){
      Country c1 = new Country(CountryName.ALASKA);
      c1.setContinent(new Continent(ContinentName.AFRICA));
      Country c2 = new Country(CountryName.ICELAND);
      c2.setContinent(new Continent(ContinentName.AFRICA));

      Attack a = new Attack(c1, c2, 69);
      StandardMessage<Attack> message = new StandardMessage<>(a);
      System.out.println("Attack Serialization:\n" + Serializer.serialize(message));
      assertTrue(Serializer.serialize(message).equals("{\"statusCode\":-1,\"content\":{\"attackingCountry\":{\"countryName\":\"ALASKA\",\"hasPlayer\":false,\"troops\":0,\"adjacentCountries\":[],\"continent\":\"AFRICA\"},\"defendingCountry\":{\"countryName\":\"ICELAND\",\"hasPlayer\":false,\"troops\":0,\"adjacentCountries\":[],\"continent\":\"AFRICA\"},\"troopNumber\":69,\"attackerLosses\":0,\"defenderLosses\":0,\"attackDiceResult\":[],\"defendDiceResult\":[]},\"contentType\":\"ATTACK\"}"));

  }
    @Test
    void testFortifySerialization(){
        Country c1 = new Country(CountryName.ALASKA);
        c1.setContinent(new Continent(ContinentName.AFRICA));
        Country c2 = new Country(CountryName.ICELAND);
        c2.setContinent(new Continent(ContinentName.AFRICA));

        Fortify a = new Fortify(c1, c2, 69);
        StandardMessage<Fortify> message = new StandardMessage<>(a);
        System.out.println("Fortify Serialization:\n" + Serializer.serialize(message));
        assertTrue(Serializer.serialize(message).equals("{\"statusCode\":-1,\"content\":{\"outgoing\":{\"countryName\":\"ALASKA\",\"hasPlayer\":false,\"troops\":0,\"adjacentCountries\":[],\"continent\":\"AFRICA\"},\"incoming\":{\"countryName\":\"ICELAND\",\"hasPlayer\":false,\"troops\":0,\"adjacentCountries\":[],\"continent\":\"AFRICA\"},\"troopsToMove\":69},\"contentType\":\"FORTIFY\"}"));

    }
    @Test
    void testReinforceSerialization(){
        Country c1 = new Country(CountryName.ALASKA);
        c1.setContinent(new Continent(ContinentName.AFRICA));

        Reinforce a = new Reinforce(c1, 69);
        StandardMessage<Reinforce> message = new StandardMessage<>(a);
        System.out.println("Reinforce Serialization:\n" + Serializer.serialize(message));
        assertTrue(Serializer.serialize(message).equals("{\"statusCode\":-1,\"content\":{\"country\":{\"countryName\":\"ALASKA\",\"hasPlayer\":false,\"troops\":0,\"adjacentCountries\":[],\"continent\":\"AFRICA\"},\"toAdd\":69},\"contentType\":\"REINFORCE\"}"));
    }

  @Test
  void testInitialGamestateSerialization(){
    ArrayList<String> users =  new ArrayList<String>(Arrays.asList("Andrey","Max","Fung"));
    ArrayList<AiBot> bots = new ArrayList<AiBot>(Arrays.asList(new EasyBot(),new EasyBot()));
    GameState gamestate = GameConfiguration.configureGame(users, bots);;
    StandardMessage<GameState> message = new StandardMessage<>(gamestate);
    System.out.println("GameState Serialization:\n" + Serializer.serialize(message));
    StandardMessage<GameState> m2 =
        (StandardMessage<GameState>) Deserializer.deserialize(Serializer.serialize(message));
    GameState g2 = m2.getContent();
    assertTrue(Serializer.serialize(message).equals(Serializer.serialize(new StandardMessage<GameState>(g2))));
  }
}
