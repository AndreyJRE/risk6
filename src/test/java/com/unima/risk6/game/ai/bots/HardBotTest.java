package com.unima.risk6.game.ai.bots;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.Probabilities;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.DeckController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.ContinentName;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.game.models.enums.GamePhase;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
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

  }

//  @BeforeEach
//  void resetState() {
//    gameState.getCountries().forEach(c -> {
//      c.setTroops(0);
//      c.setPlayer(null);
//      c.setHasPlayer(false);
//    });
//  }

  @Test
  void getBestMovesTest() {
    hardBot = new HardBot("Otto the Botto");
    botTestController = new PlayerController();
    botTestController.setPlayer((HardBot) hardBot);
    enemy = new Player();
    List<AiBot> bots = new ArrayList<>();
    List<String> humans = new ArrayList<>();
    bots.add(hardBot);
    bots.add(new EasyBot("Pedro the guy"));
    gameState = GameConfiguration.configureGame(humans, bots);
    ((HardBot) hardBot).setContinentsCopy(gameState.getContinents());
    DeckController deckController = new DeckController(gameState.getDeck());
    deckController.initDeck();
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
    ((EasyBot) enemy).setCurrentGameState(gameState);
    gameState.getCountries().stream()
        .filter(c -> !c.getCountryName().equals(CountryName.MIDDLE_EAST)).forEach(c -> {
          enemyController.addCountry(c);
          c.setTroops(1);
        });
    Country middleEast = getCountryByName(CountryName.MIDDLE_EAST);
    botTestController.addCountry(middleEast);
    middleEast.setTroops(2);
    botTestController.changeDeployableTroops(4);
    ((Player) hardBot).setCurrentPhase(GamePhase.REINFORCEMENT_PHASE);
    enemy.setCurrentPhase(GamePhase.NOT_ACTIVE);
    ((HardBot) hardBot).setCurrentGameState(gameState);
    List<Reinforce> decisions = hardBot.createAllReinforcements();
    assertEquals(4, decisions.stream().mapToInt(Reinforce::getToAdd).sum());
    List<CountryPair> attacks = new ArrayList<>();
    CountryPair attack = hardBot.createAttack();
  }

  @Test
  void getBestMovesTest2() {
    Random rng = new Random();
    hardBot = new HardBot("Otto the Botto");
    botTestController = new PlayerController();
    botTestController.setPlayer((HardBot) hardBot);
    enemy = new Player();
    List<AiBot> bots = new ArrayList<>();
    List<String> humans = List.of("Mack the Human");
    EasyBot easy = new EasyBot("Pedro the guy");
    PlayerController easyController = new PlayerController();
    easyController.setPlayer(easy);
    MediumBot med = new MediumBot("Joachim the greedy");
    PlayerController medController = new PlayerController();
    medController.setPlayer(med);
    bots.add(hardBot);
    bots.add(easy);
    bots.add(med);
    gameState = GameConfiguration.configureGame(humans, bots);
    ((HardBot) hardBot).setContinentsCopy(gameState.getContinents());
    DeckController deckController = new DeckController(gameState.getDeck());
    deckController.initDeck();
    for (Player p : gameState.getActivePlayers()) {
      if (p.getUser().equals("Mack the Human")) {
        enemy = p;
      }
    }
    enemyController = new PlayerController();
    enemyController.setPlayer(enemy);
    if (!gameState.getCurrentPlayer().equals(hardBot)) {
      gameState.getActivePlayers().poll();
      gameState.getActivePlayers().add(enemy);
      gameState.setCurrentPlayer((Player) hardBot);
    }
    gameState.getCountries().stream()
        .filter(c -> c.getContinent().getContinentName().equals(ContinentName.AUSTRALIA))
        .forEach(c -> {
          enemyController.addCountry(c);
          c.setTroops(rng.nextInt(1, 5));
        });
    gameState.getCountries().stream()
        .filter(c -> c.getContinent().getContinentName().equals(ContinentName.EUROPE))
        .forEach(c -> {
          botTestController.addCountry(c);
          c.setTroops(rng.nextInt(1, 5));
        });
    gameState.getCountries().stream()
        .filter(c -> c.getContinent().getContinentName().equals(ContinentName.NORTH_AMERICA))
        .forEach(c -> {
          medController.addCountry(c);
          c.setTroops(rng.nextInt(1, 5));
        });
    gameState.getCountries().stream()
        .filter(c -> c.getContinent().getContinentName().equals(ContinentName.SOUTH_AMERICA))
        .forEach(c -> {
          easyController.addCountry(c);
          c.setTroops(rng.nextInt(1, 5));
        });
    gameState.getCountries().stream().filter(c -> !c.hasPlayer()).forEach(c -> {
      easyController.addCountry(c);
      c.setTroops(rng.nextInt(1, 5));
    });
    easy.setCurrentGameState(gameState);
    med.setContinentsCopy(gameState.getContinents());
    System.out.println(med.getCountries().size());
    ((HardBot) hardBot).setCurrentGameState(gameState);
    botTestController.getPlayer().setCurrentPhase(GamePhase.REINFORCEMENT_PHASE);
    enemyController.getPlayer().setCurrentPhase(GamePhase.NOT_ACTIVE);
    medController.getPlayer().setCurrentPhase(GamePhase.NOT_ACTIVE);
    easyController.getPlayer().setCurrentPhase(GamePhase.NOT_ACTIVE);
    hardBot.createAllReinforcements();
  }

  static Country getCountryByName(CountryName countryName) {
    return gameState.getCountries().stream().filter(n -> n.getCountryName().equals(countryName))
        .findFirst().orElse(null);
  }
}