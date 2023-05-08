package com.unima.risk6.game.ai.models;

import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import java.util.List;
import java.util.Queue;

/**
 * A record representing a triplet of moves: All reinforcements, attacks and fortify moves which a
 * bot would want to make.
 *
 * @author eameri
 */
public record MoveTriplet(List<Reinforce> reinforcements, Queue<CountryPair> attacks,
                          Fortify fortify) {

  /**
   * Creates a new MoveTriplet object containing all chosen moves.
   *
   * @param reinforcements the collection of reinforcements to be made.
   * @param attacks        the collection of attacks to be made.
   * @param fortify        the single fortify move to be made.
   */
  public MoveTriplet {
  }
}
