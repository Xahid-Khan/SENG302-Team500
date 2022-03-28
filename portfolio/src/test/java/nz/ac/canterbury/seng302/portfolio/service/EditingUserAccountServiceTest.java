package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.DTO.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class EditingUserAccountServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UpdateAccountService updateService;


    private User user = new User();

    @BeforeEach
    void setup() {
        user.setFirstName("First Name");
        user.setLastName("Last Name");
        user.setMiddleName("Middle Name");
        user.setUsername("King4Khan");
        user.setEmail("king4khan@somemail.com");
        user.setNickname("Khan");
        user.setBio("This is a temp user for testing...");
        user.setPronouns("He/His");
        user.setPassword("Password");
    }

    @Test
    public void rejectUsernameUpdateRequest() {
        updateService.updateAccount(1, user);
    }


}
