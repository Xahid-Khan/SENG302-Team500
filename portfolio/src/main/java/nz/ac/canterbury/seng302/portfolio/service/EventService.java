package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.EventMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseEventContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.EventContract;
import nz.ac.canterbury.seng302.portfolio.repository.EventRepository;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        eventRepository.delete(event);
        projectRepository.save(project);
    }
}
