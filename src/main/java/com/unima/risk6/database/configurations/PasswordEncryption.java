package com.unima.risk6.database.configurations;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * This class provides methods for encrypting and validating passwords using SHA-256 algorithm and
 * Base64 encoding.
 *
 * @author astoyano
 */
public class PasswordEncryption {

  /**
   * Encrypts the given password using SHA-256 algorithm and Base64 encoding.
   *
   * @param password The password to be encrypted.
   * @return The encrypted password as a Base64 encoded string.
   * @throws RuntimeException if there is an error while encrypting the password.
   */
  public static String encryptPassword(String password) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hashedPassword);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error: Unable to encrypt password", e);
    }
  }

  /**
   * Validates the given password against the encrypted password.
   *
   * @param password          The password to be validated.
   * @param encryptedPassword The encrypted password to compare against.
   * @return true if the password is valid, false otherwise.
   */
  public static boolean validatePassword(String password, String encryptedPassword) {
    String encryptedInput = encryptPassword(password);
    return encryptedInput.equals(encryptedPassword);
  }
}
