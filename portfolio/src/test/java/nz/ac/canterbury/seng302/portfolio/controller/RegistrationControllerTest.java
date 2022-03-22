package nz.ac.canterbury.seng302.portfolio.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class RegistrationControllerTest {

  @Autowired MockMvc mockMvc;

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
    var result =
        this.mockMvc
            .perform(
                post(API_PATH)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content(
                        // Generated from a valid post request
                        "username=Username"
                            + "&email=email%40email.com"
                            + "&password=Password"
                            + "&firstName=FirstName"
                            + "&middleName=Middle+Name"
                            + "&lastName=LastName"
                            + "&nickname=Nickname"
                            + "&pronouns=Personal+Pronouns"
                            + "&bio=Bio"
                            + "&register=0"))
            .andExpect(status().is2xxSuccessful())
            .andReturn();
    System.out.println(result.getResponse().getContentAsString());
  }
}
