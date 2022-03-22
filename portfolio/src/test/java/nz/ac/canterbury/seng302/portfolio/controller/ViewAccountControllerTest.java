package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ViewAccountControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ViewAccountController accountController;

    @Autowired
    RegisterClientService registerClientService;


    private  UserRegisterResponse newUser;
    @BeforeEach
    public void addNewUser() throws Exception {
         newUser = registerClientService.register("SomeUserName", "thisisMypassWord", "MyFirstName",
                "MyMiddle Name", "MyLastName", "Name", "THis is a mock profile", "Mr.",
                "thisisanemail@fakeemail.com");
    }

    @Test
    public void getUserById() throws Exception {
        MvcResult user = this.mockMvc.perform(get("/api/v1/account/" + newUser.getNewUserId()))
                                .andExpect(status().isOk())
                                .andReturn();
        System.out.println(user);
        var stringContent = user.getResponse().getContentAsString().split("\n");

        assertEquals(stringContent[0].split(" ")[1], "\"SomeUserName\"");
        assertEquals(stringContent[1].split(" ")[1], "\"MyFirstName\"");
    }

    @Test
    public void invalidUserId() throws Exception {
        MvcResult users = this.mockMvc.perform(get("/api/v1/account/123"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void alphaUserId() throws Exception {
        MvcResult users = this.mockMvc.perform(get("/api/v1/account/abc"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

}
