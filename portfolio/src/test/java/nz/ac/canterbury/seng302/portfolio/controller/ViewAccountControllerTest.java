package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.portfolio.model.contract.UserContract;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class ViewAccountControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ViewAccountController accountController;

    @Autowired
    ObjectMapper mapper;

//    @Autowired
//    RegisterClientService registerClientService;


    private UserRegisterResponse newUser;
    private UserResponse responseUser;
    private GetUserByIdRequest getResponseUser = GetUserByIdRequest.newBuilder().setId(1).build();
    private UserAccountServiceGrpc.UserAccountServiceBlockingStub grpcService = Mockito.mock(UserAccountServiceGrpc.UserAccountServiceBlockingStub.class);



    @BeforeEach
    public void addNewUser() throws Exception {
//         newUser = registerClientService.register("SomeUserName", "thisisMypassWord", "MyFirstName",
//                "MyMiddle Name", "MyLastName", "Name", "THis is a mock profile", "Mr.",
//                "thisisanemail@fakeemail.com");

        List<UserRole> roles = new ArrayList<>();
        roles.add(UserRole.STUDENT);

        var responseUserBuilder = UserResponse.newBuilder();
        responseUserBuilder.setFirstName("First Name");
        responseUserBuilder.setLastName("Last Name");
        responseUserBuilder.setEmail("Thisisanemail@email.com");
        responseUserBuilder.setBio("THIS IS A BIO FIELD");
        responseUserBuilder.setPersonalPronouns("she/her");
        responseUserBuilder.addAllRoles(roles);
    }

    @Test
    public void getUserById() throws Exception {
        Mockito.when(grpcService.getUserAccountById(getResponseUser)).thenReturn(responseUser);
        var user = this.mockMvc.perform(get("/api/v1/account/" + 1))
                                .andExpect(status().isOk())
                                .andReturn();

        var userContract = mapper.readValue(user.getResponse().getContentAsString(), UserContract.class);
        assertEquals(userContract.firstName(), "MyFirstName");
        assertEquals(userContract.bio(), "THis is a mock profile");
        assertEquals(userContract.username(), "SomeUserName");
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
