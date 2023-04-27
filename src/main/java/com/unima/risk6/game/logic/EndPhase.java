package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.enums.GamePhase;

public class EndPhase extends Move {

  private final GamePhase phaseToEnd;

  public EndPhase(GamePhase phaseToEnd) {
    this.phaseToEnd = phaseToEnd;
  }

  public GamePhase getPhaseToEnd() {
    return phaseToEnd;
  }
}
