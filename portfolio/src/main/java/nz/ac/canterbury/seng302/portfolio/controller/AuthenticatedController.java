package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * This controller always adds user information to any page which requires authorization to see.
 * This allows any details about the user to be rendered or accessed (based on their current token).
 */
@Controller
public abstract class AuthenticatedController {
  @Autowired private AuthStateService authStateService;

  @Autowired private UserAccountService userAccountService;

  /**
   * Loads the user model into the view based on their token.
   *
   * @param principal the user's token
   * @return a UserResponse object with all the user details
   */
  @ModelAttribute("user")
  public UserResponse getUser(@AuthenticationPrincipal PortfolioPrincipal principal) {
    int userId = getUserId(principal);

    return userAccountService.getUserById(userId);
  }

  /**
   * Loads the user's ID into the view based on the token. The reason this is separate to the user
   * is in the instance that a user wants to view another user, the user model attribute will need
   * to be overridden. As such, this method provides a way to consistently ensure the users ACTUAL
   * ID is also loaded for the navbar for instance.
   *
   * @param principal the user's token
   * @return the user's ID
   */
  @ModelAttribute("userId")
  public int getUserId(@AuthenticationPrincipal PortfolioPrincipal principal) {
    return authStateService.getId(principal);
  }

  /**
   * Loads the user's username into the view based on the token. The reason this is separate to the
   * user is in the instance that a user wants to view another user, the user model attribute will
   * need to be overridden. As such, this method provides a way to consistently ensure the users
   * ACTUAL username is also loaded for the navbar for instance.
   *
   * @param principal the user's token
   * @return the user's username
   */
  @ModelAttribute("username")
  public String getUserName(@AuthenticationPrincipal PortfolioPrincipal principal) {
    return getUser(principal).getUsername();
  }
}
