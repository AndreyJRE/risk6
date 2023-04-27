package com.unima.risk6.game.logic.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Deck;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DeckControllerTest {

  static DeckController deckController;
  static Deck deck;

  @BeforeAll
  static void setUp() {
    deck = new Deck();
    deckController = new DeckController(deck);
    deckController.initDeck();
  }

  @Test
  void successfulInit() {
    assertNotNull(deckController);
  }

  @Test
  void numberOfCardsTest() {
    deckController.initDeck();
    assertEquals(44, deck.getDeckCards().size(), "Deck should have 44 cards after initialization");
  }

  @Test
  void shuffleDeckTest() {
    List<Card> originalDeck = new ArrayList<>(deck.getDeckCards());
    deckController.shuffleDeck();
    List<Card> shuffledDeck = deck.getDeckCards();

    assertNotEquals(originalDeck, shuffledDeck);
  }

  @Test
  void isEmptyDeckTest() {
    deckController.getDeck().getDeckCards().clear();
    assertTrue(deckController.isEmpty());
  }


}