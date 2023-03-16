package com.unima.risk6.database.daos;

import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.models.User;
import java.util.List;
import java.util.Optional;


/**
 * The UserDao interface is a data access object for the User model that extends the Dao interface.
 *
 * @author astoyano
 */
public interface UserDao extends Dao<User> {

  /**
   * Retrieves a list of all GameStatistic objects associated with a specified user ID.
   *
   * @param id The user ID to retrieve game statistics for.
   * @return A list of GameStatistic objects associated with the specified user ID.
   */

  List<GameStatistic> getAllStatisticsByUserId(Long id);

  Optional<User> getUserByUsername(String username);
}
