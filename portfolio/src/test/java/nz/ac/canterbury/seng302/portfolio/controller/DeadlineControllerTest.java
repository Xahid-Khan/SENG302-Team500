package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.portfolio.AuthorisationParamsHelper;
import nz.ac.canterbury.seng302.portfolio.model.contract.DeadlineContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.DeadlineEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.repository.DeadlineRepository;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
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
public class DeadlineControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeadlineRepository deadlineRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach() {
        deadlineRepository.deleteAll();
        projectRepository.deleteAll();

        AuthorisationParamsHelper.setParams("role", "TEACHER");
    }

    @Test
    public void getWithInvalidId() throws Exception {
        this.mockMvc.perform(get("/api/v1/deadlines/1"))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(get("/api/v1/deadlines/this isn't a valid ID"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getById() throws Exception {
        AuthorisationParamsHelper.setParams("role", "STUDENT");
        var project = new ProjectEntity("test project", null, Instant.EPOCH, Instant.parse("2007-12-03T10:15:30.00Z"));
        var deadline = new DeadlineEntity("testdeadline", "test description", Instant.ofEpochSecond(120));
        project.addDeadline(deadline);
        projectRepository.save(project);
        deadlineRepository.save(deadline);

        var result = this.mockMvc.perform(
                        get(String.format("/api/v1/deadlines/%s", deadline.getId()))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var stringContent = result.getResponse().getContentAsString();
        var decodedResponse = objectMapper.readValue(stringContent, DeadlineContract.class);

        assertEquals(1, decodedResponse.orderNumber());
        assertEquals("testdeadline" , decodedResponse.name());
        assertEquals("test description", decodedResponse.description());
        assertEquals(Instant.ofEpochSecond(120), decodedResponse.startDate());
    }

    @Test
    public void getManyByProjectId() throws Exception {
        AuthorisationParamsHelper.setParams("role", "STUDENT");
        var project = new ProjectEntity("test project", null, Instant.EPOCH, Instant.parse("2007-12-03T10:15:30.00Z"));
        var deadline = new DeadlineEntity("deadline", "test description", Instant.ofEpochSecond(120));
        var deadline2 = new DeadlineEntity("testdeadline", "test description 2", Instant.ofEpochSecond(420));
        project.addDeadline(deadline);
        project.addDeadline(deadline2);
        projectRepository.save(project);
        deadlineRepository.save(deadline);
        deadlineRepository.save(deadline2);

        var result = this.mockMvc.perform(
                        get(String.format("/api/v1/projects/%s/deadlines", project.getId()))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var stringContent = result.getResponse().getContentAsString();
        var decodedResponse = objectMapper.readValue(stringContent, new TypeReference<ArrayList<DeadlineContract>>(){});

        var receivedDeadline1 = decodedResponse.get(0);
        var receivedDeadline2 = decodedResponse.get(1);

        assertEquals(1, receivedDeadline1.orderNumber());
        assertEquals("deadline" , receivedDeadline1.name());
        assertEquals("test description", receivedDeadline1.description());
        assertEquals(Instant.ofEpochSecond(120), receivedDeadline1.startDate());

        assertEquals(2, receivedDeadline2.orderNumber());
        assertEquals("testdeadline" , receivedDeadline2.name());
        assertEquals("test description 2", receivedDeadline2.description());
        assertEquals(Instant.ofEpochSecond(420), receivedDeadline2.startDate());
    }

    @Test
    public void getManyByNonExistentProjectId() throws Exception {
        AuthorisationParamsHelper.setParams("role", "STUDENT");
        var result = this.mockMvc.perform(
                        get("/api/v1/projects/fake_project/deadlines")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void createNew() throws Exception {
        var project = new ProjectEntity("test project", null, Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2023-01-20T10:15:30.00Z"));
        projectRepository.save(project);

        var apiPath = String.format("/api/v1/projects/%s/deadlines", project.getId());
        var body = """
            {
                "name": "testdeadline",
                "startDate": "2023-01-01T10:00:00.00Z",
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
        var decodedResponse = objectMapper.readValue(stringContent, DeadlineContract.class);

        assertEquals("testdeadline" , decodedResponse.name());
        assertEquals("test description", decodedResponse.description());
        assertEquals(Instant.parse("2023-01-01T10:00:00.00Z"), decodedResponse.startDate());
    }

    @Test
    public void createNewInvalidProject() throws Exception {
        var body = """
            {
                "name": "testdeadline",
                "startDate": "2023-01-01T10:00:00.00Z",
                "description": "test description"
            }
            """;

        this.mockMvc.perform(
                        post("/api/v1/projects/fake_project/deadlines")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void createNewNoDescription() throws Exception {
        var project = new ProjectEntity("test project", null, Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2023-01-20T10:15:30.00Z"));
        projectRepository.save(project);

        var apiPath = String.format("/api/v1/projects/%s/deadlines", project.getId());
        var body = """
            {
                "name": "testdeadline",
                "startDate": "2023-01-01T10:00:00.00Z"
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
        var decodedResponse = objectMapper.readValue(stringContent, DeadlineContract.class);

        assertEquals("testdeadline" , decodedResponse.name());
        assertNull(decodedResponse.description());
        assertEquals(Instant.parse("2023-01-01T10:00:00.00Z"), decodedResponse.startDate());
    }

    @Test
    public void updateInvalidDeadline() throws Exception {
        var body = """
            {
                "name": "testdeadline",
                "startDate": "2023-01-01T10:00:00.00Z"
                "description": "test description"
            }
            """;

        this.mockMvc.perform(
                        put("/api/v1/deadlines/fake_deadline")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateValidDeadline() throws Exception {
        var project = new ProjectEntity("test project", null, Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2023-01-20T10:15:30.00Z"));
        var deadline = new DeadlineEntity("preedittestdeadline", "pre-test description", Instant.parse("2023-01-02T10:00:00.00Z"));
        project.addDeadline(deadline);
        projectRepository.save(project);
        deadlineRepository.save(deadline);
        String projectId = project.getId();
        String deadlineId = deadline.getId();
        var apiPath = String.format("/api/v1/deadlines/%s", deadline.getId());
        var body = String.format("""
            {
                "projectId": "%s",
                "deadlineId": "%s",
                "name": "postedittestdeadline",
                "startDate": "2023-01-04T10:00:00.00Z"
            }
            """, projectId, deadlineId);
        this.mockMvc.perform(
                        put(apiPath)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isOk());

        // Check that update was persisted.
        var result = this.mockMvc.perform(
                        get(String.format("/api/v1/deadlines/%s", deadline.getId()))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        var stringContent = result.getResponse().getContentAsString();
        var decodedResponse = objectMapper.readValue(stringContent, DeadlineContract.class);

        assertEquals("postedittestdeadline" , decodedResponse.name());
        assertNull(decodedResponse.description());
        assertEquals(Instant.parse("2023-01-04T10:00:00.00Z"), decodedResponse.startDate());
    }

    @Test
    public void deleteInvalidDeadline() throws Exception {
        this.mockMvc.perform(delete("/api/v1/deadlines/fake_deadline"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteValidDeadline() throws Exception {
        var project = new ProjectEntity("test project", null, Instant.EPOCH, Instant.parse("2007-12-03T10:15:30.00Z"));
        var deadline = new DeadlineEntity("preedittestdeadline", "pre-test description", Instant.parse("2007-12-03T10:15:30.00Z"));
        project.addDeadline(deadline);
        projectRepository.save(project);
        deadlineRepository.save(deadline);

        var apiPath = String.format("/api/v1/deadlines/%s", deadline.getId());

        this.mockMvc.perform(delete(apiPath))
                .andExpect(status().isNoContent());

        // Check that update was persisted.
        this.mockMvc.perform(get(apiPath))
                .andExpect(status().isNotFound());
    }

    @Test
    public void tryCreateNewAsStudent() throws Exception {
        AuthorisationParamsHelper.setParams("role", "STUDENT");

        var project = new ProjectEntity("test project", null, Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2023-01-20T10:15:30.00Z"));
        projectRepository.save(project);

        var apiPath = String.format("/api/v1/projects/%s/deadlines", project.getId());
        var body = """
            {
                "name": "testdeadline",
                "startDate": "2023-01-01T10:00:00.00Z"
            }
            """;

        this.mockMvc.perform(
                        post(apiPath)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void tryUpdateValidDeadlineAsStudent() throws Exception {
        AuthorisationParamsHelper.setParams("role", "STUDENT");

        var project = new ProjectEntity("test project", null, Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2023-01-20T10:15:30.00Z"));
        var deadline = new DeadlineEntity("preedittestdeadline", "pre-test description", Instant.parse("2023-01-03T10:15:30.00Z"));
        project.addDeadline(deadline);
        projectRepository.save(project);
        deadlineRepository.save(deadline);
        String projectId = project.getId();
        String deadlineId = deadline.getId();
        var apiPath = String.format("/api/v1/deadlines/%s", deadline.getId());
        var body = String.format("""
            {
                "projectId": "%s",
                "deadlineId": "%s",
                "name": "postedittestdeadline",
                "startDate": "2023-01-04T10:00:00.00Z"
            }
            """, projectId, deadlineId);
        this.mockMvc.perform(
                        put(apiPath)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body)
                )
                .andExpect(status().isForbidden());

    }

    @Test
    public void tryDeleteDeadlineAsStudent() throws Exception {
        AuthorisationParamsHelper.setParams("role", "STUDENT");
        var project = new ProjectEntity("test project", null, Instant.EPOCH, Instant.parse("2007-12-03T10:15:30.00Z"));
        var deadline = new DeadlineEntity("preedittestdeadline", "pre-test description", Instant.parse("2007-12-03T10:15:30.00Z"));
        project.addDeadline(deadline);
        projectRepository.save(project);
        deadlineRepository.save(deadline);

        var apiPath = String.format("/api/v1/deadlines/%s", deadline.getId());

        this.mockMvc.perform(delete(apiPath))
                .andExpect(status().isForbidden());
    }
}
