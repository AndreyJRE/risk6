package com.unima.risk6.game.ai.bots;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class EasyBotTest {

  static AiBot easyBot;
  static PlayerController botTestController;
  static Player enemy;
  static PlayerController enemyController;
  static GameState gameState;

  @BeforeAll
  static void setUp() {
    easyBot = new EasyBot("Peter Parker");
    botTestController = new PlayerController();
    botTestController.setPlayer((EasyBot) easyBot);
    enemy = new Player();
    List<AiBot> bots = new ArrayList<>();
    List<String> humans = new ArrayList<>();
    humans.add("Gary Oldman");
    bots.add(easyBot);
    gameState = GameConfiguration.configureGame(humans, bots);
    for (Player p : gameState.getActivePlayers()) {
      if (p.getUser().equals("Gary Oldman")) {
        enemy = p;
      }
    }
    enemyController = new PlayerController();
    enemyController.setPlayer(enemy);
  }

  @Test
  void claimCountryTest() {
    ((EasyBot) easyBot).setCurrentGameState(gameState);
    Reinforce firstClaim = easyBot.claimCountry();
    assertNotNull(firstClaim.getCountry());
    assertEquals(1, firstClaim.getToAdd());
  }

  @Test
  void createAllAttacksTest() {
    Country greatBritain = getCountryByName(CountryName.GREAT_BRITAIN);
    botTestController.addCountry(greatBritain);
    Country iceland = getCountryByName(CountryName.ICELAND);
    enemyController.addCountry(iceland);
    ((EasyBot) easyBot).setCurrentGameState(gameState);
    CountryPair attack = easyBot.createAttack();
    // can't attack with 1 troop
    assertNull(attack);
    greatBritain.setTroops(3);
    iceland.setTroops(1);
    attack = easyBot.createAttack();
    assertEquals(new CountryPair(greatBritain, iceland), attack);
  }

  @Test
  void moveAfterAttackTest() {
    Country ural = getCountryByName(CountryName.URAL);
    Country siberia = getCountryByName(CountryName.SIBERIA);
    ural.setTroops(5);
    siberia.setTroops(3);
    Fortify fortify = easyBot.moveAfterAttack(new CountryPair(ural, siberia));
    assertNotNull(fortify);
    assertTrue(fortify.getOutgoing().equals(ural) && fortify.getIncoming().equals(siberia));
    assertTrue(fortify.getTroopsToMove() >= 0 && fortify.getTroopsToMove() < 5);
  }

  @Test
  void createFortify() {
    Country westernEurope = getCountryByName(CountryName.WESTERN_EUROPE);
    Country southernEurope = getCountryByName(CountryName.SOUTHERN_EUROPE);
    botTestController.addCountry(westernEurope);
    botTestController.addCountry(southernEurope);
    westernEurope.setTroops(1);
    southernEurope.setTroops(1);
    Fortify fortify = easyBot.createFortify();
    assertNull(fortify);
    westernEurope.setTroops(4);
    fortify = easyBot.createFortify();
    if (fortify != null) {
      assertTrue(fortify.getOutgoing().equals(westernEurope) && fortify.getIncoming()
          .equals(southernEurope));
      assertTrue(fortify.getTroopsToMove() > 0 && fortify.getTroopsToMove() < 4);
    }

  }

  @Test
  void createAllReinforcementsTest() {
    Country middleEast = getCountryByName(CountryName.MIDDLE_EAST);
    botTestController.addCountry(middleEast);
    List<Reinforce> reinforcements = easyBot.createAllReinforcements();
    // no deployable troops
    assertEquals(0, reinforcements.size());
    ((EasyBot) easyBot).setDeployableTroops(5);
    reinforcements = easyBot.createAllReinforcements();
    assertTrue(reinforcements.size() > 0);
    assertEquals(reinforcements.get(0).getCountry(), middleEast);
  }

  static Country getCountryByName(CountryName countryName) {
    return gameState.getCountries().stream().filter(n -> n.getCountryName().equals(countryName))
        .findFirst().orElse(null);
  }
}