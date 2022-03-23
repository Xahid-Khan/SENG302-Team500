package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.GetAuthorizationParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
public class RolesTest {

    @Autowired
    private MockMvc mockMvc ;

    private int projectId = 100;

    /**
     * This test bock test if the user can access the remove project API end-point
     * @throws Exception
     */
    @Test
    public void removeProject() throws Exception {
        var apiPath = "/api/v1/projects/" + projectId;
        GetAuthorizationParams param1 = new GetAuthorizationParams("role", "Student");

        this.mockMvc.perform(delete(apiPath))
                                .andExpect(status().isForbidden())
                                .andReturn();

        GetAuthorizationParams param2 = new GetAuthorizationParams("role", "Teacher");
        this.mockMvc.perform(delete(apiPath))
                .andExpect(status().isBadRequest());

        GetAuthorizationParams param3 = new GetAuthorizationParams("role", "COORDINATOR");
        this.mockMvc.perform(delete(apiPath))
                .andExpect(status().isBadRequest());

        GetAuthorizationParams param4 = new GetAuthorizationParams("role", "COORDINATOR, Teacher");
        this.mockMvc.perform(delete("/api/v1/projects/some_project"))
                .andExpect(status().isBadRequest());

        GetAuthorizationParams param5 = new GetAuthorizationParams("role", "COORDINATOR, Teacher, Student");
        this.mockMvc.perform(delete("/api/v1/projects/123456"))
                .andExpect(status().isBadRequest());
    }


    /**
     * This test bock test if the user can access the View ALl project API end-point
     * @throws Exception
     */
    @Test
    public void viewAllProjects() throws Exception {
        var apiPath = "/api/v1/projects/";
        GetAuthorizationParams param1 = new GetAuthorizationParams("role", "Student");

        this.mockMvc.perform(get(apiPath))
                .andExpect(status().isForbidden())
                .andReturn();

        GetAuthorizationParams param2 = new GetAuthorizationParams("role", "Teacher");
        this.mockMvc.perform(get(apiPath))
                .andExpect(status().isOk());

        GetAuthorizationParams param3 = new GetAuthorizationParams("role", "COORDINATOR");
        this.mockMvc.perform(get(apiPath))
                .andExpect(status().isOk());

        GetAuthorizationParams param4 = new GetAuthorizationParams("role", "COORDINATOR, Teacher");
        this.mockMvc.perform(get("/api/v1/projects/some_project"))
                .andExpect(status().isNotFound());

        GetAuthorizationParams param5 = new GetAuthorizationParams("role", "COORDINATOR, Teacher, Student");
        this.mockMvc.perform(get("/api/v1/projects/123456"))
                .andExpect(status().isNotFound());
    }

    /**
     * This test bock test if the user can access the update project API end-point
     * @throws Exception
     */
    @Test
    public void updateProject() throws Exception {
        var apiPath = "/api/v1/projects/" + projectId;
        var body = """
                {
                    "name": "NewName Project",
                    "description": "Updated project details",
                    "startDate": "2023-01-01T10:00:00.00Z",
                    "endDate": "2023-01-01T10:00:00.00Z"
                }
                """;
        GetAuthorizationParams param1 = new GetAuthorizationParams("role", "Student");

        this.mockMvc.perform(put(apiPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andReturn();

        GetAuthorizationParams param2 = new GetAuthorizationParams("role", "Teacher");
        this.mockMvc.perform(put(apiPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());

        GetAuthorizationParams param3 = new GetAuthorizationParams("role", "COORDINATOR");
        this.mockMvc.perform(put(apiPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());

        GetAuthorizationParams param4 = new GetAuthorizationParams("role", "COORDINATOR, Teacher");
        this.mockMvc.perform(put("/api/v1/projects/some_project"))
                .andExpect(status().isBadRequest());

        GetAuthorizationParams param5 = new GetAuthorizationParams("role", "COORDINATOR, Teacher, Student");
        this.mockMvc.perform(put("/api/v1/projects/123456"))
                .andExpect(status().isBadRequest());
    }
}
