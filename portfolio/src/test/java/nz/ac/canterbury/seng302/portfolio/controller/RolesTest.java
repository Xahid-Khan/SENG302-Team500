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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.security.auth.callback.TextOutputCallback;
import java.security.Principal;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.delete;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
public class RolesTest {

    @Autowired
    private MockMvc mockMvc ;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SprintRepository sprintRepository;

    private String projectId;


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
//
        Map<String, Object> claims = new HashMap<>();
        claims.put("unique_name", "SomeUserName");
        claims.put("nameid", 1);
        claims.put("name", "FistName");
        claims.put("role", "Student");

        var token = Jwts.builder()
                .setClaims(claims)
                .setSubject("SomeUserName")
                .setIssuer("Local Authority")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() * 1000))
                .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256)).compact();

//        Authentication auth = new UsernamePasswordAuthenticationToken(token, null);
//        SecurityContextHolder.getContext().setAuthentication(auth);

        AuthState.Builder newState = AuthState.newBuilder();
        newState.setName("User");
        newState.setRoleClaimType("Teacher");

        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(String.valueOf(newState.build()));


        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(apiPath)
                .principal(mockPrincipal)
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
