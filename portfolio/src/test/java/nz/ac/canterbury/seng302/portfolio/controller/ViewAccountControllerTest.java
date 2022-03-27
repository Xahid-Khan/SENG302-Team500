package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.portfolio.model.contract.UserContract;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.GetUserByIdRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserAccountServiceGrpc;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
<<<<<<<<< Temporary merge branch 1
=========

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
>>>>>>>>> Temporary merge branch 2
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
         newUser = registerClientService.register("SomeUserName", "thisisMypassWord", "MyFirst Name",
                "MyMiddle Name", "MyLast Name", "Name", "THis is a mock profile", "Mr.",
                "thisisanemail@fakeemail.com");
    }

    @Test
    public void getUserById() throws Exception {
        var user = this.mockMvc.perform(get("/api/v1/account/" + newUser.getNewUserId()))
                                .andExpect(status().isOk())
                                .andReturn();
        var stringContent = user.getResponse().getContentAsString();
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
