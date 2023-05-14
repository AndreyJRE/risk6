package com.unima.risk6.network.serialization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.bots.EasyBot;
import com.unima.risk6.game.ai.bots.MediumBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.ContinentName;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.network.message.StandardMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests for object serialization
 *
 * @author jferch
 */


public class SerializationTest {

  @Test
  void testSerializationWithStatus() {
    assertEquals("{\"statusCode\":200,\"content\":\"tetest\",\"contentType\":\"DEFAULT\"}",
        Serializer.serialize(new StandardMessage<>("tetest", 200)));
  }

  @Test
  void testSerializationWithoutStatus() {
    assertEquals("{\"statusCode\":-1,\"content\":\"tetest\",\"contentType\":\"DEFAULT\"}",
        Serializer.serialize(new StandardMessage<>("tetest")));

  }

  @Test
  void testSerializationAndDeserialization() {
    StandardMessage<String> standard = new StandardMessage<>("tetest");
    String json = Serializer.serialize(standard);
    @SuppressWarnings("unchecked")
    StandardMessage<String> s2 = (StandardMessage<String>) Deserializer.deserialize(json);
    assertTrue(standard.equals(s2));

  }

  @Test
  void testEmptyPlayerSerialization() {
    ArrayList<String> users = new ArrayList<>(List.of("Peter"));
    ArrayList<AiBot> bots = new ArrayList<>();
    GameState gamestate = GameConfiguration.configureGame(users, bots);
    StandardMessage<GameState> message = new StandardMessage<>(gamestate);
    @SuppressWarnings("unchecked")
    StandardMessage<GameState> m2 = (StandardMessage<GameState>) Deserializer.deserialize(
        Serializer.serialize(message),
        GameConfiguration.configureGame(new ArrayList<>(), new ArrayList<>()));

    Player p1 = gamestate.getCurrentPlayer();
    Player p2 = m2.getContent().getCurrentPlayer();
    System.out.println("P1: " + p1.getUser() + " P2: " + p2.getUser());
    assertEquals(p1, p2);

  }

  @Test
  void testPlayerSerializationWithOneCountry() {
    ArrayList<String> users = new ArrayList<>(List.of("Peter"));
    ArrayList<AiBot> bots = new ArrayList<>();
    GameState oldGamestate = GameConfiguration.configureGame(users, bots);
    oldGamestate.getCurrentPlayer().getCountries()
        .add(oldGamestate.getCountries().stream().findFirst().orElseThrow());
    StandardMessage<GameState> message = new StandardMessage<>(oldGamestate);
    String json = Serializer.serialize(message);
    System.out.println(json);

    GameState newGamestate = GameConfiguration.configureGame(new ArrayList<>(),
        new ArrayList<>());
    @SuppressWarnings("unchecked")
    StandardMessage<GameState> m2 = (StandardMessage<GameState>) Deserializer.deserialize(json,
        newGamestate);
    Player p1 = oldGamestate.getCurrentPlayer();
    GameState g2 = m2.getContent();
    Player p2 = g2.getCurrentPlayer();
    System.out.println("P1: " + p1.getUser() + " P2: " + p2.getUser());
    //g2 is the same as newGamestate
    Country country = g2.getCurrentPlayer().getCountries().stream().findFirst().orElseThrow();
    assertSame(country, g2.getCountries().stream()
        .filter(x -> x.getCountryName().equals(country.getCountryName())).findFirst()
        .orElseThrow());

  }

  @Test
  void testAttackSerialization() {
    Country c1 = new Country(CountryName.ALASKA);
    c1.setContinent(new Continent(ContinentName.AFRICA));
    Country c2 = new Country(CountryName.ICELAND);
    c2.setContinent(new Continent(ContinentName.AFRICA));

    Attack a = new Attack(c1, c2, 69);
    StandardMessage<Attack> message = new StandardMessage<>(a);
    System.out.println("Attack Serialization:\n" + Serializer.serialize(message));
    assertEquals(
        "{\"statusCode\":-1,\"content\":{\"attackingCountry\":\"ALASKA\",\"defendingCountry\":\"ICELAND\",\"troopNumber\":69,\"attackerLosses\":0,\"defenderLosses\":0,\"attackDiceResult\":[],\"defendDiceResult\":[],\"hasConquered\":false},\"contentType\":\"ATTACK\"}",
        Serializer.serialize(message));

  }

