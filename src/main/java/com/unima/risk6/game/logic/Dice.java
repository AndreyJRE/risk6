package com.unima.risk6.game.logic;

import java.util.Random;

public class Dice {

  private static final Random random = new Random();


  public static int rollDice() {
    int i = 1 + random.nextInt(6);
    return i;
  }


}
