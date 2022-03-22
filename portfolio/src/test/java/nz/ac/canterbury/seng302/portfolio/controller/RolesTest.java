package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.SprintEntity;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.security.auth.callback.TextOutputCallback;
import java.security.Principal;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.delete;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest
@AutoConfigureWebTestClient
public class RolesTest {
    /*@Autowired
    private WebApplicationContext context;*/

    @Autowired
    private MockMvc mockMvc ;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SprintRepository sprintRepository;

    private String projectId;


    @BeforeEach
    public void setup() {
        /*mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();*/
    }


    /**
     * This method will make sure the database is reset prior to testing,
     * and it will create a mock project and sprint and save it in the database
     * Assign the Project ID of newly created project to the variable projectId.
     */
    @BeforeEach
    public void beforeEach() {
        projectRepository.deleteAll();
        var project1 = new ProjectEntity(
                "Project 1",
                "This is a test Project",
                Instant.EPOCH,
                Instant.parse("2022-03-03T10:15:30.00Z")
        );

        var sprint = new SprintEntity("New sprint", "My New Sprint Description", Instant.ofEpochSecond(120), Instant.ofEpochSecond(360));
        project1.addSprint(sprint);

        projectRepository.save(project1);
        sprintRepository.save(sprint);

        projectId = project1.getId();
    }

    @Test
    public void removeProject() throws Exception {
        var apiPath = "/api/v1/projects/" + projectId;

        AuthState.Builder newState = AuthState.newBuilder();
        newState.setName("User");
        newState.setRoleClaimType("Teacher");

        // With complements to: https://stackoverflow.com/a/46631015
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
        SecurityContextHolder.setContext(securityContext);

        AuthState principal = newState.build();
        Mockito.when(authentication.getPrincipal()).thenReturn(principal);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(apiPath)
                .accept(MediaType.APPLICATION_JSON);

        var result = this.mockMvc.perform(requestBuilder)
                                    .andReturn();

        System.out.println(result);

//        this.mockMvc.perform(get(apiPath))
//                .andExpect(status().isNotFound());
//
//        this.mockMvc.perform(delete(apiPath))
//                .andExpect(status().isBadRequest());
//
//        this.mockMvc.perform(delete("/api/v1/projects/some_project"))
//                .andExpect(status().isBadRequest());
//
//        this.mockMvc.perform(delete("/api/v1/projects/123456"))
//                .andExpect(status().isBadRequest());
    }
}
