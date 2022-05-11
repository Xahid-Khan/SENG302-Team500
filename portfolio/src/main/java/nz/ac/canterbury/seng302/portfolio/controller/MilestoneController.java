package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.contract.BaseEventContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseMilestoneContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.EventContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.MilestoneContract;
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
public class MilestoneController {

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private RolesService rolesService;

    /**
     * This method will be invoked when API receives a GET request with a milestone ID embedded in URL.
     * @param milestoneId milestone-ID the user wants to retrieve
     * @return a event contract (JSON) type of the milestone.
     */
    @GetMapping(value = "/milestones/{milestoneId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MilestoneContract> getMilestone(@PathVariable String milestoneId) {
        try {
            var milestone = milestoneService.get(milestoneId);

            return ResponseEntity.ok(milestone);
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * This method will be invoked when API receives a GET request with a Project ID embedded in URL and
     * will produce all the milestones of that specific project.
     * @param projectId Project-ID of the project User is interested in
     * @return A list of milestones of a given project in milestone Contract (JSON) type.
     */
    @GetMapping(value = "/projects/{projectId}/milestones", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MilestoneContract>> getProjectMilestones(@PathVariable String projectId) {
        try {
            var result = projectService.getById(projectId).milestones();

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
    @PostMapping(value = "/projects/{projectId}/milestones", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMilestone(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable String projectId,
            @RequestBody BaseMilestoneContract milestone
    ) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains("TEACHER") || roles.contains("COORDINATOR")) {
            String errorMessage = validationService.checkAddMilestone(projectId, milestone);
            if (!errorMessage.equals("Okay")) {
                if (errorMessage.equals("Project ID does not exist") || errorMessage.equals("Event ID does not exist")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

            try {
                var result = milestoneService.createMilestone(projectId, milestone);

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
    @PutMapping(value = "/milestones/{id}")
    public ResponseEntity<?> updateMilestone(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable String id,
            @RequestBody BaseMilestoneContract milestone
    ) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains("TEACHER") || roles.contains("COORDINATOR")) {
            String errorMessage = validationService.checkUpdateMilestone(id, milestone);
            if (!errorMessage.equals("Okay")) {
                if (errorMessage.equals("Project ID does not exist") || errorMessage.equals(
                        "Event ID does not exist")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

            try {
                milestoneService.update(id, milestone);

                return ResponseEntity.ok(milestoneService.get(id));
            } catch (NoSuchElementException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * This method will be invoked when API receives a DELETE request with a Event ID embedded in URL
     * @param id Milestone ID the user wants to delete
     * @return status_Code 204.
     */
    @DeleteMapping(value = "/milestones/{id}")
    public ResponseEntity<Void> deleteMilestone(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable String id
    ) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains("TEACHER") || roles.contains("COORDINATOR")) {
            try {
                milestoneService.delete(id);

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } catch (NoSuchElementException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


}
