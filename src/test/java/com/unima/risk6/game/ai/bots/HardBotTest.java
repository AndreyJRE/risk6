package com.unima.risk6.game.ai.bots;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.game.models.enums.GamePhase;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HardBotTest {

  static AiBot hardBot;
  static PlayerController botTestController;
  static Player enemy;
  static PlayerController enemyController;
  static GameState gameState;

  @BeforeAll
  static void setUp() {
    InputStream data = MediumBotTest.class.getResourceAsStream(
        "/com/unima/risk6/json/probabilities.json");
    assert data != null;
    InputStreamReader fileReader = new InputStreamReader(data);
    Probabilities.init(fileReader);
    hardBot = new HardBot("Otto the Botto");
    botTestController = new PlayerController();
    botTestController.setPlayer((HardBot) hardBot);
    enemy = new Player();
    List<AiBot> bots = new ArrayList<>();
    List<String> humans = new ArrayList<>();
    humans.add("Pedro the guy");
    bots.add(hardBot);
    gameState = GameConfiguration.configureGame(humans, bots);
    for (Player p : gameState.getActivePlayers()) {
      if (p.getUser().equals("Pedro the guy")) {
        enemy = p;
      }
    }
    enemyController = new PlayerController();
    enemyController.setPlayer(enemy);
    if (gameState.getCurrentPlayer().equals(enemy)) {
      gameState.getActivePlayers().poll();
      gameState.getActivePlayers().add(enemy);
      gameState.setCurrentPlayer((Player) hardBot);
    }
  }

  @BeforeEach
  void resetState() {
    gameState.getCountries().forEach(c -> {
      c.setTroops(0);
      c.setPlayer(null);
      c.setHasPlayer(false);
    });
    ((HardBot) hardBot).setContinentsCopy(gameState.getContinents());
  }

  @Test
  void createAllReinforcements() {
    Country middleEast = getCountryByName(CountryName.MIDDLE_EAST);
    botTestController.addCountry(middleEast);
    middleEast.setTroops(2);
    botTestController.changeDeployableTroops(4);
    for (Country adj : middleEast.getAdjacentCountries()) {
      enemyController.addCountry(adj);
      adj.setTroops(1);
    }
    ((Player) hardBot).setCurrentPhase(GamePhase.REINFORCEMENT_PHASE);
    enemy.setCurrentPhase(GamePhase.NOT_ACTIVE);
    ((HardBot) hardBot).setCurrentGameState(gameState);
    List<Reinforce> decisions = hardBot.createAllReinforcements();
    assertEquals(4, decisions.stream().mapToInt(Reinforce::getToAdd).sum());
    List<CountryPair> attacks = new ArrayList<>();
    CountryPair attack = hardBot.createAttack();
  }

  @Test
  void createAttack() {
  }

  @Test
  void createFortify() {
  }

  static Country getCountryByName(CountryName countryName) {
    return gameState.getCountries().stream().filter(n -> n.getCountryName().equals(countryName))
        .findFirst().orElse(null);
  }
}