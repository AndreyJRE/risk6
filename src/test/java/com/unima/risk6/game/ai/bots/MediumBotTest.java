package com.unima.risk6.game.ai.bots;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
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
    InputStream data = MediumBotTest.class.getResourceAsStream(
        "/com/unima/risk6/json/probabilities.json");
    try {
      assert data != null;
      try (InputStreamReader fileReader = new InputStreamReader(data)) {
        Probabilities.init(fileReader);
      }
    } catch (IOException e) {
      fail("Probabilities could not be loaded");
    }
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
    ((MediumBot) mediumBot).updateContinentsCopy(gameState.getContinents());
  }

  @Test
  void moveAfterAttack() {
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
    Continent australia = getCountryByName(CountryName.WESTERN_AUSTRALIA).getContinent();
    // prioritizes australia
    Reinforce first = mediumBot.claimCountry();
    assertEquals(australia, first.getCountry().getContinent());
    // but will look elsewhere if owned more
    Country alberta = getCountryByName(CountryName.ALBERTA);
    botTestController.addCountry(alberta);
    Reinforce smart = mediumBot.claimCountry();
    assertEquals(alberta.getContinent(), smart.getCountry().getContinent());

  }

  @Test
  void createAllReinforcementsTest1() {
    ((MediumBot) mediumBot).setDeployableTroops(15);
    Country indonesia = getCountryByName(CountryName.INDONESIA);
    Country newGuinea = getCountryByName(CountryName.NEW_GUINEA);
    Country westernAus = getCountryByName(CountryName.WESTERN_AUSTRALIA);
    botTestController.addCountry(indonesia);
    indonesia.setTroops(4);
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
    // should add two that's the max available to venezuela
    reinforcements = mediumBot.createAllReinforcements();
    assertEquals(2, reinforcements.size());
    assertEquals(2, reinforcements.get(1).getToAdd());
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
    Country easternAus = getCountryByName(CountryName.EASTERN_AUSTRALIA);
    Country newGuinea = getCountryByName(CountryName.NEW_GUINEA);
    Country indonesia = getCountryByName(CountryName.INDONESIA);
    Country siam = getCountryByName(CountryName.SIAM);
    botTestController.addCountry(westernAus);
    westernAus.setTroops(8);
    botTestController.addCountry(indonesia);
    indonesia.setTroops(5);
    enemyController.addCountry(easternAus);
    easternAus.setTroops(2);
    enemyController.addCountry(newGuinea);
    newGuinea.setTroops(5);
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
    assertEquals(5, fortify.getTroopsToMove());
  }

  @Test
  void sortContinentsTest() {
    Country westernEur = getCountryByName(CountryName.WESTERN_EUROPE);
    botTestController.addCountry(westernEur);
    westernEur.setTroops(5);
    Country china = getCountryByName(CountryName.CHINA);
    botTestController.addCountry(china);
    china.setTroops(2);
    ((MediumBot) mediumBot).sortContinentsByHighestRelativePower();
    assertEquals(westernEur.getContinent(), ((MediumBot) mediumBot).getContinentsCopy().get(0));
    assertEquals(china.getContinent(), ((MediumBot) mediumBot).getContinentsCopy().get(1));
  }

  static Country getCountryByName(CountryName countryName) {
    return gameState.getCountries().stream().filter(n -> n.getCountryName().equals(countryName))
        .findFirst().orElse(null);
  }
}