  @Test
  void testFortifySerialization() {
    Country c1 = new Country(CountryName.ALASKA);
    c1.setContinent(new Continent(ContinentName.AFRICA));
    Country c2 = new Country(CountryName.ICELAND);
    c2.setContinent(new Continent(ContinentName.AFRICA));

    Fortify a = new Fortify(c1, c2, 69);
    StandardMessage<Fortify> message = new StandardMessage<>(a);
    System.out.println("Fortify Serialization:\n" + Serializer.serialize(message));
    assertEquals(
        "{\"statusCode\":-1,\"content\":{\"outgoing\":\"ALASKA\",\"incoming\":\"ICELAND\",\"troopsToMove\":69},\"contentType\":\"FORTIFY\"}",
        Serializer.serialize(message));
  }

  @Test
  void testReinforceSerialization() {
    Country c1 = new Country(CountryName.ALASKA);
    c1.setContinent(new Continent(ContinentName.AFRICA));

    Reinforce a = new Reinforce(c1, 69);
    StandardMessage<Reinforce> message = new StandardMessage<>(a);
    System.out.println("Reinforce Serialization:\n" + Serializer.serialize(message));
    assertEquals(
        "{\"statusCode\":-1,\"content\":{\"country\":\"ALASKA\",\"toAdd\":69},\"contentType\":\"REINFORCE\"}",
        Serializer.serialize(message));
  }

  @Test
  void testInitialGamestateSerialization() {
    ArrayList<String> users = new ArrayList<>(List.of(""));
    ArrayList<AiBot> bots = new ArrayList<>(Arrays.asList(new MediumBot(), new EasyBot()));
    GameState gamestate = GameConfiguration.configureGame(users, bots);
    Country c1 = new Country(CountryName.ALASKA);
    c1.setContinent(new Continent(ContinentName.AFRICA));

    Reinforce a = new Reinforce(c1, 69);
    gamestate.getLastMoves().add(a);

    Country c2 = new Country(CountryName.ALASKA);
    c1.setContinent(new Continent(ContinentName.AFRICA));
    Country c3 = new Country(CountryName.ICELAND);
    c2.setContinent(new Continent(ContinentName.AFRICA));

    Fortify f = new Fortify(c2, c3, 69);
    gamestate.getLastMoves().add(f);

    StandardMessage<GameState> message = new StandardMessage<>(gamestate);

    @SuppressWarnings("unchecked")
    StandardMessage<GameState> m2 = (StandardMessage<GameState>) Deserializer.deserialize(
        Serializer.serialize(message),
        GameConfiguration.configureGame(new ArrayList<>(), new ArrayList<>()));
    GameState g2 = m2.getContent();
    String message1 = Serializer.serialize(message);
    String message2 = Serializer.serialize(new StandardMessage<>(g2));
    System.out.println("GameState Serialization:\n" + message1);
    System.out.println("GameState Serialization:\n" + message2);
    System.out.println(message1.replaceAll(".hashCode..\\d*?,", ""));

    //Failed ohne regex wegen Hashcode
    assertEquals(message1.replaceAll(".hashCode..\\d*?,", ""),
        message2.replaceAll(".hashCode..\\d*?,", ""));
  }

  @Test
  void testOrderMessageSerializationTest() {
    HashMap<String, Integer> hashMap = new HashMap<>();
    hashMap.put("lel", 1);
    hashMap.put("lol", 2);
    StandardMessage<HashMap<String, Integer>> message = new StandardMessage<>(hashMap);
    System.out.println(Serializer.serialize(message));
    @SuppressWarnings("unchecked")
    HashMap<String, Integer> hashMap2 = (HashMap<String, Integer>) Deserializer.deserialize(Serializer.serialize(message)).getContent();
    assertEquals(hashMap.get("lel"), hashMap2.get("lel"));
  }

}
