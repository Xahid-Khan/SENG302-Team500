package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.RolesService;
import nz.ac.canterbury.seng302.portfolio.service.ValidationService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;


/**
 * This project controller file allows users to make API calls, such as GET, POST, PUT, DELETE requests.
 */

@RestController
@RequestMapping("/api/v1")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private RolesService rolesService;


    private static final String TEACHER = "TEACHER";
    private static final String COURSE_ADMINISTRATOR = "COURSE_ADMINISTRATOR";

    /**
     * This method will be invoked when API receives a GET request, and will produce a list of all the projects.
     * @return List of projects converted into project contract (JSON) type.
     */
    @GetMapping(value = "/projects", produces = "application/json")
    public ResponseEntity<?> getAll() {
        try {
            ArrayList<ProjectContract> projects = projectService.allProjects();
            return ResponseEntity.ok(projects);
        } catch (NoSuchElementException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * This method will be invoked when API receives a GET request with a Project ID embedded in URL.
     * @param id Project ID the user wants to retrieve
     * @return a project contract (JSON) type of the project.
     */
    @GetMapping(value = "/projects/{id}", produces = "application/json")
    public ResponseEntity<ProjectContract> getById(@PathVariable String id) {
        try {
            var project = projectService.getById(id);
            return ResponseEntity.ok(project);
        }
        catch (NoSuchElementException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * This method will be invoked when API receives a POST request with data of new project embedded in body - (JSON type)
     * @param newProject data of new project
     * @return a project contract (JSON) type of the newly created project.
     */
    @PostMapping(value = "/projects", produces = "application/json")
    public ResponseEntity<?> addNewProject(@AuthenticationPrincipal PortfolioPrincipal principal, @RequestBody BaseProjectContract newProject) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains(TEACHER) || roles.contains(COURSE_ADMINISTRATOR)) {
            try {
                var errorMessage = validationService.checkAddProject(newProject);

                if (!errorMessage.equals("Okay")) {
                    if (errorMessage.equals("Project ID does not exist")) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                    }
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
                }
                var project = projectService.create(newProject);
                return ResponseEntity.ok(project);
            } catch (Exception error) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * This method will be invoked when API receives a DELETE request with a Project ID embedded in URL
     * @param id Project ID the user wants to delete
     * @return a project contract (JSON) type of the project.
     */
    @DeleteMapping(value = "/projects/{id}", produces = "application/json")
    public ResponseEntity<?> removeProject(@AuthenticationPrincipal PortfolioPrincipal principal, @PathVariable String id) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains(TEACHER) || roles.contains(COURSE_ADMINISTRATOR)) {
            try {
                projectService.delete(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } catch (NoSuchElementException error) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } catch (Exception error) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * This method will be invoked when API receives a UPDATE request with a Project ID embedded in URL
     * @param id Project ID the user wants to Update
     * @return a project contract (JSON) type of the project.
     */
    @PutMapping(value = "/projects/{id}", produces = "application/json")
    public ResponseEntity<?> updateProject(@AuthenticationPrincipal PortfolioPrincipal principal, @RequestBody ProjectContract updatedProject, @PathVariable String id) {
        ArrayList<String> roles = rolesService.getRolesByToken(principal);
        if (roles.contains(TEACHER) || roles.contains(COURSE_ADMINISTRATOR)) {
            try {
                var errorMessage = validationService.checkUpdateProject(id, updatedProject);

                if (!errorMessage.equals("Okay")) {
                    if (errorMessage.equals("Project ID does not exist")) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                    }
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
                }
                projectService.update(updatedProject, id);
                return ResponseEntity.ok("");
            } catch (NoSuchElementException error) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
