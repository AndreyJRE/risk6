package com.unima.risk6.game.configurations;

import com.unima.risk6.game.logic.GameState;

public interface GameStateObserver {

  void update(GameState gameState);

}
