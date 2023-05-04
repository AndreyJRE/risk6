package com.unima.risk6.game.logic.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Hand;
import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HandControllerTest {


  static HandController handController;
  static Card[] cards;

  @BeforeAll
  static void setUp() {
    Hand hand = new Hand();
    handController = new HandController();
    handController.setHand(hand);
    //initialize some Cards to test with
    cards = new Card[8];
    cards[0] = new Card(CardSymbol.CAVALRY, CountryName.ALASKA, 1);
    cards[1] = new Card(CardSymbol.CAVALRY, CountryName.KAMCHATKA, 2);
    cards[2] = new Card(CardSymbol.CAVALRY, CountryName.CONGO, 3);
    cards[3] = new Card(CardSymbol.CANNON, CountryName.WESTERN_EUROPE, 4);
    cards[4] = new Card(CardSymbol.CANNON, CountryName.VENEZUELA, 5);
    cards[5] = new Card(CardSymbol.WILDCARD, 6);
    cards[6] = new Card(CardSymbol.INFANTRY, CountryName.BRAZIL, 7);
    cards[7] = new Card(CardSymbol.WILDCARD, 8);

  }

  @BeforeEach
  void addCards() {
    //Every Test is performed with the following initial cards
    //3 Cavalry cards and one cannon card.
    ArrayList<Card> cardList = handController.getHand().getCards();
    cardList.clear();
    cardList.add(cards[0]);
    cardList.add(cards[1]);
    cardList.add(cards[2]);
    cardList.add(cards[3]);

    handController.deselectAllCards();


  }

  @Test
  void successfulInit() {
    assertNotNull(handController);
    assertNotNull(handController.getHand());
  }

  @Test
  void cardSelectionTest() {

    //check if added successfully
    assertEquals(4, handController.getHand().getCards().size());

    handController.selectCardThroughIndex(0);
    assertEquals(1, handController.getHand().getSelectedCards().size());
    assertEquals(cards[0], handController.getHand().getSelectedCards().get(0),
        "Selected card should be the first one added into Hand");

    //Select another 2 cards
    handController.selectCardThroughIndex(1);
    assertEquals(2, handController.getHand().getSelectedCards().size());
    assertEquals(cards[1], handController.getHand().getSelectedCards().get(1));
    handController.selectCardThroughIndex(2);
    assertEquals(3, handController.getHand().getSelectedCards().size());
    assertEquals(cards[2], handController.getHand().getSelectedCards().get(2));

    //Should not add a forth Card
    handController.selectCardThroughIndex(3);
    assertFalse(handController.getHand().getSelectedCards().contains(cards[3]));

  }

  @Test
  void cardDoubleSelectionTest() {
    //Should not select an already selected Card twice

    handController.selectCardThroughIndex(0);
    assertEquals(1, handController.getHand().getSelectedCards().size());
    handController.selectCardThroughIndex(0);
    assertEquals(1, handController.getHand().getSelectedCards().size());
  }

  @Test
  void maximalCardSelectionTest() {
    //Should not add a forth Card
    handController.selectCardThroughIndex(0);
    handController.selectCardThroughIndex(1);
    handController.selectCardThroughIndex(2);
    handController.selectCardThroughIndex(3);
    assertFalse(handController.getHand().getSelectedCards().contains(cards[3]));
  }

  @Test
  void cardDeselectionTest() {
    handController.selectCardThroughIndex(0);
    handController.selectCardThroughIndex(1);
    handController.selectCardThroughIndex(2);
    //Deselecting one card at a time
    handController.deselectCards(0);

    assertFalse(handController.getHand().getSelectedCards().contains(cards[0]));
    assertEquals(2, handController.getHand().getSelectedCards().size());
    handController.deselectCards(2);
    assertEquals(1, handController.getHand().getSelectedCards().size());
    assertFalse(handController.getHand().getSelectedCards().contains(cards[2]));
    handController.deselectCards(1);
    assertEquals(0, handController.getHand().getSelectedCards().size());
    assertFalse(handController.getHand().getSelectedCards().contains(cards[1]));

    //Deselect all cards
    handController.selectCardThroughIndex(1);
    handController.selectCardThroughIndex(3);
    handController.selectCardThroughIndex(0);

    handController.deselectAllCards();
    assertEquals(0, handController.getHand().getSelectedCards().size());

  }

  @Test
  void mustExchangeTest() {

    assertFalse(handController.mustExchange());
    handController.getHand().getCards().add(cards[4]);
    assertTrue(handController.mustExchange());

  }

  @Test
  void cardExchangeabilityTest() {
    ArrayList<Card> cardList = handController.getHand().getCards();
    cardList.add(cards[4]);
    cardList.add(cards[5]);
    cardList.add(cards[6]);
    cardList.add(cards[7]);

    //Three of a kind should be exchangeable
    handController.selectCardThroughIndex(0);
    handController.selectCardThroughIndex(1);
    handController.selectCardThroughIndex(2);
    assertTrue(handController.isExchangeable());
    handController.deselectAllCards();

    //Two of a kind with Wildcard should be exchangeable
    handController.selectCardThroughIndex(0);
    handController.selectCardThroughIndex(1);
    handController.selectCardThroughIndex(5);
    assertTrue(handController.isExchangeable());
    handController.deselectAllCards();

    //Two of a kind and random other card should not be exchangeable
    handController.selectCardThroughIndex(0);
    handController.selectCardThroughIndex(1);
    handController.selectCardThroughIndex(6);
    assertFalse(handController.isExchangeable());
    handController.deselectAllCards();

    handController.selectCardThroughIndex(3);
    handController.selectCardThroughIndex(4);
    handController.selectCardThroughIndex(6);
    assertFalse(handController.isExchangeable());
    handController.deselectAllCards();
    //2 wildcards with any other card should be exchangeable
    handController.selectCardThroughIndex(0);
    handController.selectCardThroughIndex(5);
    handController.selectCardThroughIndex(7);

    assertTrue(handController.isExchangeable());
    handController.deselectAllCards();
    //All symbols differ should be exchangeable
    handController.selectCardThroughIndex(0);
    handController.selectCardThroughIndex(4);
    handController.selectCardThroughIndex(6);
    assertTrue(handController.isExchangeable());
    handController.deselectAllCards();
    //Two different Cards with one wild Card should be exchangeable
    handController.selectCardThroughIndex(0);
    handController.selectCardThroughIndex(4);
    handController.selectCardThroughIndex(7);
    assertTrue(handController.isExchangeable());
    handController.deselectAllCards();

  }

  @Test
  void exchangeCardsTest() {
    handController.selectCardThroughIndex(0);
    handController.selectCardThroughIndex(1);
    handController.selectCardThroughIndex(2);
    assertEquals(4, handController.getHand().getCards().size());
    handController.exchangeCards();

    assertEquals(0, handController.getHand().getSelectedCards().size());
    //Check if cards were properly removed
    assertEquals(1, handController.getHand().getCards().size());
    assertFalse(handController.getHand().getCards().contains(cards[0]));
    assertFalse(handController.getHand().getCards().contains(cards[1]));
    assertFalse(handController.getHand().getCards().contains(cards[2]));
    //Check if cards that were not selected were not removed
    assertTrue(handController.getHand().getCards().contains(cards[3]));

  }

  @Test
  void countryBonusTest() {
    Country alaska = new Country(CountryName.ALASKA);
    Country brazil = new Country(CountryName.BRAZIL);
    Country venezuela = new Country(CountryName.VENEZUELA);
    Country congo = new Country(CountryName.CONGO);
    Country china = new Country(CountryName.CHINA);

    HashSet<Country> countries = new HashSet<>();
    countries.add(alaska);
    countries.add(brazil);
    countries.add(venezuela);
    countries.add(congo);
    countries.add(china);

    handController.selectCardThroughIndex(0);
    handController.selectCardThroughIndex(1);
    handController.selectCardThroughIndex(2);

    Set<CountryName> bonusCountries = handController.getBonusCountries(countries);
    assertEquals(2, bonusCountries.size());

  }

  @Test
  void automaticCardSelectionTest() {
    //Holds the cards that is held by all tests at the beginning consisting
    // of 3 cavalry cards and one cannon card, should be exchangeable
    assertTrue(handController.holdsExchangeable());
    ArrayList<Card> cardList = handController.getHand().getCards();
    cardList.clear();
    //hold 2 of cavalry and 2 of cannon, edge case of non-exchangeable combination
    cardList.add(cards[0]);
    cardList.add(cards[1]);
    cardList.add(cards[3]);
    cardList.add(cards[4]);
    assertFalse(handController.holdsExchangeable());

    //adds a third cavalry card. Now it must be exchangeable
    cardList.add(cards[2]);
    assertTrue(handController.holdsExchangeable());

    handController.selectExchangeableCards();
    assertTrue(handController.isExchangeable());
    //Should have selected the 3 cavalry cards
    assertTrue(handController.getHand().getSelectedCards().contains(cards[0]));
    assertTrue(handController.getHand().getSelectedCards().contains(cards[1]));
    assertTrue(handController.getHand().getSelectedCards().contains(cards[2]));

    cardList.remove(cards[2]);
    handController.deselectAllCards();
    assertFalse(handController.holdsExchangeable());
    handController.selectExchangeableCards();
    assertEquals(0, handController.getHand().getSelectedCards().size());
    assertFalse(handController.isExchangeable());

    cardList.clear();

    //Added a card amount that is held in an edge case and tested method performance
    CardSymbol[] cardSymbols = CardSymbol.values();
    Random random = new Random();
    ArrayList<Card> cards = new ArrayList<>();
    for (int i = 0; i < 24; i++) {
      cards.add(new Card(cardSymbols[random.nextInt(cardSymbols.length)], i));
    }

    cardList.addAll(cards);
    assertEquals(24, cardList.size());
    handController.selectExchangeableCards();
    assertEquals(3, handController.getHand().getSelectedCards().size());


  }

  @Test
  void selectCardsTest() {

    Card alaskaCard = new Card(CardSymbol.CAVALRY, CountryName.ALASKA, 1);
    Card wildcard1 = new Card(CardSymbol.WILDCARD, 6);
    Card wildcard2 = new Card(CardSymbol.WILDCARD, 8);

    ArrayList<Card> cardList = new ArrayList<>();
    cardList.add(wildcard1);
    cardList.add(alaskaCard);
    cardList.add(wildcard2);

    //Test if selectCards can select cards even if they are not the same reference
    handController.addCard(cards[0]);// equal to alaskaCard
    handController.addCard(cards[5]);//Equal to wildcard1
    handController.addCard(cards[7]);//equal to wildcard2
    handController.addCard(cards[1]);

    handController.selectCardsFromCardList(cardList);
    assertEquals(3, handController.getHand().getSelectedCards().size());

    assertTrue(handController.getHand().getSelectedCards().contains(cards[0]));
    assertTrue(handController.getHand().getSelectedCards().contains(cards[5]));
    assertTrue(handController.getHand().getSelectedCards().contains(cards[7]));


  }

}