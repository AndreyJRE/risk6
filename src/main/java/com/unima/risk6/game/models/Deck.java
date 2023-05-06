package com.unima.risk6.game.models;

import java.util.ArrayList;

/**
 * Represents the deck in the risk game which saves the Cards of the deck that can be drawn by the
 * players.
 *
 * @author wphung
 */

public class Deck {

  private final ArrayList<Card> deckCards;


  public Deck() {
    deckCards = new ArrayList<Card>();
  }

  /**
   * Returns the ArrayList in which the Cards of the deck are saved.
   *
   * @return the ArrayList in which the Cards of the deck are saved.
   */
  public ArrayList<Card> getDeckCards() {
    return deckCards;
  }
}
