package com.unima.risk6.network.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.game.ai.AiBot;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.EndPhase;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.HandIn;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.DeckController;
import com.unima.risk6.game.logic.controllers.GameController;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Deck;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import com.unima.risk6.game.models.enums.GamePhase;
import com.unima.risk6.network.server.exceptions.InvalidMoveException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MoveProcessorTest {

  private static GameController gameController;
  private static GameState gameState;
  private static Player[] players;
  private static ArrayList<String> playerList;
  private static DeckController deckController;
  private static PlayerController playerController;
  private static MoveProcessor moveProcessor;


  @BeforeAll
  static void setUp() {
    playerList = new ArrayList<>();
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
    deckController.initDeck();
    moveProcessor = new MoveProcessor(playerController,
        gameController, deckController);
    players = new Player[6];
    gameState = GameConfiguration.getGameState();
    for (int i = 0; i < 6; i++) {
      players[i] = gameState.getActivePlayers().poll();
    }

    for (int i = 0; i < 6; i++) {
      gameState.getActivePlayers().add(players[i]);
    }
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
    for (int i = 0; i < 6; i++) {
      players[i].setInitialTroops(20);
    }

    //Added Each Country to a player.
    for (Country c : gameState.getCountries()) {
      moveProcessor.processReinforce(
          new Reinforce(getCountryByCountryName(c.getCountryName()), 1));
      moveProcessor.processEndPhase(
          new EndPhase(GamePhase.REINFORCEMENT_PHASE));

    }

    //assertEquals(2, getCountryByCountryName(CountryName.ONTARIO).getTroops());

    //Reinforced the first country in the ArrayList of owned countries
    // until every player has no more initial troops.

    while (!gameState.getCurrentPlayer().getCurrentPhase()
        .equals(GamePhase.REINFORCEMENT_PHASE)) {

      List<Country> countries = new ArrayList<>(gameState.getCurrentPlayer().getCountries());
      moveProcessor.processReinforce(
          new Reinforce(countries.get(0), 1));
      moveProcessor.processEndPhase(
          new EndPhase(GamePhase.REINFORCEMENT_PHASE));


    }

    assertEquals(0, players[0].getInitialTroops());
    assertEquals(0, players[1].getInitialTroops());
    assertEquals(0, players[2].getInitialTroops());
    assertEquals(0, players[3].getInitialTroops());
    assertEquals(0, players[4].getInitialTroops());
    assertEquals(0, players[5].getInitialTroops());


  }


  @Test
  void processAttackGeneralTest() {

    //Give "P1" players[0]
    //Attacking countries
    addCountryToPlayer(CountryName.ARGENTINA, players[0]);
    addCountryToPlayer(CountryName.GREENLAND, players[0]);
    addCountryToPlayer(CountryName.ONTARIO, players[0]);

    getCountryByCountryName(CountryName.GREENLAND).setTroops(8);
    getCountryByCountryName(CountryName.ARGENTINA).setTroops(5);
    getCountryByCountryName(CountryName.ONTARIO).setTroops(8);

    //defending countries
    addCountryToPlayer(CountryName.ICELAND, players[1]);
    addCountryToPlayer(CountryName.PERU, players[1]);
    addCountryToPlayer(CountryName.QUEBEC, players[1]);

    getCountryByCountryName(CountryName.ICELAND).setTroops(1);
    getCountryByCountryName(CountryName.PERU).setTroops(1);
    getCountryByCountryName(CountryName.QUEBEC).setTroops(1);

    //make Reinforce in a country owned by players[0].
    players[0].setCurrentPhase(GamePhase.REINFORCEMENT_PHASE);
    players[0].setDeployableTroops(3);
    moveProcessor.processReinforce(
        new Reinforce(getCountryByCountryName(CountryName.ARGENTINA), 3));
    moveProcessor.processEndPhase(new EndPhase(GamePhase.REINFORCEMENT_PHASE));

    //Test 3 Attacks with all different Troop number.
    Attack testAttack0 = new Attack(
        getCountryByCountryName(CountryName.ARGENTINA),
        getCountryByCountryName(CountryName.PERU),
        3);

    Player defender = testAttack0.getDefendingCountry().getPlayer();
    Player attacker = testAttack0.getAttackingCountry().getPlayer();

    moveProcessor.processAttack(testAttack0);

    if (testAttack0.getHasConquered()) {
      moveProcessor.processFortify(
          new Fortify(testAttack0.getAttackingCountry(), testAttack0.getDefendingCountry(),
              testAttack0.getTroopNumber()));
      //Owner of Peru should change to the attacker
      assertEquals(attacker, getCountryByCountryName(CountryName.PERU).getPlayer());
      //Automatic fortify should move 3 troops to Peru if conquered.
      assertEquals(3, getCountryByCountryName(CountryName.PERU).getTroops());
      assertTrue(gameController.getCurrentPlayer().getHasConquered());
    } else {
      //Country should stay with the original owner.
      assertEquals(defender, getCountryByCountryName(CountryName.PERU).getPlayer());
      //Attacking Country should have lost 1 army
      assertEquals(7, getCountryByCountryName(CountryName.ARGENTINA).getTroops());
      //Defending Country should be unchanged.
      assertEquals(1, getCountryByCountryName(CountryName.PERU).getTroops());
    }

    Attack testAttack1 = new Attack(
        getCountryByCountryName(CountryName.GREENLAND),
        getCountryByCountryName(CountryName.ICELAND),
        2);

    defender = testAttack1.getDefendingCountry().getPlayer();
    attacker = testAttack1.getAttackingCountry().getPlayer();

    moveProcessor.processAttack(testAttack1);

    if (testAttack1.getHasConquered()) {
      moveProcessor.processFortify(
          new Fortify(testAttack1.getAttackingCountry(), testAttack1.getDefendingCountry(),
              testAttack1.getTroopNumber()));
      assertEquals(attacker, getCountryByCountryName(CountryName.ICELAND).getPlayer());
      //Automatic fortify should move 2 troops to Iceland if conquered.
      assertEquals(2, getCountryByCountryName(CountryName.ICELAND).getTroops());
      //Should have 2 troops less than the troops used for the attack get moved to conquered
      // country
      assertEquals(6,
          getCountryByCountryName(CountryName.GREENLAND).getTroops());
    } else {
      assertEquals(defender, getCountryByCountryName(CountryName.ICELAND).getPlayer());
      //Attacking Country should have lost 1 army
      assertEquals(7, getCountryByCountryName(CountryName.GREENLAND).getTroops());
      //Defending Country should be unchanged.
      assertEquals(1, getCountryByCountryName(CountryName.ICELAND).getTroops());
    }

    Attack testAttack2 = new Attack(
        getCountryByCountryName(CountryName.GREENLAND),
        getCountryByCountryName(CountryName.QUEBEC),
        1);
    getCountryByCountryName(CountryName.QUEBEC).setTroops(1);
    defender = testAttack2.getDefendingCountry().getPlayer();
    attacker = testAttack2.getAttackingCountry().getPlayer();

    moveProcessor.processAttack(testAttack2);

    if (testAttack2.getHasConquered()) {
      moveProcessor.processFortify(
          new Fortify(testAttack2.getAttackingCountry(), testAttack2.getDefendingCountry(),
              testAttack2.getTroopNumber()));
      assertEquals(attacker, getCountryByCountryName(CountryName.QUEBEC).getPlayer());
      //Automatic fortify should move 1 troop to Iceland if conquered.
      assertEquals(1, getCountryByCountryName(CountryName.QUEBEC).getTroops());
    } else {
      assertEquals(defender, getCountryByCountryName(CountryName.QUEBEC).getPlayer());
      //Attacking Country should have lost 1 army
      //Defending Country should be unchanged.
      assertEquals(1, getCountryByCountryName(CountryName.QUEBEC).getTroops());
    }

    getCountryByCountryName(CountryName.GREENLAND).setTroops(3);
    getCountryByCountryName(CountryName.QUEBEC).setTroops(0);

    Attack testAttack3 = new Attack(
        getCountryByCountryName(CountryName.GREENLAND),
        getCountryByCountryName(CountryName.QUEBEC),
        4);
    //Created invalid Attack

    assertThrows(InvalidMoveException.class, () -> moveProcessor.processAttack(testAttack3));
    //Should not call calculateLosses() method in processAttack-->should not change game state.
    assertEquals(0, testAttack3.getDefenderLosses());
    assertEquals(0, testAttack3.getAttackerLosses());
    assertFalse(testAttack3.getHasConquered());

    //Create invalid Attack
    Attack testAttack4 = new Attack(
        getCountryByCountryName(CountryName.GREENLAND),
        getCountryByCountryName(CountryName.CHINA),
        3);

    assertThrows(InvalidMoveException.class, () -> moveProcessor.processAttack(testAttack4));

    //Should not call calculateLosses() method in processAttack-->should not change game state.
    assertEquals(0, testAttack4.getDefenderLosses());
    assertEquals(0, testAttack4.getAttackerLosses());
    assertFalse(testAttack3.getHasConquered());
    try {
      moveProcessor.processEndPhase(new EndPhase(GamePhase.ATTACK_PHASE));

      //Should draw Card if turn is ended and hasConquered is true.
      if (gameController.getCurrentPlayer().getHasConquered()) {
        moveProcessor.processEndPhase(new EndPhase(GamePhase.FORTIFY_PHASE));
        assertEquals(1, players[0].getHand().getCards().size());
      } else {
        moveProcessor.processEndPhase(new EndPhase(GamePhase.FORTIFY_PHASE));
      }

    } catch (InvalidMoveException e) {
      System.err.println(e.getMessage());

    }
  }

  @Test
  void conquerLastCountryOfPlayerTest() {
    addCountryToPlayer(CountryName.CHINA, players[0]);
    addCountryToPlayer(CountryName.INDIA, players[1]);
    getCountryByCountryName(CountryName.CHINA).setTroops(100);
    getCountryByCountryName(CountryName.INDIA).setTroops(1);

    players[0].setCurrentPhase(GamePhase.ATTACK_PHASE);
    while (!players[0].getHasConquered()) {
      try {
        moveProcessor.processAttack(new Attack(getCountryByCountryName(CountryName.CHINA),
            getCountryByCountryName(CountryName.INDIA), 3));
      } catch (InvalidMoveException e) {
        System.err.println(e.getMessage());

      }
    }
    assertFalse(gameState.getActivePlayers().contains(players[1]));
    assertTrue(gameState.getLostPlayers().contains(players[1]));

    //Adds India as only country of the players and then conquers India through China
    //It should add those who lost the last Country(here it is India) to the List of lost players
    for (int i = 2; i < 6; i++) {
      addCountryToPlayer(CountryName.INDIA, players[i]);
      getCountryByCountryName(CountryName.INDIA).setTroops(1);

      while (!getCountryByCountryName(CountryName.INDIA).getPlayer().equals(players[0])) {
        try {
          moveProcessor.processAttack(
              new Attack(getCountryByCountryName(CountryName.CHINA),
                  getCountryByCountryName(CountryName.INDIA), 3));
        } catch (Exception e) {
          System.err.println(e.getMessage());
        }
      }
    }

    assertTrue(gameState.isGameOver());


  }

  @Test
  void drawCardTest() {

    players[0].setCurrentPhase(GamePhase.REINFORCEMENT_PHASE);
    playerController.setPlayer(players[0]);
    moveProcessor.drawCard();
    //Should have removed Card from deck and added it to Hand of Player
    assertEquals(1, players[0].getHand().getCards().size());
    assertFalse(
        deckController.getDeck().getDeckCards()
            .contains(players[0].getHand().getCards().get(0)));
    moveProcessor.drawCard();
    assertEquals(2, players[0].getHand().getCards().size());
    assertFalse(
        deckController.getDeck().getDeckCards()
            .contains(players[0].getHand().getCards().get(1)));

  }

  @Test
  void processHandInTest() {

    players[0].setCurrentPhase(GamePhase.REINFORCEMENT_PHASE);
    playerController.setPlayer(players[0]);
    for (int i = 0; i < 5; i++) {
      moveProcessor.drawCard();
    }
    playerController.getHandController().selectExchangeableCards();
    assertEquals(5, players[0].getHand().getCards().size());
    assertEquals(3, players[0].getHand().getSelectedCards().size());

    moveProcessor.processHandIn(new HandIn(players[0].getHand().getSelectedCards()));

    //Should remove cards in Hand and selected ones and set DeployableTroops to 2.
    assertEquals(0, players[0].getHand().getSelectedCards().size());
    assertEquals(2, players[0].getHand().getCards().size());
    assertEquals(2, players[0].getDeployableTroops());
    assertEquals(39, deckController.getDeck().getDeckCards().size());
    assertEquals(3, deckController.getHandedIn().getDeckCards().size());
    //draws the rest of the cards to check if Deck is refilled
    for (int i = 0; i < 39; i++) {
      moveProcessor.drawCard();
    }
    assertEquals(3, deckController.getDeck().getDeckCards().size());

    //Clear player for next test HandIn
    players[0].setDeployableTroops(0);
    players[0].getHand().getCards().clear();
    playerController.getHandController().deselectAllCards();

    //Check if the CountryBonus is applied
    addCountryToPlayer(CountryName.CHINA, players[0]);
    addCountryToPlayer(CountryName.BRAZIL, players[0]);
    //Make two Sets of identical cards to test if references must be on the same object.

    Card chinaCard = new Card(CardSymbol.CAVALRY, CountryName.CHINA, 1);
    Card argentinaCard = new Card(CardSymbol.CAVALRY, CountryName.ARGENTINA, 2);
    Card brazilCard = new Card(CardSymbol.CAVALRY, CountryName.BRAZIL, 3);

    players[0].getHand().getCards().add(argentinaCard);
    players[0].getHand().getCards().add(chinaCard);
    players[0].getHand().getCards().add(brazilCard);

    playerController.getHandController().selectExchangeableCards();

    moveProcessor.processHandIn(new HandIn(players[0].getHand().getSelectedCards()));

    assertEquals(2, getCountryByCountryName(CountryName.CHINA).getTroops());
    assertEquals(2, getCountryByCountryName(CountryName.BRAZIL).getTroops());
    assertEquals(0, getCountryByCountryName(CountryName.ARGENTINA).getTroops());


  }

  @Test
  void processFortifyTest() {

    for (int i = 0; i < 6; i++) {
      players[i].setInitialTroops(0);
    }
    addCountryToPlayer(CountryName.UKRAINE, players[0]);
    addCountryToPlayer(CountryName.URAL, players[0]);

    Country ukraine = getCountryByCountryName(CountryName.UKRAINE);
    Country russia = getCountryByCountryName(CountryName.URAL);

    gameController.getCurrentPlayer().setCurrentPhase(GamePhase.FORTIFY_PHASE);

    getCountryByCountryName(CountryName.UKRAINE).setTroops(10);
    getCountryByCountryName(CountryName.URAL).setTroops(10);
    moveProcessor.processFortify(new Fortify(russia, ukraine, 5));
    assertEquals(5, getCountryByCountryName(CountryName.URAL).getTroops());
    assertEquals(15, getCountryByCountryName(CountryName.UKRAINE).getTroops());
    moveProcessor.processEndPhase(new EndPhase(GamePhase.ATTACK_PHASE));

    assertEquals(GamePhase.REINFORCEMENT_PHASE,
        gameController.getCurrentPlayer().getCurrentPhase());


  }

  @Test
  void someAttacks() {
    addCountryToPlayer(CountryName.ARGENTINA, players[0]);
    addCountryToPlayer(CountryName.GREENLAND, players[0]);
    addCountryToPlayer(CountryName.ONTARIO, players[0]);

    getCountryByCountryName(CountryName.GREENLAND).setTroops(8);
    getCountryByCountryName(CountryName.ARGENTINA).setTroops(5);
    getCountryByCountryName(CountryName.ONTARIO).setTroops(8);

    //defending countries
    addCountryToPlayer(CountryName.ICELAND, players[1]);
    addCountryToPlayer(CountryName.PERU, players[1]);
    addCountryToPlayer(CountryName.QUEBEC, players[1]);

    getCountryByCountryName(CountryName.ICELAND).setTroops(1);
    getCountryByCountryName(CountryName.PERU).setTroops(2);
    getCountryByCountryName(CountryName.QUEBEC).setTroops(1);

    //make Reinforce in a country owned by players[0].
    players[0].setCurrentPhase(GamePhase.REINFORCEMENT_PHASE);
    players[0].setDeployableTroops(3);
    moveProcessor.processReinforce(
        new Reinforce(getCountryByCountryName(CountryName.ARGENTINA), 3));
    moveProcessor.processEndPhase(new EndPhase(GamePhase.REINFORCEMENT_PHASE));

    //Test 3 Attacks with all different Troop number.
    Attack testAttack0 = new Attack(
        getCountryByCountryName(CountryName.ARGENTINA),
        getCountryByCountryName(CountryName.PERU),
        3);
    moveProcessor.processAttack(testAttack0);
    moveProcessor.processEndPhase(new EndPhase(GamePhase.REINFORCEMENT_PHASE));
    moveProcessor.processEndPhase(new EndPhase(GamePhase.REINFORCEMENT_PHASE));

  }

  @Test
  void inGameStatisticsTest() {
    for (int i = 0; i < 6; i++) {
      players[i].setInitialTroops(20);
    }

    //Added Each Country to a player.
    for (Country c : gameState.getCountries()) {
      moveProcessor.processReinforce(
          new Reinforce(getCountryByCountryName(c.getCountryName()), 1));
      moveProcessor.processEndPhase(
          new EndPhase(GamePhase.REINFORCEMENT_PHASE));

    }
    for (int i = 0; i < 6; i++) {
      assertEquals(7, players[i].getStatistic().getNumberOfOwnedCountries());
      assertEquals(7, players[i].getStatistic().getNumberOfTroops());
    }
    //Selects a Random Country from players[1]
    Random random = new Random();
    int i = random.nextInt(players[1].getCountries().size());
    int counter = 0;
    Country countryToGiveAway = null;
    for (Country country : players[1].getCountries()) {
      if (i == counter) {
        countryToGiveAway = country;
        break;
      }
      counter++;
    }
    assert countryToGiveAway != null;
    addCountryToPlayer(countryToGiveAway.getCountryName(), players[0]);
    moveProcessor.updateInGameStatistics();
    assertEquals(8, players[0].getStatistic().getNumberOfOwnedCountries());
    assertEquals(6, players[1].getStatistic().getNumberOfOwnedCountries());

    assertEquals(8, players[0].getStatistic().getNumberOfTroops());
    assertEquals(6, players[1].getStatistic().getNumberOfTroops());

  }

  Country getCountryByCountryName(CountryName countryName) {

    return gameState.getCountries().stream().filter(n -> n.getCountryName().equals(countryName))
        .findFirst().orElse(null);


  }

  void addCountryToPlayer(CountryName countryName, Player player) {

    if (getCountryByCountryName(countryName).getPlayer() != null) {
      getCountryByCountryName(countryName).getPlayer().getCountries()
          .remove(getCountryByCountryName(countryName));
    }
    player.getCountries().add(getCountryByCountryName(countryName));
    getCountryByCountryName(countryName).setPlayer(player);
  }


}

