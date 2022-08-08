package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseSprintContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.SprintContract;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.RolesService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This sprint controller file allows users to make API calls, such as GET, POST, PUT, DELETE requests.
 *
 */

@RestController
@RequestMapping("/api/v1")
public class SprintController {
    @Autowired
    private SprintService sprintService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private RolesService rolesService;


    private static final String TEACHER = "TEACHER";
    private static final String COURSE_ADMINISTRATOR = "COURSE_ADMINISTRATOR";

    /**
     * This method will be invoked when API receives a GET request with a sprint ID embedded in URL.
     * @param sprintId sprint-ID the user wants to retrieve
     * @return a sprint contract (JSON) type of the sprint.
     */
    @GetMapping(value = "/sprints/{sprintId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SprintContract> getSprint(@PathVariable String sprintId) {
        try {
            var sprint = sprintService.get(sprintId);

            return ResponseEntity.ok(sprint);
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * This method will be invoked when API receives a GET request with a Project ID embedded in URL and
     * will produce all the sprints of that specific project.
     * @param projectId Project-ID of the project User is interested in
     * @return A list of sprints of a given project in Sprint Contract (JSON) type.
     */
    @GetMapping(value = "/projects/{projectId}/sprints", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SprintContract>> getProjectSprints(@PathVariable String projectId) {
        try {
            var result = projectService.getById(projectId).sprints();

            return ResponseEntity.ok(result);
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * This method will be invoked when API receives a POST request with a Project ID embedded in URL and
     * data for the sprint in the body.
     * @param projectId Project-ID of the project User wants the sprint to be added to.
     * @return A list of sprints of a given project in Sprint Contract (JSON) type.
     */
    @PostMapping(value = "/projects/{projectId}/sprints", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createSprint(
        @AuthenticationPrincipal PortfolioPrincipal principal,
        @PathVariable String projectId,
        @RequestBody BaseSprintContract sprint
    ) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);

        if (roles.contains(TEACHER) || roles.contains(COURSE_ADMINISTRATOR)) {
            String errorMessage = validationService.checkAddSprint(projectId, sprint);
            if (!errorMessage.equals("Okay")) {
                if (errorMessage.equals("Project ID does not exist") || errorMessage.equals("Sprint ID does not exist")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

            try {
                var result = sprintService.create(projectId, sprint);

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
     * updated sprint data in the body.
     * @param id Project-ID of the project User wants to make the changes to.
     * @return The updated sprint value
     */
    @PutMapping(value = "/sprints/{id}")
    public ResponseEntity<?> updateSprint(
        @AuthenticationPrincipal PortfolioPrincipal principal,
        @PathVariable String id,
        @RequestBody BaseSprintContract sprint
    ) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains(TEACHER) || roles.contains(COURSE_ADMINISTRATOR)) {
            String errorMessage = validationService.checkUpdateSprint(id, sprint);
            if (!errorMessage.equals("Okay")) {
                if (errorMessage.equals("Project ID does not exist") || errorMessage.equals(
                    "Sprint ID does not exist")) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }

            try {
                sprintService.update(id, sprint);

                return ResponseEntity.ok(sprintService.get(id));
            } catch (NoSuchElementException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * This method will be invoked when API receives a DELETE request with a Sprint ID embedded in URL
     * @param id Sprint ID the user wants to delete
     * @return status_Code 204.
     */
    @DeleteMapping(value = "/sprints/{id}")
    public ResponseEntity<Void> deleteSprint(
        @AuthenticationPrincipal PortfolioPrincipal principal,
        @PathVariable String id
    ) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains(TEACHER) || roles.contains(COURSE_ADMINISTRATOR)) {
            try {
                sprintService.delete(id);

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } catch (NoSuchElementException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
