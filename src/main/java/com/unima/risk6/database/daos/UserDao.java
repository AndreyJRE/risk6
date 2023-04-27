package com.unima.risk6.database.daos;

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
   * Retrieves a User object associated with a specified username.
   *
   * @param username The username to retrieve a User object for
   * @return An Optional object containing a User object associated with the specified username
   */
  Optional<User> getUserByUsername(String username);

  /**
   * Retrieves a list of all User objects associated with a specified active status.
   *
   * @param active The active status to retrieve User objects for (true or false)
   * @return A list of User objects associated with the specified active status
   */
  List<User> getAllUsersByActive(boolean active);
}
