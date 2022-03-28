package nz.ac.canterbury.seng302.identityprovider.exceptions;

/**
 * This exception handles the case of if a specific irremovable role is attempted to be removed.
 */
public class IrremovableRoleException extends IllegalArgumentException {
  public IrremovableRoleException(String errorMessage) {
    super(errorMessage);
  }
}
