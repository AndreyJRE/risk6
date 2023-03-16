package com.unima.risk6.game.logic;

import java.util.Random;

/**
 * Represents a dice used in the game of Risk.
 *
 * @author wphung
 */
public class Dice {

  /**
   * The random number generator used to roll the dice.
   */
  private static final Random random = new Random();

  /**
   * Rolls the dice and returns a random integer between 1 and 6.
   *
   * @return a random integer between 1 and 6
   */
  public static int rollDice() {
    int i = 1 + random.nextInt(6);
    return i;
  }


}
