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

  private Hand hand;
  private Set<Country> countries;
  private Set<Continent> continents;

  public Player() {
    this.hand = new Hand();
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
          switch (country.getTroops()) {
            //Should add 3 valid Attacks when the country has more than 3 troops.
            case 1:
              break;
            default:
              attackable.add(new Attack(country, adjacentCountry, 3));
            case 3:
              attackable.add(new Attack(country, adjacentCountry, 2));
            case 2:
              attackable.add(new Attack(country, adjacentCountry, 1));
              break;

          }
        }
      }
    }
    return attackable;
  }

  public void drawCard(Card drawnCard){
    this.hand.addCard(drawnCard);
  }
  public int numberOfCountries() {
    return countries.size();
  }

  public void sendReinforce(Reinforce reinforce) {

  }

  public void sendAttack(Attack attack) {

  }

  public void sendFortify(Fortify fortify) {

  }

  public Hand getHand() {
    return hand;
  }
}
