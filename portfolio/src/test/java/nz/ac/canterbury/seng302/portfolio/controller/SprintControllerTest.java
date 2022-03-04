package nz.ac.canterbury.seng302.portfolio.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import nz.ac.canterbury.seng302.portfolio.model.contract.SprintContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.SprintEntity;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @BeforeEach
    public void beforeEach() {
        sprintRepository.deleteAll();
        projectRepository.deleteAll();
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
        var project = new ProjectEntity("test project", null, Instant.EPOCH, Instant.parse("2007-12-03T10:15:30.00Z"));
        projectRepository.save(project);

        var apiPath = String.format("/api/v1/projects/%s/sprints", project.getId());
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

    @Test
    public void fetchById() throws Exception {
        var project = new ProjectEntity("test project", null, Instant.EPOCH, Instant.parse("2007-12-03T10:15:30.00Z"));
        var sprint = new SprintEntity("test sprint", "test description", Instant.ofEpochSecond(120), Instant.ofEpochSecond(360));
        project.addSprint(sprint);
        projectRepository.save(project);
        sprintRepository.save(sprint);

        var result = this.mockMvc.perform(
            get(String.format("/api/v1/sprints/%s", sprint.getId()))
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        var stringContent = result.getResponse().getContentAsString();
        var decodedResponse = objectMapper.readValue(stringContent, SprintContract.class);

        assertEquals(1, decodedResponse.orderNumber());
        assertEquals("test sprint" , decodedResponse.name());
        assertEquals("test description", decodedResponse.description());
        assertEquals(Instant.ofEpochSecond(120), decodedResponse.startDate());
        assertEquals(Instant.ofEpochSecond(360), decodedResponse.endDate());
    }

    @Test
    public void fetchManyByProjectId() throws Exception {
        var project = new ProjectEntity("test project", null, Instant.EPOCH, Instant.parse("2007-12-03T10:15:30.00Z"));
        var sprint = new SprintEntity("test sprint", "test description", Instant.ofEpochSecond(120), Instant.ofEpochSecond(360));
        var sprint2 = new SprintEntity("test sprint 2", "test description 2", Instant.ofEpochSecond(420), Instant.ofEpochSecond(480));
        project.addSprint(sprint);
        project.addSprint(sprint2);
        projectRepository.save(project);
        sprintRepository.save(sprint);
        sprintRepository.save(sprint2);

        var result = this.mockMvc.perform(
            get(String.format("/api/v1/projects/%s/sprints", project.getId()))
        )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        var stringContent = result.getResponse().getContentAsString();
        var decodedResponse = objectMapper.readValue(stringContent, new TypeReference<ArrayList<SprintContract>>(){});

        var receivedSprint1 = decodedResponse.get(0);
        var receivedSprint2 = decodedResponse.get(1);

        assertEquals(1, receivedSprint1.orderNumber());
        assertEquals("test sprint" , receivedSprint1.name());
        assertEquals("test description", receivedSprint1.description());
        assertEquals(Instant.ofEpochSecond(120), receivedSprint1.startDate());
        assertEquals(Instant.ofEpochSecond(360), receivedSprint1.endDate());

        assertEquals(2, receivedSprint2.orderNumber());
        assertEquals("test sprint 2" , receivedSprint2.name());
        assertEquals("test description 2", receivedSprint2.description());
        assertEquals(Instant.ofEpochSecond(420), receivedSprint2.startDate());
        assertEquals(Instant.ofEpochSecond(480), receivedSprint2.endDate());
    }

    @Test
    public void fetchManyByNonExistentProjectId() throws Exception {
        var result = this.mockMvc.perform(
            get("/api/v1/projects/fake_project/sprints")
        )
            .andExpect(status().isNotFound());
    }
}
