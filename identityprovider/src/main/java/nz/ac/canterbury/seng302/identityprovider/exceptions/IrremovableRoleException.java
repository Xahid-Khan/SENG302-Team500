package nz.ac.canterbury.seng302.identityprovider.exceptions;

public class IrremovableRoleException extends IllegalArgumentException {
  public IrremovableRoleException(String errorMessage) {
    super(errorMessage);
  }
}
