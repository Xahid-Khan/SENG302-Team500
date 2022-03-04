package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<?> getAll() {
        try{
            ArrayList<ProjectContract> projects = projectService.allProjects();
            return ResponseEntity.ok(projects);
        }
        catch (NoSuchElementException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/{id}", produces = "application/jason")
    public ResponseEntity<ProjectContract> getById(@PathVariable long id) {
        try {
            var project = projectService.getById(id);
            return ResponseEntity.ok(project);
        }
        catch (NoSuchElementException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping(value = "/", produces = "application/jason")
    public ResponseEntity<?> addNewProject(@RequestBody ProjectContract newProject) {
        try {
            projectService.create(newProject);
            return ResponseEntity.ok(newProject);
        }
        catch (Exception error) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping(value = "/{id}", produces = "application/jason")
    public ResponseEntity<?> removeProject(@PathVariable long id) {
        try{
            projectService.delete(id);
            return ResponseEntity.ok("");
        }
        catch(NoSuchElementException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception error) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(value = "/", produces = "application/jason")
    public ResponseEntity<?> updateProject(@RequestBody ProjectContract updatedProject) {
        try {
            projectService.update(updatedProject);
            return ResponseEntity.ok("");
        }
        catch (NoSuchElementException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
