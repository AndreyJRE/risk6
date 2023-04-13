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

  Optional<User> getUserByUsername(String username);

  List<User> getAllUsersByActive(boolean active);
}
