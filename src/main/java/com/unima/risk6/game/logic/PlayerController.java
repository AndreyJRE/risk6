package com.unima.risk6.game.logic;

import com.unima.risk6.game.models.Card;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlayerController {

  private Player player;
  private HandController handController;
  private GameState gameState;


  public PlayerController(Player player, GameState gameState) {

    this.player = player;
    this.gameState = gameState;
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

  public List<Country> getValidFortifiesFromCountry(Country country) {
    List<Country> fortifiable = new ArrayList<Country>();
    country.getAdjacentCountries().forEach((n) -> {
      if (n.getPlayer().equals(player)) {
        fortifiable.add(n);
      }
    });
    return fortifiable;
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

/*
  public GamePhase nextPhase() {

    switch (player.getCurrentPhase()) {
      case REINFORCEMENTPHASE:
        if (player.getDeployableTroops() == 0) {
          player.setPhase(ATTACKPHASE);
          return ATTACKPHASE;
        } else {
          return REINFORCEMENTPHASE;
          //TODO exception or error which should be given to UI
        }

      case ATTACKPHASE:
        player.setPhase(FORTIFYPHASE);
        break;
      case FORTIFYPHASE, CLAIMPHASE:
        player.setPhase(NOTACTIVE);
        break;
      case NOTACTIVE:
        if (player.getInitialTroops() > 0) {
          player.setPhase(CLAIMPHASE);
        } else {
          player.setPhase(REINFORCEMENTPHASE);
        }
        break;

      default:
        break;
    }
    return player.getCurrentPhase();
  }

 */

}
