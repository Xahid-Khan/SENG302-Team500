package nz.ac.canterbury.seng302.identityprovider.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RegisterServerServiceTest {

  @Autowired private RegisterServerService registerServerService;

  @Autowired private UserRepository userRepository;

  // A basic request to be used for tests here
  private UserRegisterRequest request = UserRegisterRequest.newBuilder()
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
    repository.deleteAll();
  }

  /**
   * Tests registering a completely valid user.
   */
  @Test
  public void registerValidUser() throws NoSuchAlgorithmException, InvalidKeySpecException {
    var response = registerServerService.register(request);

    // Ensures registration was a success
    assertTrue(response.getIsSuccess());
    // Ensure that the message is sent successfully
    assertEquals("Registered new user", response.getMessage().split(":", 2)[0]);
    // Ensure only 1 user exists
    assertEquals(1, repository.count());
    // Ensure user exists
    assertNotNull(repository.findByUsername("Username"));
  }

  /**
   * Runs a test by inputting the same user twice, which should cause a username error.
   */
  @Test
  public void registerDuplicateUsername() throws NoSuchAlgorithmException, InvalidKeySpecException {
    registerServerService.register(request);
    var response = registerServerService.register(request);

    // Ensure only 1 user exists
    assertEquals(1, userRepository.count());
    // Ensure it failed
    assertFalse(response.getIsSuccess());
    // Ensure that the message is sent successfully
    assertEquals("Error", response.getMessage().split(":", 2)[0]);
  }
}
