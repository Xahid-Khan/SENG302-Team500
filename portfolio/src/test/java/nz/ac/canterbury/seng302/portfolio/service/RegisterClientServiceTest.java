package nz.ac.canterbury.seng302.portfolio.service;


import nz.ac.canterbury.seng302.portfolio.DTO.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RegisterClientServiceTest {

  @Autowired
  private RegisterClientService registerClientService;

  @Test
  public void TestValidUser() {
    User user = new User();
  }

}
