package nz.ac.canterbury.seng302.identityprovider.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import io.grpc.stub.StreamObserver;
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

  @Autowired
  private RegisterServerService registerServerService;

  private StreamObserver<UserRegisterResponse> observer = mock(StreamObserver.class);

  private StreamObserver<EditUserResponse> editObserver = mock(StreamObserver.class);

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

  // CAUTION! NEVER RUN UNIT TESTS IN PRODUCTION. TODO: How do we get around this?
  @BeforeEach
  private void clearDatabase() {
    registerServerService.getRepository().deleteAll();
  }
  /**
   * Tests registering a completely valid user.
   * Credit to https://stackoverflow.com/a/49872463
   *  for providing how to run a Mockito mock observer.
   */
  @Test
  public void registerValidUser() {
    registerServerService.register(request, observer);

    // Ensure request was only run once
    Mockito.verify(observer, times(1)).onCompleted();
    // Set up a captor for the response
    ArgumentCaptor<UserRegisterResponse> captor
        = ArgumentCaptor.forClass(UserRegisterResponse.class);
    // Capture the response
    Mockito.verify(observer, times(1)).onNext(captor.capture());
    // Get the UserRegisterResponse from the captor
    UserRegisterResponse response = captor.getValue();
    // Check it was successful
    assertTrue(response.getIsSuccess());
    // Ensure that the message is sent successfully
    assertEquals("Registered new user", response.getMessage().split(":", 2)[0]);
    // Ensure only 1 user exists
    assertEquals(1, registerServerService.getRepository().count());
    // Ensure user exists
    assertTrue(registerServerService.getRepository().findByUsername("Username") != null);
  }

  /**
   * Runs a test by inputting the same user twice, which should cause a username error.
   */
  @Test
  public void registerDuplicateUsername() {
    registerServerService.register(request, observer);
    registerServerService.register(request, observer);
    // Ensure request was only ran twice
    Mockito.verify(observer, times(2)).onCompleted();
    // Ensure only 1 user exists
    assertEquals(1, registerServerService.getRepository().count());
    // Set up a captor for the second response
    ArgumentCaptor<UserRegisterResponse> captor
        = ArgumentCaptor.forClass(UserRegisterResponse.class);
    // Capture the response
    Mockito.verify(observer, times(2)).onNext(captor.capture());
    // Get the UserRegisterResponse from the captor
    UserRegisterResponse response = captor.getValue();
    // Ensure it failed
    assertTrue(!response.getIsSuccess());
    // Ensure that the message is sent successfully
    assertEquals("Error", response.getMessage().split(":", 2)[0]);
  }

  @Test
  public void editValidUser() {

    registerServerService.register(request, observer);
    int userID = registerServerService.getRepository().findByUsername("Username").getId();
    EditUserRequest newRequest = EditUserRequest.newBuilder()
            .setUserId(userID)
            .setFirstName("FirstName")
            .setMiddleName("Middle Names")
            .setLastName("NewLastName")
            .setNickname("Nickname")
            .setBio("Bio")
            .setPersonalPronouns("Pronoun1/Pronoun2")
            .setEmail("email@email.email")
            .build();
    registerServerService.editUser(newRequest, editObserver);

    // Ensure request was only run once
    Mockito.verify(editObserver, times(1)).onCompleted();
    // Set up a captor for the response
    ArgumentCaptor<EditUserResponse> captor
            = ArgumentCaptor.forClass(EditUserResponse.class);
    // Capture the response
    Mockito.verify(editObserver, times(1)).onNext(captor.capture());
    // Get the UserRegisterResponse from the captor
    EditUserResponse response = captor.getValue();
    // Check it was successful
    assertTrue(response.getIsSuccess());
    // Ensure that the message is sent successfully
    assertEquals("Updated details for user", response.getMessage().split(":", 2)[0]);
    // Ensure only 1 user exists
    assertEquals(1, registerServerService.getRepository().count());
    // Ensure user exists
    assertTrue(registerServerService.getRepository().findByUsername("Username") != null);

    assertEquals("NewLastName", registerServerService.getRepository().findByUsername("Username").getLastName());
  }

  @Test
  public void editNonExistentUser() {
    EditUserRequest newRequest = EditUserRequest.newBuilder()
            .setUserId(1)
            .setFirstName("FirstName")
            .setMiddleName("Middle Names")
            .setLastName("NewLastName")
            .setNickname("Nickname")
            .setBio("Bio")
            .setPersonalPronouns("Pronoun1/Pronoun2")
            .setEmail("email@email.email")
            .build();
    registerServerService.editUser(newRequest, editObserver);

    // Ensure request was only run once
    Mockito.verify(editObserver, times(1)).onCompleted();
    // Set up a captor for the response
    ArgumentCaptor<EditUserResponse> captor
            = ArgumentCaptor.forClass(EditUserResponse.class);
    // Capture the response
    Mockito.verify(editObserver, times(1)).onNext(captor.capture());
    // Get the UserRegisterResponse from the captor
    EditUserResponse response = captor.getValue();
    // Check it was successful
    assertFalse(response.getIsSuccess());

    assertEquals("Error: User not in database", response.getMessage());
  }

}
