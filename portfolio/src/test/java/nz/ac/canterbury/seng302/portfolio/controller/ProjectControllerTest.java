package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ProjectControllerTest<project1> {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ProjectController projectController;

    @Autowired
    ProjectRepository projectRepository;

    private String projectId;

    @BeforeEach
    public void beforeEach() {
        projectRepository.deleteAll();
        var project1 = new ProjectEntity(
                "Project 1",
                "This is a test Project",
                Instant.EPOCH,
                Instant.parse("2022-03-03T10:15:30.00Z")
        );
        projectRepository.save(project1);
        projectId = project1.getId();
    }

    @Test
    public void getProjects() throws Exception {
        this.mockMvc.perform(get("/api/v1/projects/" + projectId))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/api/v1/projects/3333"))
                .andExpect(status().isNotFound());

        this.mockMvc.perform(get("/api/v1/projects/Some ID"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createProject() throws Exception {
        var apiPath = String.format("/api/v1/projects/");
        var apiBody = """
                {
                    "name" : "Project 2",
                    "description: "NewProject",
                    "startDate": "2000-01-01T10:00:00.00Z",
                    "endDate": "2023-01-01T10:00:00.00Z"
                }
                """;

        var result = this.mockMvc.perform(
                post(apiPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(apiBody)
            )
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        var replyString = result.getResponse().getContentAsString();
        var responseDecode = mapper.readValue(replyString, ProjectContract.class);

        assertEquals("Project 2", responseDecode.name());
        assertEquals("NewProject", responseDecode.description());
        assertEquals(Instant.parse("2000-01-01T10:00:00.00Z"), responseDecode.startDate());
        assertEquals(Instant.parse("2023-01-01T10:00:00.00Z"), responseDecode.endDate());


    }

}
