package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.NoSuchElementException;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseSprintContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.SprintContract;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class SprintController {
    @Autowired
    private SprintService sprintService;

    @GetMapping(value = "/sprints/{sprintId}", produces = "application/json")
    public ResponseEntity<SprintContract> getSprint(@PathVariable long sprintId) {
        try {
            var sprint = sprintService.get(sprintId);

            return ResponseEntity.ok(sprint);
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping(value = "/projects/{projectId}/sprints", produces = "application/json")
    public ResponseEntity<SprintContract> createSprint(@PathVariable long projectId, @RequestBody BaseSprintContract sprint) {
        try {
            var result = sprintService.create(projectId, sprint);

            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping(value = "/sprints/{id}", produces = "application/json")
    public ResponseEntity<SprintContract> updateSprint(@PathVariable long id, @RequestBody BaseSprintContract sprint) {
        try {
            sprintService.update(id, sprint);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping(value = "/sprints/{id}", produces = "application/json")
    public ResponseEntity<SprintContract> updateSprint(@PathVariable long id) {
        try {
            sprintService.delete(id);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
