package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class SprintControllerTest {
    @Autowired
    private MockMvc mockMvc;

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
    public void getById() throws Exception {
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
    public void getManyByProjectId() throws Exception {
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
    public void getManyByNonExistentProjectId() throws Exception {
        var result = this.mockMvc.perform(
                get("/api/v1/projects/fake_project/sprints")
            )
            .andExpect(status().isNotFound());
    }

    @Test
    public void createNew() throws Exception {
        var project = new ProjectEntity("test project", null, Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2023-01-20T10:15:30.00Z"));
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
    public void createNewInvalidProject() throws Exception {
        var body = """
            {
                "name": "test sprint",
                "startDate": "2023-01-01T10:00:00.00Z",
                "endDate": "2023-01-15T10:00:00.00Z",
                "description": "test description"
            }
            """;

        this.mockMvc.perform(
                post("/api/v1/projects/fake_project/sprints")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    public void createNewNoDescription() throws Exception {
        var project = new ProjectEntity("test project", null, Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2023-01-20T10:15:30.00Z"));
        projectRepository.save(project);

        var apiPath = String.format("/api/v1/projects/%s/sprints", project.getId());
        var body = """
            {
                "name": "test sprint",
                "startDate": "2023-01-01T10:00:00.00Z",
                "endDate": "2023-01-15T10:00:00.00Z"
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
        assertNull(decodedResponse.description());
        assertEquals(Instant.parse("2023-01-01T10:00:00.00Z"), decodedResponse.startDate());
        assertEquals(Instant.parse("2023-01-15T10:00:00.00Z"), decodedResponse.endDate());
    }

    @Test
    public void updateInvalidSprint() throws Exception {
        var body = """
            {
                "name": "test sprint",
                "startDate": "2023-01-01T10:00:00.00Z",
                "endDate": "2023-01-15T10:00:00.00Z",
                "description": "test description"
            }
            """;

        this.mockMvc.perform(
                put("/api/v1/sprints/fake_sprint")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateValidSprint() throws Exception {
        var project = new ProjectEntity("test project", null, Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2023-01-20T10:15:30.00Z"));
        var sprint = new SprintEntity("pre-edit test sprint", "pre-test description", Instant.parse("2023-01-01T10:15:30.00Z"), Instant.parse("2023-01-03T10:15:30.00Z"));
        project.addSprint(sprint);
        projectRepository.save(project);
        sprintRepository.save(sprint);
        String projectId = project.getId();
        String sprintId = sprint.getId();
        var apiPath = String.format("/api/v1/sprints/%s", sprint.getId());
        var body = String.format("""
            {
                "projectId": "%s",
                "sprintId": "%s",
                "name": "post-edit test sprint",
                "startDate": "2023-01-04T10:00:00.00Z",
                "endDate": "2023-01-15T10:00:00.00Z"
            }
            """, projectId, sprintId);
        this.mockMvc.perform(
                put(apiPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
            .andExpect(status().isNoContent());

        // Check that update was persisted.
        var result = this.mockMvc.perform(
                get(String.format("/api/v1/sprints/%s", sprint.getId()))
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        var stringContent = result.getResponse().getContentAsString();
        var decodedResponse = objectMapper.readValue(stringContent, SprintContract.class);

        assertEquals("post-edit test sprint" , decodedResponse.name());
        assertNull(decodedResponse.description());
        assertEquals(Instant.parse("2023-01-04T10:00:00.00Z"), decodedResponse.startDate());
        assertEquals(Instant.parse("2023-01-15T10:00:00.00Z"), decodedResponse.endDate());
    }

    @Test
    public void deleteInvalidSprint() throws Exception {
        this.mockMvc.perform(delete("/api/v1/sprints/fake_sprint"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteValidSprint() throws Exception {
        var project = new ProjectEntity("test project", null, Instant.EPOCH, Instant.parse("2007-12-03T10:15:30.00Z"));
        var sprint = new SprintEntity("pre-edit test sprint", "pre-test description", Instant.EPOCH, Instant.parse("2007-12-03T10:15:30.00Z"));
        project.addSprint(sprint);
        projectRepository.save(project);
        sprintRepository.save(sprint);

        var apiPath = String.format("/api/v1/sprints/%s", sprint.getId());

        this.mockMvc.perform(delete(apiPath))
            .andExpect(status().isNoContent());

        // Check that update was persisted.
        this.mockMvc.perform(get(apiPath))
            .andExpect(status().isNotFound());
    }
}
