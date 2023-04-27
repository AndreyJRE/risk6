package com.unima.risk6.game.models;

import java.util.ArrayList;

public class Deck {

  private final ArrayList<Card> deckCards;


  public Deck() {
    deckCards = new ArrayList<Card>();
  }


  public ArrayList<Card> getDeckCards() {
    return deckCards;
  }
}
