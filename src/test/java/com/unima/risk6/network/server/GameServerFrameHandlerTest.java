package com.unima.risk6.network.server;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.DeckController;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Deck;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.ContinentName;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameServerFrameHandlerTest {

  private static GameController gameController;
  private static GameState gameState;
  private static GameConfiguration gameConfiguration;
  private static Player[] players;
  private static ArrayList<String> playerList;
  private static DeckController deckController;
  private static PlayerController playerController;
  private static MoveProcessor moveProcessor;


  @BeforeAll
  static void setUp() {
    playerList = new ArrayList<>();
    ArrayList<AiBot> bots = new ArrayList<>();
    playerList.add("P1");
    playerList.add("P2");
    playerList.add("P3");
    playerList.add("P4");
    playerList.add("P5");
    playerList.add("P6");
  }

  @BeforeEach
  void makeGameStateBlank() {
    ArrayList<AiBot> bots = new ArrayList<>();
    GameConfiguration.setGameState(GameConfiguration.configureGame(playerList, bots));
    gameState = GameConfiguration.getGameState();
    gameController = new GameController(gameState);
    playerController = new PlayerController();
    deckController = new DeckController(new Deck());
    moveProcessor = new MoveProcessor(playerController,
        gameController, deckController);
    players = new Player[6];
    gameState = GameConfiguration.getGameState();
    players[0] = gameState.getActivePlayers().poll();
    players[1] = gameState.getActivePlayers().poll();
    players[2] = gameState.getActivePlayers().poll();
    players[3] = gameState.getActivePlayers().poll();
    players[4] = gameState.getActivePlayers().poll();
    players[5] = gameState.getActivePlayers().poll();

    gameState.getActivePlayers().add(players[0]);
    gameState.getActivePlayers().add(players[1]);
    gameState.getActivePlayers().add(players[2]);
    gameState.getActivePlayers().add(players[3]);
    gameState.getActivePlayers().add(players[4]);
    gameState.getActivePlayers().add(players[5]);
    gameController.setGameState(gameState);

    //Setting up the gameState so that the game is in a playable state
    HashMap<Player, Integer> diceRolls = new HashMap<>();
    diceRolls.put(players[0], 6);
    diceRolls.put(players[1], 5);
    diceRolls.put(players[2], 4);
    diceRolls.put(players[3], 3);
    diceRolls.put(players[4], 2);
    diceRolls.put(players[5], 1);

    Queue<Player> playerQueue = gameController.getNewPlayerOrder(diceRolls);
    gameController.setNewPlayerOrder(playerQueue);
    playerController.setPlayer(players[0]);

  }

  @Test
  void successfulInit() {
    assertNotNull(moveProcessor);
  }

  @Test
  void processReinforceWhileInClaimPhaseTest() {
    players[0].setInitialTroops(20);
    players[1].setInitialTroops(20);
    players[2].setInitialTroops(20);
    players[3].setInitialTroops(20);
    players[4].setInitialTroops(20);
    players[5].setInitialTroops(20);
    System.out.println(players[0].getCurrentPhase());
    System.out.println(players[1].getCurrentPhase());
    System.out.println(players[2].getCurrentPhase());
    System.out.println(players[3].getCurrentPhase());
    System.out.println(players[4].getCurrentPhase());
    System.out.println(players[5].getCurrentPhase());
    moveProcessor.processReinforce(
        new Reinforce(getCountryByCountryName(CountryName.ALASKA), 100));
    System.out.println(getCountryByCountryName(CountryName.ALASKA));

    System.out.println(players[0].getDeployableTroops());
    System.out.println(players[0].getInitialTroops());

    System.out.println(players[0].getCurrentPhase());
    System.out.println(players[1].getCurrentPhase());
    System.out.println(players[2].getCurrentPhase());
    System.out.println(players[3].getCurrentPhase());
    System.out.println(players[4].getCurrentPhase());
    System.out.println(players[5].getCurrentPhase());

    moveProcessor.processReinforce(
        new Reinforce(getCountryByCountryName(CountryName.CHINA), 1));
    System.out.println(getCountryByCountryName(CountryName.CHINA));

  }

  Country getCountryByCountryName(CountryName countryName) {
    final Country[] country = new Country[1];
    gameState.getCountries().stream().filter(n -> n.getCountryName().equals(countryName))
        .forEach(n -> country[0] = n);
    return country[0];

  }

  Continent getContinentByContinentName(ContinentName continentName) {
    final Continent[] continent = new Continent[1];
    gameState.getContinents().stream().filter(n -> n.getContinentName().equals(continentName))
        .forEach(n -> continent[0] = n);
    return continent[0];

  }

  void clearCountries(Player player) {
    player.getCountries().forEach(n -> n.setPlayer(null));
    player.getCountries().clear();


  }

  void addCountryToPlayer(CountryName countryName, Player player) {
    player.getCountries().add(getCountryByCountryName(countryName));
    getCountryByCountryName(countryName).setPlayer(player);
  }

}