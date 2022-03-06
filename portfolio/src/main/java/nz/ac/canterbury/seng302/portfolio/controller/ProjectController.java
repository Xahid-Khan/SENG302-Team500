package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.contract.BaseProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import org.h2.util.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @GetMapping(value = "/projects", produces = "application/json")
    public ResponseEntity<?> getAll() {
        try{
            ArrayList<ProjectContract> projects = projectService.allProjects();
            return ResponseEntity.ok(projects);
        }
        catch (NoSuchElementException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

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

    @PostMapping(value = "/projects", produces = "application/json")
    public ResponseEntity<ProjectContract> addNewProject(@RequestBody BaseProjectContract newProject) {
        try {
            var project = projectService.create(newProject);
            return ResponseEntity.ok(project);
        }
        catch (Exception error) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping(value = "/projects/{id}", produces = "application/json")
    public ResponseEntity<?> removeProject(@PathVariable String id) {
        try{
            projectService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        catch(NoSuchElementException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        catch (Exception error) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping(value = "/projects/{id}", produces = "application/json")
    public ResponseEntity<?> updateProject(@RequestBody ProjectContract updatedProject, @PathVariable String id) {
        try {
            projectService.update(updatedProject, id);
            return ResponseEntity.ok("");
        }
        catch (NoSuchElementException error) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
