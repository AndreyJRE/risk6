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
   * @param id Entity ID
   * @return An optional instance of the entity with the specified ID.
   */
  Optional<T> get(Long id);

  /**
   * @return A list of all instances of the entity.
   */
  List<T> getAll();

  /**
   * Save a object in Database
   *
   * @param t Database Entity
   * @return ID of Database
   */
  Long save(T t);

  /**
   * Update a entity in database
   *
   * @param t Database Entity
   */
  void update(T t);

  /**
   * Deleting a database entity by id
   *
   * @param id Entity id
   */
  void deleteById(Long id);
}
