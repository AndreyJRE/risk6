package com.unima.risk6.game.ai.bots;

import com.unima.risk6.game.ai.models.MoveTriplet;
import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import com.unima.risk6.game.logic.controllers.PlayerController;
import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.GameState;
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
//    this.continentsCopy = new ArrayList<>();
//    continentsCopy.addAll(this.getContinents());

  }

  public List<MoveTriplet> getLegalMoves() {
    List<Reinforce> reinforceMoves = getReinforceMoves();
    List<Attack> attackMoves = getAttackMoves();
    List<Fortify> fortifyMoves = getFortifyMoves();

    List<MoveTriplet> legalMoves = new ArrayList<>();
    for (Reinforce reinforce : reinforceMoves) {
      for (Attack attack : attackMoves) {
        for (Fortify fortify : fortifyMoves) {
//          legalMoves.add();
        }
      }
    }

    return legalMoves;
  }

  private List<Reinforce> getReinforceMoves() {
    // Implement the method to generate reinforce moves considering available troops and strategic placement
    return null;
  }

  private List<Attack> getAttackMoves() {
    // Implement the method to generate attack moves with high probability of victory
    return null;
  }

  private List<Fortify> getFortifyMoves() {
    // Implement the method to generate fortify moves that help fortify the borders of owned regions
    return null;
  }
}
