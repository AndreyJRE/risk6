package com.unima.risk6.game.ai;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.GameState;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.Player;
import java.util.List;
import java.util.Map;

/***
 * @author eameri
 */
public class EasyBot extends Player implements AiBot {

  @Override
  public void makeMove(GameState gameState) {

  }

  public Attack createAttack() {
    Map<Country, List<Attack>> options = this.getAllValidAttacks();
    return null;
  }
}
