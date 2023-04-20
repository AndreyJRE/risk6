package com.unima.risk6.game.logic.controllers;

import static com.unima.risk6.game.models.enums.GamePhase.NOT_ACTIVE;

import com.unima.risk6.game.ai.models.MovePair;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerController {

  private Player player;
  private final HandController handController;


  public PlayerController() {
    this.handController = new HandController();
  }

  public Reinforce sendReinforce(Country reinforcedCountry, int troopNumber) {
    return new Reinforce(reinforcedCountry, troopNumber);
  }

  public Attack sendAttack(Country attackingCountry, Country defendingCountry, int troopNumber) {

    return new Attack(attackingCountry, defendingCountry, troopNumber);
  }

  public Fortify sendFortify(Country outgoing, Country incoming, int troopNumber) {
    return new Fortify(outgoing, incoming, troopNumber);
  }

  public void changeDeployableTroops(int diff) {
    player.setDeployableTroops(player.getDeployableTroops() + diff);
  }

  public void handInCards(int numberOfHandIn) {
    if (handController.isExchangable()) {
      Set<Country> countries = player.getCountries();
      if (!handController.hasCountryBonus(countries).isEmpty()) {
        //TODO send REINFORCE or add troops

        handController.hasCountryBonus(countries).forEach(n -> sendReinforce(n, 2));
      }
      handController.exchangeCards();
      if (numberOfHandIn > 5) {
        changeDeployableTroops(15 + 5 * (numberOfHandIn - 6));
      } else {
        changeDeployableTroops(2 + 2 * (numberOfHandIn));
      }

    }

  }

  public void addCountry(Country countryToAdd) {
    player.getCountries().add(countryToAdd);
    countryToAdd.setPlayer(player);
  }

  //TODO have to implement which Continent is fully Occupied by Player


  public List<MovePair> getValidFortifiesFromCountry(Country country) {
    List<MovePair> fortifiable = new ArrayList<>();
    country.getAdjacentCountries().forEach((adj) -> {
      if (adj.getPlayer().equals(player)) {
        fortifiable.add(new MovePair(country, adj));
      }
    });
    return fortifiable;
  }

  public List<MovePair> getValidAttacksFromCountry(Country country) {
    List<MovePair> attackable = new ArrayList<>();
    if (country.getTroops() >= 2) {
      for (Country adjacentCountry : country.getAdjacentCountries()) {
        if (!this.player.equals(adjacentCountry.getPlayer())) {
          attackable.add(new MovePair(country, adjacentCountry));
        }
      }
    }
    return attackable;
  }


  public List<MovePair> getAllValidFortifies() {
    List<MovePair> countriesFortifiable = new ArrayList<>();
    for (Country country : this.player.getCountries()) {
      List<MovePair> fortifiable = getValidFortifiesFromCountry(country);
      if (fortifiable.size() > 0) {
        countriesFortifiable.addAll(fortifiable);
      }
    }
    return countriesFortifiable;
  }

  public List<MovePair> getAllAttackableCountryPairs(Continent continent) {
    List<MovePair> countriesAttackable = new ArrayList<>();
    for (Country country : continent.getCountries()) {
      List<MovePair> attackable = getValidAttacksFromCountry(country);
      if (attackable.size() > 0) {
        countriesAttackable.addAll(attackable);
      }
    }
    return countriesAttackable;
  }

  public void removeCountry(Country countryToRemove) {
    player.getCountries().remove(countryToRemove);
  }


  public void setPlayer(Player player) {
    this.player = player;
    this.handController.setHand(player.getHand());

  }

  public int getNumberOfCountries() {
    return player.getCountries().size();
  }

  public boolean isActive() {
    return !player.getCurrentPhase().equals(NOT_ACTIVE);
  }
}
