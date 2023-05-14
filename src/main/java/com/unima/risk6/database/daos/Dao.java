package com.unima.risk6.database.daos;

import java.util.List;
import java.util.Optional;

/**
 * This class defines a generic DAO interface that can be used to interact with various types of
 * database entities. It includes several basic CRUD (Create, Read, Update, Delete) operations
 *
 * @param <T> Database Entity
 * @author astoyano
 */
public interface Dao<T> {

  /**
   * Get an entity by id.
   *
   * @param id Entity id
   * @return Entity
   */
  Optional<T> get(Long id);

  /**
   * Get all entities from database.
   *
   * @return List of entities
   */
  List<T> getAll();

  /**
   * Save a object in Database.
   *
   * @param t Database Entity
   */
  void save(T t);

  /**
   * Update a entity in database.
   *
   * @param t Database Entity
   */
  void update(T t);

  /**
   * Deleting a database entity by id.
   *
   * @param id Entity id
   */
  void deleteById(Long id);
}
