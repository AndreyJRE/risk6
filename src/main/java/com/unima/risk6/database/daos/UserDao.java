package com.unima.risk6.database.daos;

import com.unima.risk6.database.models.GameStatistic;
import com.unima.risk6.database.models.User;
import java.util.List;


/**
 * Data access object interface for User model
 *
 * @author astoyano
 */
public interface UserDao extends Dao<User> {

  List<GameStatistic> getAllStatisticsByUserId(Long id);
}
