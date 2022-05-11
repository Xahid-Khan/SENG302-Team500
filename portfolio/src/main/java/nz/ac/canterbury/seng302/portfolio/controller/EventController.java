package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.contract.BaseEventContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.EventContract;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private RolesService rolesService;

    /**
     * This method will be invoked when API receives a GET request with a event ID embedded in URL.
     * @param eventId event-ID the user wants to retrieve
     * @return a event contract (JSON) type of the event.
     */
    @GetMapping(value = "/events/{eventId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventContract> getEvent(@PathVariable String eventId) {
        try {
            var event = eventService.get(eventId);

            return ResponseEntity.ok(event);
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * This method will be invoked when API receives a GET request with a Project ID embedded in URL and
     * will produce all the events of that specific project.
     * @param projectId Project-ID of the project User is interested in
     * @return A list of events of a given project in Event Contract (JSON) type.
     */
    @GetMapping(value = "/projects/{projectId}/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EventContract>> getProjectEvents(@PathVariable String projectId) {
        try {
            var result = projectService.getById(projectId).events();

            return ResponseEntity.ok(result);
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * This method will be invoked when API receives a POST request with a Project ID embedded in URL and
     * data for the event in the body.
     * @param projectId Project-ID of the project User wants the event to be added to.
     * @return A list of events of a given project in Event Contract (JSON) type.
     */
    @PostMapping(value = "/projects/{projectId}/events", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createEvent(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable String projectId,
            @RequestBody BaseEventContract event
    ) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains("TEACHER") || roles.contains("COORDINATOR")) {
            String errorMessage = validationService.checkAddEvent(projectId, event);
            if (!errorMessage.equals("Okay")) {
                if (errorMessage.equals("Project ID does not exist") || errorMessage.equals("Event ID does not exist")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

            try {
                var result = eventService.createEvent(projectId, event);

                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            }
            catch (NoSuchElementException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * This method will be invoked when API receives a PUT request with a Project ID embedded in URL and
     * updated event data in the body.
     * @param id Project-ID of the project User wants to make the changes to.
     * @return The updated event value
     */
    @PutMapping(value = "/events/{id}")
    public ResponseEntity<?> updateEvent(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable String id,
            @RequestBody BaseEventContract event
    ) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains("TEACHER") || roles.contains("COORDINATOR")) {
            String errorMessage = validationService.checkUpdateEvent(id, event);
            if (!errorMessage.equals("Okay")) {
                if (errorMessage.equals("Project ID does not exist") || errorMessage.equals(
                        "Event ID does not exist")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

            try {
                eventService.update(id, event);

                return ResponseEntity.ok(eventService.get(id));
            } catch (NoSuchElementException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * This method will be invoked when API receives a DELETE request with a Event ID embedded in URL
     * @param id Event ID the user wants to delete
     * @return status_Code 204.
     */
    @DeleteMapping(value = "/events/{id}")
    public ResponseEntity<Void> deleteEvent(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable String id
    ) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains("TEACHER") || roles.contains("COORDINATOR")) {
            try {
                eventService.delete(id);

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } catch (NoSuchElementException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


}
