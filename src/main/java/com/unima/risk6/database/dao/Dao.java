package com.unima.risk6.database.dao;

import java.util.List;
import java.util.Optional;

/**
 * Data access object interface
 *
 * @param <T>
 */
public interface Dao<T> {

  Optional<T> get(long id);

  List<T> getAll();

  void save(T t);

  void update(T t, String[] params);

  void delete(T t);
}
