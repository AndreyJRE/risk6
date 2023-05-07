package com.unima.risk6.game.models;

import java.util.ArrayList;

public class Hand {

  private final ArrayList<Card> cards;
  private final ArrayList<Card> selectedCards;


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
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("Cards: ");
    for (Card c : cards) {
      sbuf.append(c.toString() + "||");
    }
    sbuf.append("\n");
    sbuf.append("Selected: ");
    for (Card c : selectedCards) {
      sbuf.append(c.toString() + "||");
    }
    return sbuf.toString();
  }

}
