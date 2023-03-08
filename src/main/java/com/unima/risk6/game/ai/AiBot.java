package com.unima.risk6.game.ai;

import com.unima.risk6.game.logic.Attack;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.GameState;
import com.unima.risk6.game.logic.Reinforce;

/***
 * @author eameri
 */
public interface AiBot {
  void makeMove(GameState gameState);
}
