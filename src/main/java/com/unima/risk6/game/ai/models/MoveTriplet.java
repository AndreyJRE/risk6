package com.unima.risk6.game.ai.models;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import java.util.List;

public class MoveTriplet {
  private final List<Reinforce> reinforcements;
  private final List<CountryPair> attacks;
  private final Fortify fortify;

  public List<Reinforce> getReinforcements() {
    return reinforcements;
  }

  public List<CountryPair> getAttacks() {
    return attacks;
  }

  public Fortify getFortify() {
    return fortify;
  }

  public MoveTriplet(List<Reinforce> reinforcements, List<CountryPair> attacks, Fortify fortify) {
    this.reinforcements = reinforcements;
    this.attacks = attacks;
    this.fortify = fortify;
  }
}
