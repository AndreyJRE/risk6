package com.unima.risk6.database.repositories;

import com.unima.risk6.database.dao.UserDao;
import com.unima.risk6.database.models.User;
import java.util.List;
import java.util.Optional;

public class UserRepository implements UserDao {


  @Override
  public Optional<User> get(long id) {

    return Optional.empty();
  }

  @Override
  public List<User> getAll() {
    return null;
  }

  @Override
  public void save(User user) {

  }

  @Override
  public void update(User user, String[] params) {

  }

  @Override
  public void delete(User user) {

  }
}
