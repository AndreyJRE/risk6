package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.enums.GamePhase;


/**
 * The EndPhase class represents an ending of a Game Phase of risk game. It stores the Game phase
 * which should be ended.
 *
 * @author wphung
 */

public class EndPhase extends Move {

  private final GamePhase phaseToEnd;

  public EndPhase(GamePhase phaseToEnd) {
    this.phaseToEnd = phaseToEnd;
  }

  /**
   * Returns the Game phase to end.
   *
   * @return the Game phase to end.
   */
  public GamePhase getPhaseToEnd() {
    return phaseToEnd;
  }
}
