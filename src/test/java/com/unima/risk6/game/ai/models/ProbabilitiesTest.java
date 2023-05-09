package com.unima.risk6.game.ai.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ProbabilitiesTest {

  static PlayerController playerController = new PlayerController();
  static PlayerController enemyController = new PlayerController();
  static Player player;
  static Player enemy;
  static GameState gameState;

  @BeforeAll
  static void setUp() {
    Probabilities.init();
    List<String> players = new ArrayList<>();
    players.add("John McClane");
    players.add("Jerry Seinfeld");
    gameState = GameConfiguration.configureGame(players, new ArrayList<>());
    player = gameState.getActivePlayers().poll();
    enemy = gameState.getActivePlayers().poll();
    gameState.getActivePlayers().add(player);
    gameState.getActivePlayers().add(enemy);
    gameState.setCurrentPlayer(player);
    playerController.setPlayer(player);
    enemyController.setPlayer(enemy);
    Country brazil = getCountryByName(CountryName.BRAZIL);
    playerController.addCountry(brazil);
    brazil.setTroops(15);
    Country peru = getCountryByName(CountryName.PERU);
    enemyController.addCountry(peru);
    peru.setTroops(2);
    Country southAfrica = getCountryByName(CountryName.SOUTH_AFRICA);
    enemyController.addCountry(southAfrica);
    southAfrica.setTroops(7);
  }

  @Test
  void relativeTroopContinentPowerTest() {
    Continent southAmerica = getCountryByName(CountryName.BRAZIL).getContinent();
    double playerPower = Probabilities.relativeTroopContinentPower(player, southAmerica);
    assertTrue(Math.abs(0.88235 - playerPower) < 1E-4);
    double enemyPower = Probabilities.relativeTroopContinentPower(enemy, southAmerica);
    assertTrue(Math.abs((1 - playerPower) - enemyPower) < 1E-4);
    Continent africa = getCountryByName(CountryName.SOUTH_AFRICA).getContinent();
    assertEquals(0, Probabilities.relativeTroopContinentPower(player, africa));
    assertEquals(1, Probabilities.relativeTroopContinentPower(enemy, africa));
  }

  @Test
  void getWinProbabilityTest() {
    // method should adjust for index and adjust for matrix applying for attacking player count
    // index [4,2]
    assertEquals(79, Probabilities.getWinProbability(5, 2));
    assertEquals(29, Probabilities.getWinProbability(14, 18));
    // should adjust for index out of bounds
    assertEquals(69, Probabilities.getWinProbability(30, 19));
  }

  @Test
  void findStrongestPlayerTest() {
    assertEquals(player, Probabilities.findStrongestPlayer(gameState));
  }

  @Test
  void initTest() {
    assertEquals(20, Probabilities.getWinProbabilityArray().length);
    for (int i = 0; i < Probabilities.getWinProbabilityArray().length; i++) {
      assertEquals(20, Probabilities.getWinProbabilityArray()[i].length,
          "Position " + (i + 1) + " not correct");
    }
  }

  static Country getCountryByName(CountryName countryName) {
    return gameState.getCountries().stream().filter(n -> n.getCountryName().equals(countryName))
        .findFirst().orElse(null);
  }
}