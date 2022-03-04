package nz.ac.canterbury.seng302.portfolio.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import nz.ac.canterbury.seng302.portfolio.model.contract.SprintContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class SprintControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SprintController controller;

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String projectId;

    @BeforeEach
    public void beforeEach() {
        projectRepository.deleteAll();
        sprintRepository.deleteAll();

        var project = new ProjectEntity("test project", null, Instant.EPOCH, Instant.parse("2007-12-03T10:15:30.00Z"));
        projectRepository.save(project);
        this.projectId = project.getId();
    }

    @Test
    public void getWithInvalidId() throws Exception {
        this.mockMvc.perform(get("/api/v1/sprints/1"))
            .andExpect(status().isNotFound());

        this.mockMvc.perform(get("/api/v1/sprints/this isn't a valid ID"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    public void createNew() throws Exception {
        var apiPath = String.format("/api/v1/projects/%s/sprints", projectId);
        var body = """
            {
                "name": "test sprint",
                "startDate": "2023-01-01T10:00:00.00Z",
                "endDate": "2023-01-15T10:00:00.00Z",
                "description": "test description"
            }
            """;

        var result = this.mockMvc.perform(
                post(apiPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        var stringContent = result.getResponse().getContentAsString();
        var decodedResponse = objectMapper.readValue(stringContent, SprintContract.class);

        assertEquals("test sprint" , decodedResponse.name());
        assertEquals("test description", decodedResponse.description());
        assertEquals(Instant.parse("2023-01-01T10:00:00.00Z"), decodedResponse.startDate());
        assertEquals(Instant.parse("2023-01-15T10:00:00.00Z"), decodedResponse.endDate());
    }
}
