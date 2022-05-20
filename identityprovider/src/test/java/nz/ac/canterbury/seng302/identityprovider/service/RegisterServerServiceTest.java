package nz.ac.canterbury.seng302.identityprovider.service;

import static org.junit.jupiter.api.Assertions.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RegisterServerServiceTest {

  @Autowired private RegisterServerService registerServerService;

  @Autowired private UserRepository userRepository;

  // A basic request to be used for tests here
  private UserRegisterRequest request =
      UserRegisterRequest.newBuilder()
          .setUsername("Username")
          .setPassword("Password")
          .setFirstName("FirstName")
          .setMiddleName("Middle Names")
          .setLastName("LastName")
          .setNickname("Nickname")
          .setBio("Bio")
          .setPersonalPronouns("Pronoun1/Pronoun2")
          .setEmail("email@email.email")
          .build();

  @BeforeEach
  private void clearDatabase() {
    userRepository.deleteAll();
  }

  /** Tests registering a completely valid user. */
  @Test
  public void registerValidUser() throws NoSuchAlgorithmException, InvalidKeySpecException {
    var response = registerServerService.register(request);

    // Ensures registration was a success
    assertTrue(response.getIsSuccess());
    // Ensure that the message is sent successfully
    assertEquals("Registered new user", response.getMessage().split(":", 2)[0]);
    // Ensure only 1 user exists
    assertEquals(1, userRepository.count());
    // Ensure user exists
    assertNotNull(userRepository.findByUsername("Username"));
  }

  /** Runs a test by inputting the same user twice, which should cause a username error. */
  @Test
  public void registerDuplicateUsername() throws NoSuchAlgorithmException, InvalidKeySpecException {
    registerServerService.register(request);
    var response = registerServerService.register(request);

    // Ensure only 1 user exists
    assertEquals(1, userRepository.count());
    // Ensure it failed
    assertFalse(response.getIsSuccess());
    // Ensure that the message is sent successfully
    assertTrue(response.getMessage().contains("Username already in use"));
  }

  /**
   * Attempts to register a user with an email already in use, which should cause an email error.
   */
  @Test
  public void registerDuplicateEmail() throws NoSuchAlgorithmException, InvalidKeySpecException {
    // Register the valid request to ensure there is data in the database.
    registerServerService.register(request);

    // Try register a user with a duplicate email address.
    UserRegisterRequest duplicateEmailRequest =
        UserRegisterRequest.newBuilder()
            .setUsername("NotUsedUsername")
            .setPassword("Password")
            .setFirstName("FirstName")
            .setMiddleName("Middle Names")
            .setLastName("LastName")
            .setNickname("Nickname")
            .setBio("Bio")
            .setPersonalPronouns("Pronoun1/Pronoun2")
            .setEmail("email@email.email")
            .build();
    var response = registerServerService.register(duplicateEmailRequest);

    // Ensure only 1 user exists
    assertEquals(1, userRepository.count());
    // Ensure it failed
    assertFalse(response.getIsSuccess());
    // Ensure that the message is sent successfully
    assertTrue(response.getMessage().contains("Email already in use"));
  }
}
