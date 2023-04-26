package com.unima.risk6.game.logic.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.ArrayList;
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
    //The first hand in should give
    playerController.handInCards(1);
    assertEquals(4, playerController.getPlayer().getDeployableTroops());


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

}