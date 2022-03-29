package nz.ac.canterbury.seng302.identityprovider.exceptions;

/**
 * A custom exception for the case something occurs on a user who does not exist.
 */
public class UserDoesNotExistException extends IllegalArgumentException {
  public UserDoesNotExistException(String errorMessage) {
    super(errorMessage);
  }
}
