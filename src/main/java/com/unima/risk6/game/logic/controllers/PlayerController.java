package com.unima.risk6.game.logic.controllers;

import static com.unima.risk6.game.models.enums.GamePhase.NOTACTIVE;

import com.unima.risk6.game.ai.models.MovePair;
import com.unima.risk6.game.configurations.GameConfiguration;
import com.unima.risk6.game.configurations.GameStateObserver;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerController implements GameStateObserver {

  private Player player;
  private HandController handController;

  public GameState getGameState() {
    return gameState;
  }

  private GameState gameState;


  public PlayerController(Player player, GameState gameState) {

    this.player = player;
    this.gameState = gameState;
    GameConfiguration.addObserver(this);
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

  //TODO ELIYA Klau das zurück
  public void calculateDeployableTroops(int deployableTroops) {
    this.calculateDeployableTroops();
  }

  public void changeDeployableTroops(int diff) {
    player.setDeployableTroops(player.getDeployableTroops() + diff);
  }

  public void handInCards(int numberOfHandIn) {
//TODO handController
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

  public void calculateDeployableTroops() {
    updateContinents(gameState.getContinents());
    player.setDeployableTroops(3);
    int n = player.getNumberOfCountries();
    if (n > 8) {
      n = n - 9;
      player.setDeployableTroops(Math.floorDiv(n, 3));
    }
    player.getContinents().forEach((x) -> this.changeDeployableTroops(x.getBonusTroops()));
  }

  //TODO have to implement which Continent is fully Occupied by Player

  public void updateContinents(Set<Continent> continents) {
    continents.forEach((n) -> {
      Set<Country> countries = player.getCountries();
      if (countries.containsAll(n.getCountries())) {
        player.getContinents().add(n);
      }
    });

  }

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


  //TODO überlegen, ob das gebraucht wird, oder ob man von ui auf hand instant zugreift
  public void selectCard(int i) {
    handController.selectCard(i);
  }

  public void deselectCard(int i) {
    handController.deselectCards(i);
  }

  public void drawCard(Card drawnCard) {
    handController.drawCard();
  }

  @Override
  public void update(GameState gameState) {
    this.gameState = gameState;
  }

 */

}
