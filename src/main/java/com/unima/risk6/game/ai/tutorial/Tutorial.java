package com.unima.risk6.game.ai.tutorial;

import com.unima.risk6.game.models.Continent;
import com.unima.risk6.game.models.Country;
import com.unima.risk6.game.models.GameState;
import com.unima.risk6.game.models.Player;
import java.util.Queue;
import java.util.Set;

public class Tutorial extends GameState {

  /**
   * Constructs a new game state with the given countries, continents, and players.
   *
   * @param countries     the set of countries in the game
   * @param continents    the set of continents in the game
   * @param activePlayers the queue of players in the game
   */
  public Tutorial(Set<Country> countries,
      Set<Continent> continents,
      Queue<Player> activePlayers) {
    super(countries, continents, activePlayers);
  }
}
