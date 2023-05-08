package com.unima.risk6.game.models;

import java.util.ArrayList;

public class Hand {

  private final ArrayList<Card> cards;
  private final ArrayList<Card> selectedCards;

  /**
   * Constructs a new Hand and initializes tha fields: cards and selectedCards.
   */
  public Hand() {
    cards = new ArrayList<>();
    selectedCards = new ArrayList<>();
  }

  public ArrayList<Card> getCards() {
    return cards;
  }

  public ArrayList<Card> getSelectedCards() {
    return selectedCards;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Cards: ");
    for (Card c : cards) {
      stringBuilder.append(c.toString()).append("||");
    }
    stringBuilder.append("\n");
    stringBuilder.append("Selected: ");
    for (Card c : selectedCards) {
      stringBuilder.append(c.toString()).append("||");
    }
    return stringBuilder.toString();
  }

}
