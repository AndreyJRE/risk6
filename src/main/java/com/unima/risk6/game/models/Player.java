package com.unima.risk6.game.models;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Player {

  private ArrayList<Card> cards;
  private Set<Country> countries;
  private Set<Continent> continents;

  public Player() {
    this.cards = new ArrayList<>();
  }

  public Map<Country, List<Attack>> getAllValidAttacks() {
    Map<Country, List<Attack>> countriesAttackable = new HashMap<>();
    for (Country country : countries) {
      List<Attack> attackable = getValidAttackFromCountry(country);
      countriesAttackable.put(country, attackable);
    }
    return countriesAttackable;
  }

  public List<Attack> getValidAttackFromCountry(Country country) {
    // TODO
    List<Attack> attackable = new ArrayList<>();
    if (country.getTroops() >= 2) {
      for (Country adjacentCountry : country.getAdjacentCountries()) {
        if (!this.equals(adjacentCountry.getPlayer())) {
          attackable.add(null); // TODO when Attack class is ready
        }
      }
    }
    return attackable;
  }

  public void sendReinforce(Reinforce reinforce) {

  }

  public void sendAttack(Attack attack) {

  }

  public void sendFortify(Fortify fortify) {

  }
}
