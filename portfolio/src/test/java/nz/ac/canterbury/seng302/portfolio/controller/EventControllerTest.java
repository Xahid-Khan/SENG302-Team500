package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.portfolio.AuthorisationParamsHelper;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseEventContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.SprintEntity;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach() {
        sprintRepository.deleteAll();
        projectRepository.deleteAll();

        AuthorisationParamsHelper.setParams("role", "TEACHER");
    }
    @Before("Set up sprints")
    public void before() {
        //Create 3 sprint entities with the same project id
        var sprint1 = new SprintEntity("sprint 1", "test sprint", Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2022-12-02T10:15:30.00Z"));
        var sprint2 = new SprintEntity("sprint 2", "test sprint",Instant.parse("2022-12-03T10:15:30.00Z"), Instant.parse("2022-12-04T10:15:30.00Z"));
        var sprint3 = new SprintEntity("sprint 3", "test sprint", Instant.parse("2022-12-05T10:15:30.00Z"), Instant.parse("2022-12-06T10:15:30.00Z"));
                
    }
    @Test
    public void testCreateEvent() throws Exception {
        // Create a project
        var project = new ProjectEntity("testproject", null, Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2023-01-20T10:15:30.00Z"));
        projectRepository.save(project);
        // Create an event
        BaseEventContract event = new BaseEventContract("test event", "testdescription", Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2022-12-02T10:15:30.00Z"));
        eventService.createEvent(project.getId(), event);
//
//        // Check that the event was created
//        var events = eventRepository.findAll();
//        assert events.iterator().hasNext();
    }
}
