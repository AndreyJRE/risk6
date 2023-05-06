package com.unima.risk6.game.ai.montecarlo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.bots.HardBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MonteCarloTreeSearchTest {

  static GameState gameState;
  static HardBot ourBot;

  @BeforeAll
  static void beforeAll() {
    List<String> randomHumans = new ArrayList<>();
    randomHumans.add("Steven");
    ourBot = new HardBot("Popp");
    List<AiBot> bots = new ArrayList<>();
//    bots.add(new EasyBot("Tested"));
    bots.add(ourBot);
    gameState = GameConfiguration.configureGame(randomHumans, bots);
  }

  @Test
  void copyGameStateTest() {
    assertEquals(2, gameState.getActivePlayers().size());
    Player originalHumanPlayer = gameState.getActivePlayers().poll();
    Player originalBotPlayer = gameState.getActivePlayers().poll();
    gameState.getActivePlayers().add(originalHumanPlayer);
    gameState.getActivePlayers().add(originalBotPlayer);
    assertEquals(2, gameState.getActivePlayers().size());
    originalHumanPlayer.setDeployableTroops(6);
    MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(ourBot);
    GameState gameStateCopy = mcts.copyGameState(gameState);
    Player copiedHumanPlayer = gameStateCopy.getActivePlayers().poll();
    Player copiedBotPlayer = gameStateCopy.getActivePlayers().poll();
    gameStateCopy.getActivePlayers().add(copiedHumanPlayer);
    gameStateCopy.getActivePlayers().add(copiedBotPlayer);
    assertEquals(2, gameStateCopy.getActivePlayers().size());
    assertEquals(originalHumanPlayer, copiedHumanPlayer);
    assertEquals(originalHumanPlayer.getDeployableTroops(),
        copiedHumanPlayer.getDeployableTroops());
    assertNotSame(originalHumanPlayer, copiedHumanPlayer);
    assertEquals(originalBotPlayer, copiedBotPlayer);
    assertNotSame(originalBotPlayer, copiedBotPlayer); //TODO: try with bots that don't get swapped
    Country originalPeru = getCountryFromGameState(gameState, CountryName.PERU);
    Country copyPeru = getCountryFromGameState(gameStateCopy, CountryName.PERU);
    assertEquals(originalPeru, copyPeru);
    assertNotSame(originalPeru, copyPeru);
  }

  Country getCountryFromGameState(GameState gameState, CountryName countryName) {
    return gameState.getCountries().stream().filter(c -> c.getCountryName().equals(countryName))
        .findFirst().orElse(null);
  }
}