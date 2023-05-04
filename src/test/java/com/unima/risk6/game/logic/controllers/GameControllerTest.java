package com.unima.risk6.game.logic.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.ContinentName;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.game.models.enums.GamePhase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {


  private static GameController gameController;
  private static GameState gameState;
  private static GameConfiguration gameConfiguration;
  private Player[] players;
  private static ArrayList<String> playerList;

  @BeforeAll
  static void initGameController() {
    playerList = new ArrayList<>();
    ArrayList<AiBot> bots = new ArrayList<>();
    playerList.add("P1");
    playerList.add("P2");
    playerList.add("P3");
    playerList.add("P4");
    playerList.add("P5");
    playerList.add("P6");
    GameConfiguration.setGameState(GameConfiguration.configureGame(playerList, bots));
    gameState = GameConfiguration.getGameState();
    gameController = new GameController(gameState);
  }

  @BeforeEach
  void makeGameStateBlank() {
    ArrayList<AiBot> bots = new ArrayList<>();
    GameConfiguration.setGameState(GameConfiguration.configureGame(playerList, bots));
    players = new Player[6];
    gameState = GameConfiguration.getGameState();
    gameController = new GameController(gameState);
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


  }

  @Test
  void successfulInit() {
    assertNotNull(gameController);

  }

  //Should add Player into the Queue according to their diceRolls
  @Test
  void setPlayerOrder() {

    HashMap<Player, Integer> diceRolls = new HashMap<>();
    diceRolls.put(players[0], 6);
    diceRolls.put(players[1], 5);
    diceRolls.put(players[2], 4);
    diceRolls.put(players[3], 3);
    diceRolls.put(players[4], 2);
    diceRolls.put(players[5], 1);

    Queue<Player> playerQueue = gameController.getNewPlayerOrder(diceRolls);
    gameController.setNewPlayerOrder(playerQueue);
    //No active players were lost when
    assertEquals(6, gameState.getActivePlayers().size());

    //Player were inserted into the activePlayers queue according to their dice rolls
    //If same Number it will be decided randomly by System, due to Set class.
    assertEquals(players[0], gameState.getActivePlayers().poll());
    assertEquals(players[1], gameState.getActivePlayers().poll());
    assertEquals(players[2], gameState.getActivePlayers().poll());
    assertEquals(players[3], gameState.getActivePlayers().poll());
    assertEquals(players[4], gameState.getActivePlayers().poll());
    assertEquals(players[5], gameState.getActivePlayers().poll());
  }

  @Test
  void generalPhaseFlowTest() {
    //Players should start in ORDER_PHASE
    for (int i = 0; i < 6; i++) {
      assertEquals(GamePhase.ORDER_PHASE, players[i].getCurrentPhase());
    }

    HashMap<Player, Integer> diceRolls = new HashMap<>();
    diceRolls.put(players[0], 6);
    diceRolls.put(players[1], 5);
    diceRolls.put(players[2], 4);
    diceRolls.put(players[3], 3);
    diceRolls.put(players[4], 2);
    diceRolls.put(players[5], 1);

    Queue<Player> playerQueue = gameController.getNewPlayerOrder(diceRolls);
    gameController.setNewPlayerOrder(playerQueue);
    assertEquals(players[0], gameState.getCurrentPlayer());
    for (int i = 0; i < 6; i++) {
      assertEquals(GamePhase.CLAIM_PHASE, players[0].getCurrentPhase());
    }

    //Set the initialTroops according to the rulebook for 6 players.
    for (int i = 0; i < 6; i++) {
      players[i].setInitialTroops(20);
    }

    gameController.nextPhase();
    assertEquals(players[1], gameState.getCurrentPlayer());
    assertEquals(GamePhase.CLAIM_PHASE, players[1].getCurrentPhase());
    gameController.nextPhase();
    assertEquals(players[2], gameState.getCurrentPlayer());
    assertEquals(GamePhase.CLAIM_PHASE, players[2].getCurrentPhase());
    gameController.nextPhase();
    assertEquals(players[3], gameState.getCurrentPlayer());
    assertEquals(GamePhase.CLAIM_PHASE, players[3].getCurrentPhase());
    gameController.nextPhase();

    assertEquals(players[4], gameState.getCurrentPlayer());
    assertEquals(GamePhase.CLAIM_PHASE, players[4].getCurrentPhase());
    gameController.nextPhase();
    assertEquals(players[5], gameState.getCurrentPlayer());
    assertEquals(GamePhase.CLAIM_PHASE, players[5].getCurrentPhase());

    for (int i = 0; i < 6; i++) {
      players[i].setInitialTroops(0);
    }

    gameController.nextPhase();

    //Test a standard turn of Player 1
    assertEquals(players[0], gameState.getCurrentPlayer());
    assertEquals(GamePhase.REINFORCEMENT_PHASE, players[0].getCurrentPhase());
    players[0].setDeployableTroops(0);

    gameController.nextPhase();
    assertEquals(players[0], gameState.getCurrentPlayer());
    assertEquals(GamePhase.ATTACK_PHASE, players[0].getCurrentPhase());
    gameController.nextPhase();
    assertEquals(players[0], gameState.getCurrentPlayer());
    assertEquals(GamePhase.FORTIFY_PHASE, players[0].getCurrentPhase());

    gameController.nextPhase();
    //Should start the turn into Reinforcement_Phase
    assertEquals(players[1], gameState.getCurrentPlayer());
    assertEquals(GamePhase.REINFORCEMENT_PHASE, players[1].getCurrentPhase());
    players[1].setDeployableTroops(0);

    gameController.nextPhase();
    assertEquals(players[1], gameState.getCurrentPlayer());
    assertEquals(GamePhase.ATTACK_PHASE, players[1].getCurrentPhase());
    gameController.nextPhase();
    assertEquals(players[1], gameState.getCurrentPlayer());
    assertEquals(GamePhase.FORTIFY_PHASE, players[1].getCurrentPhase());
    gameController.nextPhase();

    assertEquals(players[2], gameState.getCurrentPlayer());
    assertEquals(GamePhase.REINFORCEMENT_PHASE, players[2].getCurrentPhase());

  }

  //Test if a player that has lost will be removed from active Player queue
  @Test
  void lostPlayerTest() {
    assertEquals(gameState.getCurrentPlayer(), players[0]);
    //Should add 3 Cards in players[1] hand.

    players[1].getHand().getCards().add(new Card(CardSymbol.CAVALRY, CountryName.ALASKA, 1));
    players[1].getHand().getCards().add(new Card(CardSymbol.CAVALRY, CountryName.ALASKA, 2));
    players[1].getHand().getCards().add(new Card(CardSymbol.CAVALRY, CountryName.ALASKA, 3));

    assertEquals(players[0], gameController.getCurrentPlayer());
    assertEquals(0, players[0].getHand().getCards().size());
    assertEquals(3, players[1].getHand().getCards().size());

    gameController.removeLostPlayer(players[1]);
    //Should add all cards of players[1] into players[0] hand.
    assertEquals(3, players[0].getHand().getCards().size());
    assertEquals(0, players[1].getHand().getCards().size());

    assertEquals(5, gameState.getActivePlayers().size());
    assertFalse(gameState.getActivePlayers().contains(players[1]));
    assertTrue(gameState.getLostPlayers().contains(players[1]));
    assertEquals(1, gameState.getLostPlayers().size());


  }

  @Test
  void phaseFlowTestWithLosingPlayers() {
    HashMap<Player, Integer> diceRolls = new HashMap<>();
    diceRolls.put(players[0], 6);
    diceRolls.put(players[1], 5);
    diceRolls.put(players[2], 4);
    diceRolls.put(players[3], 3);
    diceRolls.put(players[4], 2);
    diceRolls.put(players[5], 1);

    Queue<Player> playerQueue = gameController.getNewPlayerOrder(diceRolls);
    gameController.setNewPlayerOrder(playerQueue);

    for (int i = 0; i < 6; i++) {
      players[i].setInitialTroops(1);
    }

    gameController.nextPhase();
    assertEquals(players[1], gameState.getCurrentPlayer());
    gameController.nextPhase();
    assertEquals(players[2], gameState.getCurrentPlayer());
    gameController.removeLostPlayer(players[3]);
    gameController.nextPhase();
    assertEquals(players[4], gameState.getCurrentPlayer());
    gameController.removeLostPlayer(players[5]);
    gameController.nextPhase();
    assertEquals(players[0], gameState.getCurrentPlayer());

  }

  @Test
  void deployableTroopsCalculationTest() {
    gameController.calculateDeployableTroops();
    assertEquals(3, players[0].getDeployableTroops());
    //Give Player countries that are not a continent to check if correct amount of
    //deployable troops are assigned
    //Added 11 countries, it should assign 3 troops to Player
    addCountryToPlayer(CountryName.ALASKA, players[0]);
    addCountryToPlayer(CountryName.AFGHANISTAN, players[0]);
    addCountryToPlayer(CountryName.ARGENTINA, players[0]);
    addCountryToPlayer(CountryName.ALBERTA, players[0]);
    addCountryToPlayer(CountryName.BRAZIL, players[0]);

    addCountryToPlayer(CountryName.CHINA, players[0]);
    addCountryToPlayer(CountryName.CONGO, players[0]);
    addCountryToPlayer(CountryName.CENTRAL_AMERICA, players[0]);
    addCountryToPlayer(CountryName.EGYPT, players[0]);
    addCountryToPlayer(CountryName.EAST_AFRICA, players[0]);
    addCountryToPlayer(CountryName.EASTERN_UNITED_STATES, players[0]);

    assertEquals(3, players[0].getDeployableTroops());

    //Adds Countries one by one and check if correct number of troops is given
    addCountryToPlayer(CountryName.EASTERN_AUSTRALIA, players[0]);
    gameController.calculateDeployableTroops();
    assertEquals(4, players[0].getDeployableTroops());

    addCountryToPlayer(CountryName.GREENLAND, players[0]);
    gameController.calculateDeployableTroops();
    assertEquals(4, players[0].getDeployableTroops());

    addCountryToPlayer(CountryName.GREAT_BRITAIN, players[0]);
    gameController.calculateDeployableTroops();
    assertEquals(4, players[0].getDeployableTroops());

    addCountryToPlayer(CountryName.ICELAND, players[0]);
    gameController.calculateDeployableTroops();
    assertEquals(5, players[0].getDeployableTroops());


  }


  @Test
  void updateContinentsTest() {
    getContinentByContinentName(ContinentName.AUSTRALIA).getCountries()
        .forEach(n -> addCountryToPlayer(n.getCountryName(), players[0]));
    assertEquals(4, players[0].getCountries().size());
    gameController.updateContinentsOfPlayer(players[0]);
    assertEquals(1, players[0].getContinents().size());

    //Should not add Continent if only one Country of the continent is added
    addCountryToPlayer(CountryName.CONGO, players[0]);
    assertEquals(1, players[0].getContinents().size());
    //If a country of a fully owned continent is removed from the player, the continent
    // should be removed in PLayer class attribute continents after update
    removeCountryFromPlayer(CountryName.EASTERN_AUSTRALIA, players[0]);
    assertEquals(4, players[0].getCountries().size());
    gameController.updateContinentsOfPlayer(players[0]);
    assertEquals(0, players[0].getContinents().size());

    //Adding each continent to test edge case
    getContinentByContinentName(ContinentName.ASIA).getCountries()
        .forEach(n -> addCountryToPlayer(n.getCountryName(), players[0]));
    gameController.updateContinentsForAll();

    assertEquals(1, players[0].getContinents().size());
    getContinentByContinentName(ContinentName.AFRICA).getCountries()
        .forEach(n -> addCountryToPlayer(n.getCountryName(), players[0]));

    getContinentByContinentName(ContinentName.NORTH_AMERICA).getCountries()
        .forEach(n -> addCountryToPlayer(n.getCountryName(), players[0]));

    getContinentByContinentName(ContinentName.AUSTRALIA).getCountries()
        .forEach(n -> addCountryToPlayer(n.getCountryName(), players[0]));

    getContinentByContinentName(ContinentName.SOUTH_AMERICA).getCountries()
        .forEach(n -> addCountryToPlayer(n.getCountryName(), players[0]));

    getContinentByContinentName(ContinentName.EUROPE).getCountries()
        .forEach(n -> addCountryToPlayer(n.getCountryName(), players[0]));

    gameController.updateContinentsForAll();
    assertEquals(6, players[0].getContinents().size());
  }

  @Test
  void deployableTroopsCalculationWithContinentsTest() {
    getContinentByContinentName(ContinentName.AUSTRALIA).getCountries()
        .forEach(n -> addCountryToPlayer(n.getCountryName(), players[0]));
    gameController.calculateDeployableTroops();
    //Should calculate 5: 3 through the country number and 2 bonus troops for Australian continent
    assertEquals(5, players[0].getDeployableTroops());

    clearCountries(players[0]);

    getContinentByContinentName(ContinentName.ASIA).getCountries()
        .forEach(n -> addCountryToPlayer(n.getCountryName(), players[0]));
    gameController.calculateDeployableTroops();
    // Should calculate 11: 4 through the countries of asia: 12/3=4 and 7 as bonus from full Asian Continent
    assertEquals(11, players[0].getDeployableTroops());

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

  void removeCountryFromPlayer(CountryName countryName, Player player) {
    player.getCountries().remove(getCountryByCountryName(countryName));
    getCountryByCountryName(countryName).setPlayer(null);
  }


}