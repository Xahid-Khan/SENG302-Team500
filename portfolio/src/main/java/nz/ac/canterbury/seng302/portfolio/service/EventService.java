package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.EventMapper;
import nz.ac.canterbury.seng302.portfolio.mapping.SprintMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseEventContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseSprintContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.EventContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.SprintEntity;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@Service
public class EventService {
    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private SprintMapper sprintMapper;

    /**
     * Get the event with the event ID
     *
     * @param eventId The event ID
     * @throws IllegalArgumentException If the event ID is invalid
     * @return The event contract with the event ID
     */
    public EventContract getEvent(String eventId) {
        var event= eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Invalid event ID"));
        return eventMapper.toContract(event);
    }

    /**
     * Creates an event within a project and puts it in a sprint
     * if it falls within the sprint's start and end dates
     * TODO: When sprint date changes, event should be checked again.
     * TODO Implement putting into sprint by date
     *
     * @return
     */
    public EventContract createEvent(String projectId, BaseEventContract event) {
        var project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));
        var entity = eventMapper.toEntity(event);

        project.newEvent(entity);
        projectRepository.save(project);
        eventRepository.save(entity);

        return eventMapper.toContract(entity);
    }


    /**
     * Deletes an event, including removing it from its parent project.
     *
     * @param eventId to delete
     * @throws NoSuchElementException if the id is invalid
     */
    public void delete(String eventId) {
        var event = eventRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException("Invalid event ID"));
        var project = event.getProject();


        project.removeEvent(event);
        projectRepository.save(project);
        eventRepository.deleteById(eventId);
    }

    /**
     * Updates an event using the EventContract provided
     * @param eventId to update
     * @param event to update, with the new values
     */
    public void update(String eventId, BaseEventContract event) {
        var eventEntity = eventRepository.findById(eventId).orElseThrow(() -> new NoSuchElementException("Invalid event ID"));
        var project = eventEntity.getProject();
        eventEntity.setName((event.name()));
        eventEntity.setDescription(event.description());


        eventRepository.save(eventEntity);
    }

    /**
     * Goes through all the sprints in the project and returns the ids of the ones whose dates overlap with the event provided
     * @param project Project to search
     * @param event Event whose dates you are checking
     * @return List of sprint IDs that overlap
     */
    public ArrayList<String> getSprintForEvent(ProjectEntity project, BaseEventContract event) {
        var sprints = project.getSprints();
        var eventEntity = eventMapper.toEntity(event);
        var sprintIds = new ArrayList<String>();

        //iterates through all the sprints in the project using i
        for (var i = 0; i < sprints.size(); i++) {
            //Gets sprint
            var sprint = sprints.get(i);
            var sprintEntity = sprintRepository.findById(sprint.getId()).orElseThrow(() -> new NoSuchElementException("Invalid sprint ID"));
            eventEntity.getStartDate().compareTo(sprintEntity.getStartDate());
            //The two if statements below cover all scenarios where an event will fall in a sprint
            // Adds if event starts before the sprint starts and the event DOES NOT END before the sprint starts. Will add any sprints that overlap but none that end before
            if (eventEntity.getStartDate().compareTo(sprintEntity.getStartDate())<=0 && eventEntity.getEndDate().compareTo(sprintEntity.getStartDate()) >= 0) {
                sprintIds.add(sprint.getId());
                System.out.println("Event starts before sprint starts");
            }
            // Adds if event starts after the sprint starts but before the sprint ends. This will add any events that overlap the sprint
            else if (eventEntity.getStartDate().compareTo(sprintEntity.getStartDate()) >= 0 && eventEntity.getStartDate().compareTo(sprintEntity.getEndDate()) <= 0) {
                sprintIds.add(sprint.getId());
            }

        }
        return sprintIds;
    }
    /**
     * Goes through all the events in the project and returns the ids of the ones whose dates overlap with the sprint provided
     * @param project Project to search
     * @param sprint Sprint whose dates you are checking
     * @return List of event IDs that overlap
     */
    public ArrayList<String> getEventsInSprint(ProjectEntity project, BaseSprintContract sprint) {
        var events = project.getEvents();
        var sprintEntity = sprintMapper.toEntity(sprint);
        var eventIds = new ArrayList<String>();

        //iterates through all the sprints in the project using i
        for (var i = 0; i < events.size(); i++) {
            //Gets sprint
            var event = events.get(i);
            var eventEntity = eventRepository.findById(event.getId()).orElseThrow(() -> new NoSuchElementException("Invalid sprint ID"));
            sprintEntity.getStartDate().compareTo(eventEntity.getStartDate());
            //The two if statements below cover all scenarios where an event will fall in a sprint
            // Adds if event starts before the sprint starts and the event DOES NOT END before the sprint starts. Will add any sprints that overlap but none that end before
            if (sprintEntity.getStartDate().compareTo(eventEntity.getStartDate())<=0 && sprintEntity.getEndDate().compareTo(eventEntity.getStartDate()) >= 0) {
                eventIds.add(event.getId());
                System.out.println("Event starts before sprint starts");
            }
            // Adds if event starts after the sprint starts but before the sprint ends. This will add any events that overlap the sprint
            else if (sprintEntity.getStartDate().compareTo(eventEntity.getStartDate()) >= 0 && sprintEntity.getStartDate().compareTo(eventEntity.getEndDate()) <= 0) {
                eventIds.add(event.getId());
            }

        }
        return eventIds;
    }
}
