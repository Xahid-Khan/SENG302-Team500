package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.contract.BaseDeadlineContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.DeadlineContract;
import nz.ac.canterbury.seng302.portfolio.service.DeadlineService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.RolesService;
import nz.ac.canterbury.seng302.portfolio.service.ValidationService;
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
public class DeadlineController {

    @Autowired
    private DeadlineService deadlineService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private RolesService rolesService;

    /**
     * This method will be invoked when API receives a GET request with a deadline ID embedded in URL.
     * @param deadlineId deadline-ID the user wants to retrieve
     * @return a deadline contract (JSON) type of the deadline.
     */
    @GetMapping(value = "/deadlines/{deadlineId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DeadlineContract> getDeadline(@PathVariable String deadlineId) {
        try {
            var deadline = deadlineService.get(deadlineId);

            return ResponseEntity.ok(deadline);
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * This method will be invoked when API receives a GET request with a Project ID embedded in URL and
     * will produce all the deadlines of that specific project.
     * @param projectId Project-ID of the project User is interested in
     * @return A list of deadlines of a given project in deadline Contract (JSON) type.
     */
    @GetMapping(value = "/projects/{projectId}/deadlines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DeadlineContract>> getProjectDeadlines(@PathVariable String projectId) {
        try {
            var result = projectService.getById(projectId).deadlines();

            return ResponseEntity.ok(result);
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * This method will be invoked when API receives a POST request with a Project ID embedded in URL and
     * data for the deadline in the body.
     * @param projectId Project-ID of the project User wants the deadline to be added to.
     * @return A list of deadlines of a given project in deadline Contract (JSON) type.
     */
    @PostMapping(value = "/projects/{projectId}/deadlines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createDeadline(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable String projectId,
            @RequestBody BaseDeadlineContract deadline
    ) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains("TEACHER") || roles.contains("COORDINATOR")) {
            String errorMessage = validationService.checkAddDeadline(projectId, deadline);
            if (!errorMessage.equals("Okay")) {
                if (errorMessage.equals("Project ID does not exist") || errorMessage.equals("Deadline ID does not exist")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

            try {
                var result = deadlineService.createDeadline(projectId, deadline);

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
     * updated deadline data in the body.
     * @param id Project-ID of the project User wants to make the changes to.
     * @return The updated deadline value
     */
    @PutMapping(value = "/deadlines/{id}")
    public ResponseEntity<?> updateDeadline(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable String id,
            @RequestBody BaseDeadlineContract deadline
    ) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains("TEACHER") || roles.contains("COORDINATOR")) {
            String errorMessage = validationService.checkUpdateDeadline(id, deadline);
            if (!errorMessage.equals("Okay")) {
                if (errorMessage.equals("Project ID does not exist") || errorMessage.equals(
                        "Deadline ID does not exist")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

            try {
                deadlineService.update(id, deadline);

                return ResponseEntity.ok(deadlineService.get(id));
            } catch (NoSuchElementException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * This method will be invoked when API receives a DELETE request with a deadline ID embedded in URL
     * @param id Deadline ID the user wants to delete
     * @return status_Code 204.
     */
    @DeleteMapping(value = "/deadlines/{id}")
    public ResponseEntity<Void> deleteDeadline(
            @AuthenticationPrincipal AuthState principal,
            @PathVariable String id
    ) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains("TEACHER") || roles.contains("COORDINATOR")) {
            try {
                deadlineService.delete(id);

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } catch (NoSuchElementException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


}
