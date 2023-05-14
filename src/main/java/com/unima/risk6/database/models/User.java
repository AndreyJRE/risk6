package com.unima.risk6.database.models;


import java.time.LocalDate;
import java.util.Objects;

/**
 * User model for database.
 *
 * @author astoyano
 */
public class User {

  private Long id;

  private String username;

  private String password;

  private String imagePath;

  private boolean active;

  private LocalDate createdAt;


  public User() {
  }

  /**
   * Constructor for a user.
   *
   * @param username  The username of the user.
   * @param password  The password of the user.
   * @param imagePath The path to the image of the user.
   */
  public User(String username, String password, String imagePath) {
    this.username = username;
    this.password = password;
    this.imagePath = imagePath;
  }

  /**
   * Constructor for a user.
   *
   * @param id        The id of the user.
   * @param username  The username of the user.
   * @param password  The password of the user.
   * @param imagePath The path to the image of the user.
   * @param active    The active status of the user.
   * @param createdAt The date the user was created.
   */
  public User(Long id, String username, String password, String imagePath, boolean active,
      LocalDate createdAt) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.imagePath = imagePath;
    this.active = active;
    this.createdAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDate createdAt) {
    this.createdAt = createdAt;
  }

  public String getImagePath() {
    return imagePath;
  }


  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  @Override
  public String toString() {
    return "User{ id=" + id + ", username='" + username + '\'' + ", password='" + password + '\''
        + ", imagePath='" + imagePath + '\'' + ", active=" + active + ", createdAt=" + createdAt
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return active == user.active && id.equals(user.id) && username.equals(user.username)
        && Objects.equals(password, user.password) && Objects.equals(imagePath,
        user.imagePath) && createdAt.equals(user.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, username, password, imagePath, active, createdAt);
  }
}
