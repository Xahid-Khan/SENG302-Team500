package nz.ac.canterbury.seng302.portfolio.controller;


import nz.ac.canterbury.seng302.portfolio.AuthorisationParamsHelper;
import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.SubscriptionContract;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.SubscriptionService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the HomePageController
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureWebTestClient
public class HomePageControllerTest {

    @Autowired
    private MockMvc mockMvc ;

    @Autowired
    private HomePageController controller;

    @MockBean
    private UserAccountService userAccountService;

    @MockBean
    private AuthStateService authStateService;

    @MockBean
    private SubscriptionService service;

    private final int USER_ID = 3;
    private final SubscriptionContract contract = new SubscriptionContract(1, 1);


    /**
     * Mocks the authentication service to return a valid student
     */
    @BeforeEach
    void setupBeforeEach() {
        Mockito.when(userAccountService.getUserById(any(int.class))).thenReturn(
                UserResponse.newBuilder()
                        .setId(USER_ID)
                        .setUsername("testing")
                        .build()
        );
        Mockito.when(authStateService.getId(any(PortfolioPrincipal.class))).thenReturn(3);
        AuthorisationParamsHelper.setParams("role", UserRole.STUDENT);

    }

    /**
     * This test makes sure that controller is loaded and running.
     *
     @throws Exception if mockMvc fails
     */
    @Test
    void contextLoads() throws Exception {
        Assertions.assertNotNull(controller);
    }

    @Test
    void subscribeTest() throws Exception {
        var body = """
                {
                    "userId": 1,
                    "groupId": 1
                }
                """;
        mockMvc.perform(post("/api/v1/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(service).subscribe(contract);
    }

    @Test
    void subscribeTestInvalid() throws Exception {
        var body = """
                {
                    "userId": 1
                }
                """;
        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(service).subscribe(contract);
    }


}
