package nz.ac.canterbury.seng302.portfolio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.portfolio.AuthorisationParamsHelper;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseEventContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseSprintContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.EventContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.SprintEntity;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.service.EventService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    private SprintService sprintService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach() {
        eventRepository.deleteAll();
        sprintRepository.deleteAll();
        projectRepository.deleteAll();


        AuthorisationParamsHelper.setParams("role", "TEACHER");
    }

    /**
     * Tests creating an event
     * @throws Exception
     */
    @Test
    public void testCreateEvent() throws Exception {
        // Create a project
        var project = new ProjectEntity("testproject", null, Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2023-01-20T10:15:30.00Z"));
        projectRepository.save(project);
        // Create an event
        BaseEventContract event = new BaseEventContract("test event", "testdescription", Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2022-12-02T10:15:30.00Z"));
        EventContract eventContract = eventService.createEvent(project.getId(), event);

        // Check that the event was created
        var events = eventRepository.findAll();
        assert events.iterator().hasNext();

        //Checks the event name
        var event1 = events.iterator().next();
        assertEquals("test event", event1.getName());

        //Tests event was added to project
        var projects = projectRepository.findAll();
        var project1 = projects.iterator().next();
        assertEquals(event1.getId(),project1.getEvents().get(0).getId());
    }

    public ProjectEntity setupThreeSprints() {
        //Create 3 sprint entities with the same project id
        BaseSprintContract sprint1 = new BaseSprintContract("January", "test sprint", Instant.parse("2023-01-01T10:15:30.00Z"), Instant.parse("2023-01-30T10:15:30.00Z"));
        BaseSprintContract sprint2 = new BaseSprintContract("March", "test sprint", Instant.parse("2023-03-01T10:15:30.00Z"), Instant.parse("2023-03-30T10:15:30.00Z"));
        BaseSprintContract sprint3 = new BaseSprintContract("May", "test sprint", Instant.parse("2023-05-01T10:15:30.00Z"), Instant.parse("2023-05-30T10:15:30.00Z"));
        // Create a project and adds the sprints to it
        ProjectEntity project = new ProjectEntity("testproject", null, Instant.parse("2022-12-01T10:15:30.00Z"), Instant.parse("2024-01-20T10:15:30.00Z"));
        projectRepository.save(project);
        sprintService.create(project.getId(), sprint1);
        sprintService.create(project.getId(), sprint2);
        sprintService.create(project.getId(), sprint3);

        return project;
    }
    /**
     * Creates an event that starts when the sprint starts and ends when the sprint ends
     * @throws Exception
     */
    @Test
     public void testCreateEventStartSameAsSprintAndEventEndSameAsSprint() throws Exception {
        //Sets up sprints and project
         ProjectEntity project = setupThreeSprints();
         //Creates event
         BaseEventContract event = new BaseEventContract("January", "test", Instant.parse("2023-01-01T10:15:30.00Z"), Instant.parse("2023-01-30T10:15:30.00Z"));
        EventContract eventContract = eventService.createEvent(project.getId(), event);

         // Get all the sprints
         var sprints = sprintRepository.findAll();



         for (SprintEntity sprint : sprints) {
             if(sprint.getName().equals("January")) {
                 // Check that the event was added to the january sprint
                 assertEquals(eventContract.sprintId(), sprint.getId());
                 assertEquals(eventContract.eventId(), sprint.getEvents().get(0).getId());
             }else{
                 // Check that it was not added to the other sprints
                 assertEquals(0, sprint.getEvents().size());
             }
         }

    }

    /**
     * Creates an event that starts after the sprint starts and ends before the sprint ends
     * @throws Exception
     *
     */
    @Test
    public void testCreateEventStartAfterSprintStartAndEventEndBeforeSprintEnd() throws Exception {
        //Sets up sprints and project
        ProjectEntity project = setupThreeSprints();
        //Creates event
        BaseEventContract event = new BaseEventContract("January", "test", Instant.parse("2023-01-03T10:15:30.00Z"), Instant.parse("2023-01-05T10:15:30.00Z"));
        EventContract eventContract = eventService.createEvent(project.getId(), event);

        // Get all the sprints
        var sprints = sprintRepository.findAll();



        for (SprintEntity sprint : sprints) {
            if(sprint.getName().equals("January")) {
                // Check that the event was added to the january sprint
                assertEquals(eventContract.sprintId(), sprint.getId());
                assertEquals(eventContract.eventId(), sprint.getEvents().get(0).getId());
            }else{
                // Check that it was not added to the other sprints
                assertEquals(0, sprint.getEvents().size());
            }
        }
    }
    /**
     * Tests creating an event that starts before the sprint starts and ends after the sprint ends
     */
    @Test
    public void testCreateEventStartBeforeSprintStartAndEventEndAfterSprintEnd() throws Exception {
        //Sets up sprints and project
        ProjectEntity project = setupThreeSprints();
        //Creates event
        BaseEventContract event = new BaseEventContract("January", "test", Instant.parse("2022-12-05T10:15:30.00Z"), Instant.parse("2023-02-25T10:15:30.00Z"));
        EventContract eventContract = eventService.createEvent(project.getId(), event);

        // Get all the sprints
        var sprints = sprintRepository.findAll();



        for (SprintEntity sprint : sprints) {
            if(sprint.getName().equals("January")) {
                // Check that the event was added to the january sprint
                assertEquals(eventContract.sprintId(), sprint.getId());
                assertEquals(eventContract.eventId(), sprint.getEvents().get(0).getId());
            }else{
                // Check that it was not added to the other sprints
                assertEquals(0, sprint.getEvents().size());
            }
        }

    }
    /**
     * Tests creating an event that spans multiple sprints
     * TODO: GET THIS TO WORK
     */
    @Test
    public void testCreateEventSpanningMultipleSprints() throws Exception {
        //Sets up sprints and project
        ProjectEntity project = setupThreeSprints();
        //Creates event
        BaseEventContract event = new BaseEventContract("January", "test", Instant.parse("2023-01-01T10:15:30.00Z"), Instant.parse("2023-04-30T10:15:30.00Z"));
        EventContract eventContract = eventService.createEvent(project.getId(), event);

        // Get all the sprints
        var sprints = sprintRepository.findAll();



        for (SprintEntity sprint : sprints) {
            if(sprint.getName().equals("January")) {
                // Check that the event was added to the january sprint
//                assertEquals(eventContract.sprintId(), sprint.getId());
                assertEquals(eventContract.eventId(), sprint.getEvents().get(0).getId());
            }else{
                // Check that it was not added to the other sprints
                assertEquals(0, sprint.getEvents().size());
            }
        }


    }
    /**
     * Tests creating an event that is not in any sprints
     * @throws Exception
     */
    @Test
     public void testCreateEventNotInSprint() throws Exception {
         //Sets up sprints and project
         ProjectEntity project = setupThreeSprints();
         //Creates event
         BaseEventContract event = new BaseEventContract("February", "test", Instant.parse("2023-02-01T10:15:30.00Z"), Instant.parse("2023-02-03T10:15:30.00Z"));
        EventContract eventContract = eventService.createEvent(project.getId(), event);

         // Get all the sprints
         var sprints = sprintRepository.findAll();

         for (SprintEntity sprint : sprints) {
             assertEquals(0, sprint.getEvents().size());
         }
     }

    @Test
    public void testUpdateEventToNewSprint() {
        //Sets up sprints and project
        ProjectEntity project = setupThreeSprints();
        //Creates event
        BaseEventContract event = new BaseEventContract("January", "test", Instant.parse("2023-01-01T10:15:30.00Z"), Instant.parse("2023-01-30T10:15:30.00Z"));
        EventContract eventContract = eventService.createEvent(project.getId(), event);

        // Tests that event is in january sprint
        var sprints = sprintRepository.findAll();
        for (SprintEntity sprint : sprints) {
            if(sprint.getName().equals("January")) {
                // Check that the event was added to the january sprint
                assertEquals(eventContract.sprintId(), sprint.getId());
                assertEquals(eventContract.eventId(), sprint.getEvents().get(0).getId());
            }else{
                // Check that it was not added to the other sprints
                assertEquals(0, sprint.getEvents().size());
            }
        }

        // Updates event to new sprint
        BaseEventContract newEvent = new BaseEventContract("March", "test sprint", Instant.parse("2023-03-01T10:15:30.00Z"), Instant.parse("2023-03-30T10:15:30.00Z"));
        eventService.update(eventContract.eventId(), newEvent);

        // Tests that event is in March sprint
         for (SprintEntity sprint : sprints) {
            if(sprint.getName().equals("March")) {
                // Check that the event was added to the january sprint
                assertEquals(eventContract.sprintId(), sprint.getId());
                assertEquals(eventContract.eventId(), sprint.getEvents().get(0).getId());
            }else{
                // Check that it was not added to the other sprints
                System.out.println(sprint.getName());
                System.out.println(sprint.getEvents().size());
                assertEquals(0, sprint.getEvents().size());
            }
        }

    }
    @Test
    public void testDeleteEvent() {
        //Sets up sprints and project
        ProjectEntity project = setupThreeSprints();
        //Creates event
        BaseEventContract event = new BaseEventContract("January", "test", Instant.parse("2023-01-01T10:15:30.00Z"), Instant.parse("2023-01-30T10:15:30.00Z"));
        EventContract eventContract = eventService.createEvent(project.getId(), event);

        // Get all the sprints
        var sprints = sprintRepository.findAll();
        for (SprintEntity sprint : sprints) {
            if(sprint.getName().equals("January")) {
                // Check that the event was added to the january sprint
                assertEquals(eventContract.sprintId(), sprint.getId());
                assertEquals(eventContract.eventId(), sprint.getEvents().get(0).getId());
            }else{
                // Check that it was not added to the other sprints
                assertEquals(0, sprint.getEvents().size());
            }
        }

        // Delete the event
        eventService.delete(project.getEvents().get(0).getId());
        for (SprintEntity sprint : sprints) {
            assertEquals(0, sprint.getEvents().size());
        }

    }

     //TODO TEST NO SPINTS


}
