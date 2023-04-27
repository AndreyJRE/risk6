package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Player;
import java.util.ArrayList;
import java.util.List;

public class MonteCarloBot extends Player {

  private final PlayerController playerController;
//  private final List<Continent> continentsCopy;

  public MonteCarloBot(Player toCopy) {
    super(toCopy);
    playerController = new PlayerController();
    playerController.setPlayer(this);

  }

  public List<MoveTriplet> getLegalMoves() {
    List<List<Reinforce>> reinforceMoves = this.getReinforceMoves();
    List<CountryPair> attackMoves = this.getAttackMoves();
    List<Fortify> fortifyMoves = this.getFortifyMoves();

    List<MoveTriplet> legalMoves = new ArrayList<>();
    // for (List<Reinforce> reinforce : reinforceMoves) {
    // for (List<> attack : attackMoves) {
    // for (Fortify fortify : fortifyMoves) {
    // legalMoves.add(new MoveTriplet(reinforce, attack, fortify));
    // }
    // }
    // }

    return legalMoves;
  }

  // TODO: DO
  public List<List<Reinforce>> getReinforceMoves() {
    return null;
  }

  // TODO: List<List>
  public List<CountryPair> getAttackMoves() {
    List<CountryPair> attackPairs = new ArrayList<>();
    for (Continent continent : this.getContinents()) {
      attackPairs.addAll(this.playerController.getAllAttackableCountryPairs(continent));
    }
    // TODO: sort by best for continent?
    return attackPairs.stream().filter(pair -> pair.getWinningProbability() > 70).toList();
  }

  public List<Fortify> getFortifyMoves() {
    List<CountryPair> allFortifies = this.playerController.getAllValidFortifies();
    allFortifies = allFortifies.stream().filter(
        countryPair -> countryPair.getOutgoing().getTroops() > countryPair.getIncoming()
            .getTroops()).toList();

    List<Fortify> fortifiesToReturn = new ArrayList<>();
    for (CountryPair pair : allFortifies) {
      // TODO: better decisions here
      int troops = Math.abs((pair.getOutgoing().getTroops() - pair.getIncoming().getTroops()) / 2);
      fortifiesToReturn.add(pair.createFortify(troops));
    }
    return fortifiesToReturn;
  }

}
