package com.unima.risk6.game.models;

import java.util.ArrayList;
import java.util.Set;

public class Player {

  private ArrayList<Card> cards;
  // Country : Troops
  // Country - getBesetzt()

  private Set<Country> countries;


  public Player() {
    this.cards = new ArrayList<>();
  }
}
