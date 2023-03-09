package com.unima.risk6.database.daos;

import java.util.List;
import java.util.Optional;

/**
 * Data access object interface
 *
 * @param <T>
 */
public interface Dao<T> {

  Optional<T> get(Long id);

  List<T> getAll();

  Long save(T t);

  void update(T t);

  void deleteById(Long id);
}
