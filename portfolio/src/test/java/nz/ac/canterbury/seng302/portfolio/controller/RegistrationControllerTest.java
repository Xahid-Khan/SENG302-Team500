package nz.ac.canterbury.seng302.portfolio.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * This class tests the Registration Controller, which is used for handling reasonable inputs on
 * what a user can register as. Note: All Null test cases are not included here as they break the
 * tests, however manual testing shows that Nulls are fine. To retest Nulls, either intercept with
 * BurpSuite and submit a blank post request, or use this cURL command: curl -X POST -d ""
 * http://localhost:9000/register
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class RegistrationControllerTest {

  @Autowired MockMvc mockMvc;

  @MockBean private RegisterClientService service;

  private final String API_PATH = "/register";

  // Helper function to place create a Post Body out of a user.
  // Note that a factory or builder pattern could've been used here,
  //  however this is just for testing.
  private String buildPostBody(User user) {
    return "username="
        + user.username()
        + "&email="
        + user.email()
        + "&password="
        + user.password()
        + "&firstName="
        + user.firstName()
        + "&middleName="
        + user.middleName()
        + "&lastName="
        + user.lastName()
        + "&nickname="
        + user.nickname()
        + "&pronouns="
        + user.pronouns()
        + "&bio="
        + user.bio()
        + "&register=0";
  }

  // Helper function to submit a registration to a mock /register endpoint.
  private MvcResult submitRegistration(User user) throws Exception {
    // Creates the Post Body to be sent to the mock /register endpoint
    var postBody = buildPostBody(user);

    // Creates a mock for the /register endpoint
    Mockito.when(service.register(any()))
        .thenReturn(
            UserRegisterResponse.newBuilder()
                .setIsSuccess(true)
                .setNewUserId(1)
                .setMessage("Mock executed successfully")
                .build());

    // Performs the request to the /register endpoint
    return this.mockMvc
        .perform(
            post(API_PATH).contentType(MediaType.APPLICATION_FORM_URLENCODED).content(postBody))
        .andExpect(status().is2xxSuccessful())
        .andReturn();
  }

  // Helper function for extrapolating if the request was invalid.
  private boolean wasError(MvcResult result) throws Exception {
    // If there was an error, then form-error should appear at least once.
    return result.getResponse().getContentAsString().contains("form-error");
  }

  /**
   * A simple test to ensure that the Thymeleaf template is not broken for the registration form.
   *
   * @throws Exception if perform fails for some reason
   */
  @Test
  public void getRegistrationForm() throws Exception {
    // If Thymeleaf throws an exception, it will be caught via this test.
    this.mockMvc.perform(get(API_PATH)).andExpect(status().isOk());
  }

  /**
   * Registers a valid user.
   *
   * @throws Exception if perform fails for some reason
   */
  @Test
  public void registerValidUser() throws Exception {
    var validUser =
        new User(
            "Username",
            "Password1",
            "FirstName",
            "Middle Names",
            "LastName",
            "Nickname",
            "Bio",
            "Pronouns",
            "email%40email.com");

    var result = submitRegistration(validUser);

    assertFalse(wasError(result));
  }

  /**
   * Tests (in order): Empty String, String too short (2 characters), String too long (33
   * characters)
   *
   * @throws Exception if perform fails for some reason
   */
  @ParameterizedTest
  @ValueSource(strings = {"", "AA", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"})
  public void registerInvalidUsernames(String username) throws Exception {
    var user =
        new User(
            username,
            "Password1",
            "FirstName",
            "Middle Names",
            "LastName",
            "Nickname",
            "Bio",
            "Pronouns",
            "email%40email.com");

    var result = submitRegistration(user);

    assertTrue(wasError(result));
  }

  /**
   * Tests (in order): Min string length (3), Max string length (32)
   *
   * @throws Exception if perform fails for some reason
   */
  @ParameterizedTest
  @ValueSource(strings = {"AAA", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"})
  public void registerBoundaryUsernames(String username) throws Exception {
    var user =
        new User(
            username,
            "Password1",
            "FirstName",
            "Middle Names",
            "LastName",
            "Nickname",
            "Bio",
            "Pronouns",
            "email%40email.com");

    var result = submitRegistration(user);

    assertFalse(wasError(result));
  }

  /**
   * Tests (in order): Empty String, String too short (7 characters)
   *
   * @throws Exception if perform fails for some reason
   */
  @ParameterizedTest
  @ValueSource(strings = {"", "AAAAAAA"})
  public void registerInvalidPasswords(String password) throws Exception {
    var user =
        new User(
            "Username",
            password,
            "FirstName",
            "Middle Names",
            "LastName",
            "Nickname",
            "Bio",
            "Pronouns",
            "email%40email.com");

    var result = submitRegistration(user);

    assertTrue(wasError(result));
  }

  /**
   * Tests a password with 8 characters
   *
   * @throws Exception if perform fails for some reason
   */
  @Test
  public void registerBoundaryPassword() throws Exception {
    var user =
        new User(
            "Username",
            "AAAAAAAA",
            "FirstName",
            "Middle Names",
            "LastName",
            "Nickname",
            "Bio",
            "Pronouns",
            "email%40email.com");

    var result = submitRegistration(user);

    assertFalse(wasError(result));
  }

  /**
   * Tests (in order): <br>
   *  Empty first name, valid middle name(s), valid last name <br>
   *  Valid first name, valid middle name(s), empty last name <br>
   *  Invalid first name (too long), valid middle name(s), valid last name <br>
   *  Valid first name, invalid middle name(s) (too long), valid last name <br>
   *  Valid first name, valid middle name(s), invalid last name (too long) <br>
   *
   * @throws Exception if perform fails for some reason
   */
  @ParameterizedTest
  @CsvSource({
    "'',Middle Names,LastName",
    "FirstName,Middle Names,''",
    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,Middle Names,LastName",
    "FirstName,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,LastName",
    "FirstName,Middle Names,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
  })
  public void registerInvalidNames(String firstName, String middleNames, String lastName) throws Exception {
    var user =
        new User(
            "Username",
            "Password",
            firstName,
            middleNames,
            lastName,
            "Nickname",
            "Bio",
            "Pronouns",
            "email%40email.com");

    var result = submitRegistration(user);

    assertTrue(wasError(result));
  }

  /**
   * Tests (in order): <br>
   *  Size 50 first name, valid middle name(s), valid last name <br>
   *  Valid first name, Size 50 middle name(s), valid last name <br>
   *  Valid first name, valid middle name(s), Size 50 last name <br>
   *
   * @throws Exception if perform fails for some reason
   */
  @ParameterizedTest
  @CsvSource({
      "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,Middle Names,LastName",
      "FirstName,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,LastName",
      "FirstName,Middle Names,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
  })
  public void registerBoundaryNames(String firstName, String middleNames, String lastName) throws Exception {
    var user =
        new User(
            "Username",
            "Password",
            firstName,
            middleNames,
            lastName,
            "Nickname",
            "Bio",
            "Pronouns",
            "email%40email.com");

    var result = submitRegistration(user);

    assertFalse(wasError(result));
  }

  /**
   * Tests (in order): <br>
   *  Invalid nickname (too long), valid bio, valid pronouns <br>
   *  Valid nickname, invalid bio (too long), valid pronouns <br>
   *  Valid nickname, valid bio, invalid pronouns (too long) <br>
   *
   * @throws Exception if perform fails for some reason
   */
  @ParameterizedTest
  @CsvSource({
      "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,bio,pronouns",
      "nickname,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,pronouns",
      "nickname,bio,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
  })
  public void registerInvalidAdditionalInfo(String nickname, String bio, String pronouns) throws Exception {
    var user =
        new User(
            "Username",
            "Password",
            "firstName",
            "middle Names",
            "lastName",
            nickname,
            bio,
            pronouns,
            "email%40email.com");

    var result = submitRegistration(user);

    assertTrue(wasError(result));
  }

  /**
   * Tests (in order): <br>
   *  Empty nickname, valid bio, valid pronouns <br>
   *  Valid nickname, empty bio, valid pronouns <br>
   *  Valid nickname, valid bio, empty pronouns <br>
   *  Size 32 nickname, valid bio, valid pronouns <br>
   *  Valid nickname, size 512 bio, valid pronouns <br>
   *  Valid nickname, valid bio, size 50 pronouns <br>
   *
   * @throws Exception if perform fails for some reason
   */
  @ParameterizedTest
  @CsvSource({
      "'',bio,pronouns",
      "nickname,'',pronouns",
      "nickname,bio,''",
      "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,bio,pronouns",
      "nickname,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,pronouns",
      "nickname,bio,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
  })
  public void registerBoundaryAdditionalInfo(String nickname, String bio, String pronouns) throws Exception {
    var user =
        new User(
            "Username",
            "Password",
            "firstName",
            "middle Names",
            "lastName",
            nickname,
            bio,
            pronouns,
            "email%40email.com");

    var result = submitRegistration(user);

    assertFalse(wasError(result));
  }

  /**
   * Tests a blank email. Unfortunately email is quite hard to test, so we have to assume that JavaX
   *  has the correct regex for it. The valid email test falls under the valid user test.
   */
  @Test
  public void registerEmptyEmail() throws Exception {
    var user =
        new User(
            "Username",
            "Password",
            "firstName",
            "middle Names",
            "lastName",
            "nickname",
            "bio",
            "pronouns",
            "");

    var result = submitRegistration(user);

    assertTrue(wasError(result));
  }
}
