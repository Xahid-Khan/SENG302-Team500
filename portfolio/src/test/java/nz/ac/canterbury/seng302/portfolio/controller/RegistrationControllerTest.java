package nz.ac.canterbury.seng302.portfolio.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class RegistrationControllerTest {

  @Autowired MockMvc mockMvc;

  @MockBean
  private RegisterClientService service;

  private final String API_PATH = "/register";

  /**
   * A simple test to ensure that the Thymeleaf template is not broken for the registration form.
   *
   * @throws Exception if perform fails for some reason
   */
  @Test
  public void getRegistrationForm() throws Exception {
    this.mockMvc.perform(get(API_PATH)).andExpect((status().isOk()));
  }

  @Test
  public void registerValidUser() throws Exception {
    // TODO: Does this require a database drop each time running?
    // TODO: Move to integration test. Figure out how to run the IDP
    var validUser = new User(
            "Username",
            "Password",
            "FirstName",
            "Middle Names",
            "LastName",
            "Nickname",
            "Bio",
            "Pronouns",
            "email%40email.com"
    );

    var postBody = // Generated from a valid post request
            "username=" + validUser.username()
                    + "&email=" + validUser.email()
                    + "&password=" + validUser.password()
                    + "&firstName=" + validUser.firstName()
                    + "&middleName=" + validUser.middleName()
                    + "&lastName=" + validUser.lastName()
                    + "&nickname=" + validUser.nickname()
                    + "&pronouns=" + validUser.pronouns()
                    + "&bio=" + validUser.bio()
                    + "&register=0";

    // TODO: Fix and figure out I guess
    given(this.service.register(validUser))
            .willReturn(
                    UserRegistrationResponse.newBuilder()
                            .setIsSuccess(true)
                            .setNewUserId(1)
                            .setMessage("Mock executed successfully")
            );

    var result =
        this.mockMvc
            .perform(
                post(API_PATH)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content(postBody)
            )
            .andExpect(status().is2xxSuccessful())
            .andReturn();
    System.out.println(result.getResponse().getContentAsString());
  }
}
