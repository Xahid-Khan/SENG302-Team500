package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.EventMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseEventContract;
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

//    /**
//     * Goes through all the sprints in the project that have overlapping dates with the event
//     * @param project Project to search
//     * @param event Event whose dates you are checking
//     * @return List of sprint IDs that overlap
//     */
//    public ArrayList<String> getSprintForEvent(ProjectEntity project, BaseEventContract event) {
//        var sprints = project.getSprints();
//        var eventEntity = eventMapper.toEntity(event);
//        var sprintIds = new ArrayList<String>();
//
//        //iterates through all the sprints in the project using i
//        for (var i = 0; i < sprints.size(); i++) {
//            //Gets sprint
//            var sprint = sprints.get(i);
//            var sprintEntity = sprintRepository.findById(sprint.getId()).orElseThrow(() -> new NoSuchElementException("Invalid sprint ID"));
//
//            //1 Checks if event start date is before or the same as the sprint start date and the event end date is after or the same as the sprint end date
////            if (eventEntity.getStartDate().isBefore(sprintEntity.getStartDate()) || eventEntity.getStartDate().equals(sprintEntity.getStartDate())
////                    && eventEntity.getEndDate().isAfter(sprintEntity.getEndDate()) || eventEntity.getEndDate().equals(sprintEntity.getEndDate())) {
////                sprintIds.add(sprint.getId());
////            }
//            eventEntity.getStartDate().compareTo(sprintEntity.getStartDate());
//            // Adds if event starts before the sprint starts and the event doesn't end before the sprint starts
//            if (eventEntity.getStartDate().compareTo(sprintEntity.getStartDate())<=0 && eventEntity.getEndDate().compareTo(sprintEntity.getStartDate()) >= 0) {
//                sprintIds.add(sprint.getId());
//                System.out.println("Event starts before sprint starts");
//            }
//            // Adds if event starts after the sprint starts but before the sprint ends
//            else if (eventEntity.getStartDate().compareTo(sprintEntity.getStartDate()) >= 0 && eventEntity.getStartDate().compareTo(sprintEntity.getEndDate()) <= 0) {
//                sprintIds.add(sprint.getId());
//            }
//
//        }
//        return sprintIds;
//    }
}