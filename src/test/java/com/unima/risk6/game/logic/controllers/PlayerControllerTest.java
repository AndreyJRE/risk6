package com.unima.risk6.game.logic.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.game.models.enums.GamePhase;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerControllerTest {

  static PlayerController playerController;
  static Player player;

  static GameState gameState;

  @BeforeAll
  static void setUp() {
    playerController = new PlayerController();

    //make a Test GameState to have a realistic test environment
    ArrayList<String> playerList = new ArrayList<>();
    ArrayList<AiBot> bots = new ArrayList<>();
    playerList.add("Arnold");
    playerList.add("Test");
    playerList.add("andrey");
    GameConfiguration.setGameState(GameConfiguration.configureGame(playerList, bots));
    gameState = GameConfiguration.getGameState();


  }

  @BeforeEach
  void setPlayerForPlayerController() {
    //player is Player with user= "Arnold";
    player = gameState.getActivePlayers().peek();
    playerController.setPlayer(player);
    clearCountries();

  }

  @Test
  void successfulInit() {
    assertNotNull(player);
    assertNotNull(playerController);
    assertNotNull(gameState);
  }

  @Test
  void addCountryTest() {

    playerController.addCountry(getCountryByCountryName(CountryName.ALASKA));
    assertTrue(getCountryByCountryName(CountryName.ALASKA).hasPlayer());
    assertEquals(1, playerController.getNumberOfCountries());
    //Should not add country another time
    playerController.addCountry(getCountryByCountryName(CountryName.ALASKA));
    assertEquals(1, playerController.getNumberOfCountries());
    playerController.addCountry(getCountryByCountryName(CountryName.CHINA));
  }

  @Test
  void removeCountryTest() {
    //Should add one country and remove it
    playerController.addCountry(getCountryByCountryName(CountryName.CHINA));
    assertEquals(1, playerController.getNumberOfCountries());
    //Should not remove countries not owned
    playerController.removeCountry(getCountryByCountryName(CountryName.ALASKA));
    assertEquals(1, playerController.getNumberOfCountries());

    playerController.removeCountry(getCountryByCountryName(CountryName.CHINA));
    assertEquals(0, playerController.getNumberOfCountries());

  }

  @Test
  void setPlayerTest() {
    //Should change the player and hand that is currently controlled
    Hand handOfCurrentlyControlledPlayer = player.getHand();
    Player currentlyControlledPlayer = player;
    Player playerOutOfQueue = new Player();
    playerController.setPlayer(playerOutOfQueue);
    assertNotEquals(playerController.getPlayer().getHand(), handOfCurrentlyControlledPlayer);
    assertEquals(playerController.getPlayer().getHand(), playerOutOfQueue.getHand());
    //change it back to Player in Queue while also changing the hand that is controlled
    playerController.setPlayer(player);
    assertEquals(playerController.getPlayer().getHand(), handOfCurrentlyControlledPlayer);
    assertNotEquals(playerController.getPlayer().getHand(), playerOutOfQueue.getHand());

  }

  @Test
  void handInCardsTest() {

    //TODO Move to Class with all controllers
    /*
    //Adds exchangeable cards to hand of currently controlled player
    Card[] cards = new Card[3];
    cards[0] = new Card(CardSymbol.CAVALRY, CountryName.ALASKA);
    cards[1] = new Card(CardSymbol.CAVALRY, CountryName.KAMCHATKA);
    cards[2] = new Card(CardSymbol.CAVALRY, CountryName.CONGO);
    playerController.getHandController().getHand().getCards().add(cards[0]);
    playerController.getHandController().getHand().getCards().add(cards[1]);
    playerController.getHandController().getHand().getCards().add(cards[2]);

    playerController.getHandController().selectExchangeableCards();
    assertTrue(playerController.getHandController().isExchangeable());
    //The first hand in should give 4 Troops
    playerController.handInCards(1);
    assertEquals(4, playerController.getPlayer().getDeployableTroops());

    player.setDeployableTroops(0);
    //The second hand in should give 6 Troops
    playerController.getHandController().getHand().getCards().add(cards[0]);
    playerController.getHandController().getHand().getCards().add(cards[1]);
    playerController.getHandController().getHand().getCards().add(cards[2]);
    playerController.getHandController().selectExchangeableCards();
    playerController.handInCards(2);
    assertEquals(6, playerController.getPlayer().getDeployableTroops());

    player.setDeployableTroops(0);
    //The 6th hand in should give 15 troops
    playerController.getHandController().getHand().getCards().add(cards[0]);
    playerController.getHandController().getHand().getCards().add(cards[1]);
    playerController.getHandController().getHand().getCards().add(cards[2]);
    playerController.getHandController().selectExchangeableCards();
    playerController.handInCards(6);
    assertEquals(15, playerController.getPlayer().getDeployableTroops());

    player.setDeployableTroops(0);
    //The 7th hand in should give 20 troops
    playerController.getHandController().getHand().getCards().add(cards[0]);
    playerController.getHandController().getHand().getCards().add(cards[1]);
    playerController.getHandController().getHand().getCards().add(cards[2]);
    playerController.getHandController().selectExchangeableCards();
    playerController.handInCards(7);
    assertEquals(20, playerController.getPlayer().getDeployableTroops());

     */

  }

  //TODO Test methods that give you a List of CountryPair
  Country getCountryByCountryName(CountryName countryName) {
    final Country[] country = new Country[1];
    gameState.getCountries().stream().filter(n -> n.getCountryName().equals(countryName))
        .forEach(n -> country[0] = n);
    return country[0];

  }

  //Clears all countries possessed by player and resets owner of those countries to null.
  void clearCountries() {
    player.getCountries().forEach(n -> n.setPlayer(null));
    player.getCountries().clear();


  }

  @Test
  void validFortifyFromCountryTest() {
    Country middleEast = getCountryByCountryName(CountryName.MIDDLE_EAST);
    Country afghanistan = getCountryByCountryName(CountryName.AFGHANISTAN);
    Country ukraine = getCountryByCountryName(CountryName.UKRAINE);
    Country egypt = getCountryByCountryName(CountryName.EGYPT);
    playerController.addCountry(middleEast);
    playerController.addCountry(afghanistan);
    playerController.addCountry(ukraine);
    middleEast.setTroops(5);
    afghanistan.setTroops(1);
    egypt.setPlayer(new Player("T.E Lawrence"));
    CountryPair validPair1 = new CountryPair(middleEast, afghanistan);
    CountryPair validPair2 = new CountryPair(middleEast, ukraine);
    CountryPair notValid = new CountryPair(middleEast, egypt);
    List<CountryPair> result = playerController.getValidFortifiesFromCountry(middleEast);
    assertEquals(2, result.size());
    assertTrue(result.contains(validPair1));
    assertTrue(result.contains(validPair2));
    assertFalse(result.contains(notValid));
    List<CountryPair> cantFortify = playerController.getValidFortifiesFromCountry(afghanistan);
    assertEquals(0, cantFortify.size());
  }

  @Test
  void validAttacksFromCountryTest() {
    Country japan = getCountryByCountryName(CountryName.JAPAN);
    playerController.addCountry(japan);
    japan.setTroops(1);
    List<CountryPair> attacks = playerController.getValidCountryPairsFromCountry(japan);
    assertEquals(0, attacks.size());
    japan.setTroops(5);
    attacks = playerController.getValidCountryPairsFromCountry(japan);
    assertEquals(0, attacks.size());
    for (Country adj : japan.getAdjacentCountries()) {
      adj.setHasPlayer(true);
      adj.setTroops(2);
    }
    attacks = playerController.getValidCountryPairsFromCountry(japan);
    assertEquals(japan.getAdjacentCountries().size(), attacks.size());
    for (Country enemy : japan.getAdjacentCountries()) {
      assertTrue(attacks.contains(new CountryPair(japan, enemy)));
    }
  }

  @Test
  void validAttacksfromContinentTest() {
    Player enemy = new Player("Juror 5");
    PlayerController enemyController = new PlayerController();
    enemyController.setPlayer(enemy);
    Country alaska = getCountryByCountryName(CountryName.ALASKA);
    Country eastern = getCountryByCountryName(CountryName.EASTERN_UNITED_STATES);
    playerController.addCountry(alaska);
    playerController.addCountry(eastern);
    alaska.setTroops(5);
    eastern.setTroops(2);
    for (Country adj : eastern.getAdjacentCountries()) {
      playerController.addCountry(adj);
      adj.setTroops(1);
    }
    List<CountryPair> expected = new ArrayList<>();
    for (Country adj : alaska.getAdjacentCountries()) {
      enemyController.addCountry(adj);
      adj.setTroops(1);
      expected.add(new CountryPair(alaska, adj));
    }
    Continent usa = alaska.getContinent();
    player.setCurrentPhase(GamePhase.ATTACK_PHASE);
    List<CountryPair> results = playerController.getAllValidCountryPairs(usa);
    assertEquals(expected, results);
  }

  @Test
  void allValidFortifiesTest() {
    Country greenland = getCountryByCountryName(CountryName.GREENLAND);
    Country quebec = getCountryByCountryName(CountryName.QUEBEC);
    Country yakutsk = getCountryByCountryName(CountryName.YAKUTSK);
    Country irkutsk = getCountryByCountryName(CountryName.IRKUTSK);
    playerController.addCountry(greenland);
    playerController.addCountry(quebec);
    playerController.addCountry(yakutsk);
    playerController.addCountry(irkutsk);
    greenland.setTroops(5);
    quebec.setTroops(3);
    yakutsk.setTroops(2);
    irkutsk.setTroops(1);
    List<CountryPair> expected = new ArrayList<>();
    expected.add(new CountryPair(greenland, quebec));
    expected.add(new CountryPair(yakutsk, irkutsk));
    expected.add(new CountryPair(quebec, greenland));
    List<CountryPair> results = playerController.getAllValidFortifies();
    assertTrue(expected.containsAll(results));
  }

}