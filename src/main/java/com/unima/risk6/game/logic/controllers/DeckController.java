package com.unima.risk6.game.logic.controllers;

import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Deck;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckController {

  private Deck deck;

  public DeckController(Deck deck) {
    this.deck = deck;
  }

  public void shuffleDeck() {
    Collections.shuffle(deck.getDeckCards());

  }

  public boolean isEmpty() {
    return deck.getDeckCards().isEmpty();
  }

}
