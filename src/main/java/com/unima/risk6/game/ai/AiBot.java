package com.unima.risk6.game.ai;

import com.unima.risk6.game.ai.models.CountryPair;
import com.unima.risk6.game.logic.Fortify;
import com.unima.risk6.game.logic.Reinforce;
import java.util.List;

/***
 * @author eameri
 */
public interface AiBot {


  List<Reinforce> createAllReinforcements();
  List<CountryPair> createAllAttacks();
  Fortify moveAfterAttack(CountryPair winPair);
  Fortify createFortify();
  /**
   * A method for a bot to claim a single country during the CLAIM PHASE Game State.
   */
  Reinforce claimCountry();
}
