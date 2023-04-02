package com.unima.risk6.game.models.enums;

/**
 * Enumeration of the continent names in the game of Risk.
 *
 * @author wphung
 */

public enum ContinentName {
  ASIA(7), AFRICA(3), AUSTRALIA(2), EUROPE(5), NORTH_AMERICA(5), SOUTH_AMERICA(2);

  private final int bonusTroops;

  ContinentName(int bonusTroops) {
    this.bonusTroops = bonusTroops;
  }

  public int getBonusTroops() {
    return bonusTroops;
  }
}
