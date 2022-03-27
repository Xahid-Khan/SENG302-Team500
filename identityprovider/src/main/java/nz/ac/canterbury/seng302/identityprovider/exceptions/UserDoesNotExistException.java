package nz.ac.canterbury.seng302.identityprovider.exceptions;

public class UserDoesNotExistException extends IllegalArgumentException {
  public UserDoesNotExistException(String errorMessage) {
    super(errorMessage);
  }
}
