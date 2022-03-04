package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Array;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ProjectControllerTest.class)
public class ProjectControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ProjectRepository projectRepository;

    ProjectContract project1 = new ProjectContract(
            "Project 1",
            "This is new First project",
            Instant.EPOCH,
            Instant.now()
            );

    ProjectContract project2 = new ProjectContract(
            "Project 2",
            "This is new Second project",
            Instant.EPOCH,
            Instant.now()
    );

    ProjectContract project3 = new ProjectContract(
            "Project 3",
            "This is new Third project",
            Instant.EPOCH,
            Instant.now()
    );

//
//    @Test
//    public void getAllRecords() throws Exception {
//        List<ProjectContract> projects = new ArrayList<ProjectContract>() {
//            Array.asList(
//            project1,
//            project2,
//            project3)
//        }
//    }



}
