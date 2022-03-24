package nz.ac.canterbury.seng302.portfolio.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

  /**
   * A simple test to ensure that the Thymeleaf template is not broken for the registration form.
   *
   * @throws Exception if perform fails for some reason
   */
  @Test
  public void getRegistrationForm() throws Exception {
    // If Thymeleaf throws an exception, it will be caught via this test.
    this.mockMvc.perform(
        get(API_PATH))
        .andExpect(status().isOk());
  }

  @Test
  public void registerValidUser() throws Exception {
    // TODO: Does this require a database drop each time running?
    var validUser =
        new User(
            "Username",
            "Password",
            "FirstName",
            "Middle Names",
            "LastName",
            "Nickname",
            "Bio",
            "Pronouns",
            "email%40email.com");

    String postBody = buildPostBody(validUser);

    // TODO: Fix and figure out I guess
    given(this.service.register(validUser))
        .willReturn(
            UserRegisterResponse.newBuilder()
                .setIsSuccess(true)
                .setNewUserId(1)
                .setMessage("Mock executed successfully")
                .build()
        );

    var result =
        this.mockMvc
            .perform(
                post(API_PATH).contentType(MediaType.APPLICATION_FORM_URLENCODED).content(postBody))
            .andExpect(status().is2xxSuccessful())
            .andReturn();
    System.out.println(result.getResponse().getContentAsString());
  }
}
