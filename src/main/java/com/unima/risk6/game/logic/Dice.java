package com.unima.risk6.game.logic;

import java.util.Random;

public class Dice {

  private Random random;

  public Dice() {
    this.random = new Random();
  }

  public int rollDice() {
    int i = 1 + random.nextInt(6);
    return i;
  }


}
