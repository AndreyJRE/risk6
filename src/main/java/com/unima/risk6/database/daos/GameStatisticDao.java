package com.unima.risk6.database.daos;

import com.unima.risk6.database.models.GameStatistic;
import java.util.List;


/**
 * The GameStatisticDao interface is a data access object for the GameStatistic model that extends
 * the Dao interface.
 *
 * @author astoyano
 */
public interface GameStatisticDao extends Dao<GameStatistic> {

  /**
   * Retrieves a list of all GameStatistic objects associated with a specified user ID.
   *
   * @param id The user ID to retrieve game statistics for.
   * @return A list of GameStatistic objects associated with the specified user ID.
   */

  List<GameStatistic> getAllStatisticsByUserId(Long id);

  /**
   * Retrieves a list of all GameStatistic objects associated with a game won status (true or
   * false).
   *
   * @param gameWon The game won status to retrieve game statistics for.
   * @return A list of GameStatistic objects associated with the specified game won status.
   */
  List<GameStatistic> getAllStatisticsByGameWon(boolean gameWon);
}
