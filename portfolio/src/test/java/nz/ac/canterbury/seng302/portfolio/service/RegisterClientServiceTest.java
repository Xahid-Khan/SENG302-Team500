package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.DTO.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class RegisterClientServiceTest {

  @Autowired private RegisterClientService registerClientService;

  /**
   * As most the validation is handled by the controller, the only test here is for duplicate
   * usernames.
   */
  @Test
  public void TestDuplicateUsername() {
    registerClientService.register(null);
    // TODO: Set up mock database, create user in database, create user with same username, run
  }

  @Test
  public void TestValidUser() {

  }
}
