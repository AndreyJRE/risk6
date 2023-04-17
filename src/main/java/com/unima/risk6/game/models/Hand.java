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

}
