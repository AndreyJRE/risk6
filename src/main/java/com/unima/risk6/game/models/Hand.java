package com.unima.risk6.game.models;

import java.util.ArrayList;

public class Hand {

  private ArrayList<Card> cards;
  private ArrayList<Card> selectedCards;


  public Hand() {
    cards = new ArrayList<Card>();
    selectedCards = new ArrayList<Card>();
  }

  public ArrayList<Card> getCards() {
    return cards;
  }

  public ArrayList<Card> getSelectedCards() {
    return selectedCards;
  }

}
