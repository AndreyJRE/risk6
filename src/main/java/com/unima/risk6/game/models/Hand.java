package com.unima.risk6.game.models;

import com.unima.risk6.game.models.enums.CardSymbol;
import com.unima.risk6.game.models.enums.CountryName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
