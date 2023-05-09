package com.unima.risk6.game.ai.bots;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.game.models.enums.GamePhase;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MediumBotTest {

  static AiBot mediumBot;
  static PlayerController botTestController;
  static Player enemy;
  static PlayerController enemyController;
  static GameState gameState;

  @BeforeAll
  static void setUp() {
    Probabilities.init();
    mediumBot = new MediumBot("Sirius Black");
    botTestController = new PlayerController();
    botTestController.setPlayer((MediumBot) mediumBot);
    enemy = new Player();
    List<AiBot> bots = new ArrayList<>();
    List<String> humans = new ArrayList<>();
    humans.add("Cal Lightman");
    bots.add(mediumBot);
    gameState = GameConfiguration.configureGame(humans, bots);
    for (Player p : gameState.getActivePlayers()) {
      if (p.getUser().equals("Cal Lightman")) {
        enemy = p;
      }
    }
    enemyController = new PlayerController();
    enemyController.setPlayer(enemy);
  }

  @BeforeEach
  void resetState() {
    gameState.getCountries().forEach(c -> {
      c.setTroops(0);
      c.setPlayer(null);
      c.setHasPlayer(false);
    });
    mediumBot.setGameState(gameState);
    ((MediumBot) mediumBot).setInitialTroops(0);
    ((MediumBot) mediumBot).setDeployableTroops(0);
  }

  @Test
  void moveAfterAttack() {
    Country westernAus = getCountryByName(CountryName.WESTERN_AUSTRALIA);
    Country newGuinea = getCountryByName(CountryName.NEW_GUINEA);
    botTestController.addCountry(westernAus);
    westernAus.setTroops(2);
    botTestController.addCountry(newGuinea);
    newGuinea.setTroops(3);
    Country adj1 = getCountryByName(CountryName.INDONESIA);
    Country adj2 = getCountryByName(CountryName.EASTERN_AUSTRALIA);
    enemyController.addCountry(adj1);
    enemyController.addCountry(adj2);
    adj1.setTroops(2);
    adj2.setTroops(2);
    CountryPair toMove = new CountryPair(westernAus, newGuinea);
    Fortify move = mediumBot.moveAfterAttack(toMove);
    assertNull(move);
    westernAus.setTroops(3);
    move = mediumBot.moveAfterAttack(toMove);
    assertEquals(0, move.getTroopsToMove());
    westernAus.setTroops(5);
    move = mediumBot.moveAfterAttack(toMove);
    assertEquals(1, move.getTroopsToMove());
    westernAus.setTroops(8);
    adj1.setTroops(4);
    adj2.setTroops(6);
    move = mediumBot.moveAfterAttack(toMove);
    assertEquals(1, move.getTroopsToMove());
  }

  @Test
  void calculateTroopWeaknessTest() {
    Country mongolia = getCountryByName(CountryName.MONGOLIA);
    Country china = getCountryByName(CountryName.CHINA);
    mongolia.setTroops(3);
    china.setTroops(8);
    assertEquals(5, ((MediumBot) mediumBot).calculateTroopWeakness(mongolia, china));
  }

  @Test
  void claimCountryTest() {
    botTestController.getPlayer().setCurrentPhase(GamePhase.CLAIM_PHASE);
    Continent australia = getCountryByName(CountryName.WESTERN_AUSTRALIA).getContinent();
    // prioritizes australia
    Reinforce first = mediumBot.claimCountry();
    assertEquals(australia, first.getCountry().getContinent());
    // but will look elsewhere if owned more
    Country alberta = getCountryByName(CountryName.ALBERTA);
    botTestController.addCountry(alberta);
    Reinforce smart = mediumBot.claimCountry();
    assertEquals(alberta.getContinent(), smart.getCountry().getContinent());
    Random rng = new Random();
    gameState.getCountries().forEach(country -> {
      if (rng.nextDouble() < 0.5) {
        botTestController.addCountry(country);
      } else {
        enemyController.addCountry(country);
      }
      country.setTroops(1);
    });
    Reinforce partTwo = mediumBot.claimCountry();
    assertNotNull(partTwo);
    assertEquals(1, partTwo.getToAdd());
  }

  @Test
  void createAllReinforcementsTest1() {
    ((MediumBot) mediumBot).setDeployableTroops(15);
    Country indonesia = getCountryByName(CountryName.INDONESIA);
    botTestController.addCountry(indonesia);
    indonesia.setTroops(4);
    Country newGuinea = getCountryByName(CountryName.NEW_GUINEA);
    Country westernAus = getCountryByName(CountryName.WESTERN_AUSTRALIA);
    enemyController.addCountry(newGuinea);
    enemyController.addCountry(westernAus);
    newGuinea.setTroops(8);
    westernAus.setTroops(19);
    List<Reinforce> reinforcements = mediumBot.createAllReinforcements();
    assertEquals(1, reinforcements.size());
    assertEquals(indonesia, reinforcements.get(0).getCountry());
    assertEquals(15, reinforcements.get(0).getToAdd());
    Country venezuela = getCountryByName(CountryName.VENEZUELA);
    Country brazil = getCountryByName(CountryName.BRAZIL);
    enemyController.addCountry(brazil);
    brazil.setTroops(1);
    botTestController.addCountry(venezuela);
    venezuela.setTroops(4);
    westernAus.setTroops(17);
    reinforcements = mediumBot.createAllReinforcements(); // cant test content due to randomness
    int sum = reinforcements.stream().mapToInt(Reinforce::getToAdd).sum();
    assertEquals(((MediumBot) mediumBot).getDeployableTroops(), sum);
  }

  @Test
  void createAllReinforcementsTest2() {
    ((MediumBot) mediumBot).setCurrentPhase(GamePhase.CLAIM_PHASE);
    ((MediumBot) mediumBot).setInitialTroops(10);
    Country middleEast = getCountryByName(CountryName.MIDDLE_EAST);
    botTestController.addCountry(middleEast);
    middleEast.setTroops(1);
    for (Country adj : middleEast.getAdjacentCountries()) {
      enemyController.addCountry(adj);
      adj.setTroops(1);
    }
    // should reinforce middle east with everything
    List<Reinforce> reinforcements = mediumBot.createAllReinforcements();
    assertTrue(reinforcements.size() > 0);
    assertEquals(10, reinforcements.stream().mapToInt(Reinforce::getToAdd).sum());
  }

  @Test
  void createAllAttacksTest() {
    Country westernAus = getCountryByName(CountryName.WESTERN_AUSTRALIA);
    botTestController.addCountry(westernAus);
    westernAus.setTroops(8);
    Country indonesia = getCountryByName(CountryName.INDONESIA);
    botTestController.addCountry(indonesia);
    indonesia.setTroops(4);
    Country easternAus = getCountryByName(CountryName.EASTERN_AUSTRALIA);
    enemyController.addCountry(easternAus);
    easternAus.setTroops(2);
    Country newGuinea = getCountryByName(CountryName.NEW_GUINEA);
    enemyController.addCountry(newGuinea);
    newGuinea.setTroops(5);
    Country siam = getCountryByName(CountryName.SIAM);
    enemyController.addCountry(siam);
    siam.setTroops(1);
    CountryPair attack = mediumBot.createAttack();
    assertEquals(new CountryPair(westernAus, easternAus), attack);
    easternAus.setTroops(6);
    attack = mediumBot.createAttack();
    assertEquals(new CountryPair(indonesia, siam), attack);
    siam.setTroops(8);
    attack = mediumBot.createAttack();
    assertEquals(new CountryPair(westernAus, newGuinea), attack);
  }

  @Test
  void createFortify() {
    Country quebec = getCountryByName(CountryName.QUEBEC); // shouldn't pick these two
    Country easternUs = getCountryByName(CountryName.EASTERN_UNITED_STATES);
    botTestController.addCountry(quebec);
    botTestController.addCountry(easternUs);
    for (Country adj : quebec.getAdjacentCountries()) {
      enemyController.addCountry(adj);
      adj.setTroops(3);
    }
    quebec.setTroops(4);
    easternUs.setTroops(2);
    Country southAfrica = getCountryByName(CountryName.SOUTH_AFRICA);
    Country congo = getCountryByName(CountryName.CONGO);
    botTestController.addCountry(southAfrica);
    botTestController.addCountry(congo);
    southAfrica.setTroops(14);
    congo.setTroops(4);
    Country madagascar = getCountryByName(CountryName.MADAGASCAR);
    Country eastAfrica = getCountryByName(CountryName.EAST_AFRICA);
    Country northAfrica = getCountryByName(CountryName.NORTH_AFRICA);
    enemyController.addCountry(madagascar);
    enemyController.addCountry(eastAfrica);
    enemyController.addCountry(northAfrica);
    madagascar.setTroops(4);
    eastAfrica.setTroops(6);
    northAfrica.setTroops(1);
    Fortify fortify = mediumBot.createFortify();
    assertEquals(southAfrica, fortify.getOutgoing());
    assertEquals(congo, fortify.getIncoming());
    assertEquals(4, fortify.getTroopsToMove());
    southAfrica.setTroops(10);
    fortify = mediumBot.createFortify();
    assertEquals(2, fortify.getTroopsToMove());
  }

  @Test
  void sortContinentsTest() {
    Country westernEur = getCountryByName(CountryName.WESTERN_EUROPE);
    botTestController.addCountry(westernEur);
    westernEur.setTroops(9);
    Country britain = getCountryByName(CountryName.GREAT_BRITAIN);
    enemyController.addCountry(britain);
    britain.setTroops(1);
    Country china = getCountryByName(CountryName.CHINA);
    botTestController.addCountry(china);
    china.setTroops(6);
    Country japan = getCountryByName(CountryName.JAPAN);
    enemyController.addCountry(japan);
    japan.setTroops(4);
    ((MediumBot) mediumBot).sortContinentsByHighestRelativePower();
    assertEquals(westernEur.getContinent(), ((MediumBot) mediumBot).getAllContinents().get(0));
    assertEquals(china.getContinent(), ((MediumBot) mediumBot).getAllContinents().get(1));
  }

  static Country getCountryByName(CountryName countryName) {
    return gameState.getCountries().stream().filter(n -> n.getCountryName().equals(countryName))
        .findFirst().orElse(null);
  }
}