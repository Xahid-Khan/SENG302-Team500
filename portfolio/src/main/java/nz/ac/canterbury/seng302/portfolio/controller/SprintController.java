package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.NoSuchElementException;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sprint")
public class SprintController {
    @Autowired
    private SprintService sprintService;

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getSprint(@PathVariable long id) {
        try {
            var sprint = sprintService.get(id);
            return ResponseEntity.ok(sprint);
        }
        catch (NoSuchElementException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
