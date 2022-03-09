package nz.ac.canterbury.seng302.portfolio.service;

import com.google.type.DateTime;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseSprintContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.SprintContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.NoSuchElementException;


@Service
public class ValidationService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SprintService sprintService;

    public String checkAddProject(BaseProjectContract projectContract) {

        return checkBaseFields("Project",
                projectContract.name(),
                projectContract.description(),
                projectContract.startDate(),
                projectContract.endDate());
    }

    public String checkBaseFields(String type, String name, String desc, Instant start, Instant end) {
        if (name.equals("")) {
            return type + " must have a name";
        }

        if (!name.matches("[A-Za-z0-9 _ -]*")) {
            return type + " name cannot contain any special characters";
        }

        if (end.isBefore(start)) {
            return type + " start date must be earlier than the end date";
        }

        if (start.isBefore(Instant.parse(LocalDate.now().minusYears(1).atStartOfDay().toString() + ":00.00Z"))) {
            return type + " cannot start more than one year ago from today";
        }
        return "Okay";
    }

    public String checkUpdateProject(String projectId, ProjectContract projectContract) {

        try {
            projectService.getById(projectId);
        } catch (NoSuchElementException error) {
            return "Project ID does not exist";
        }

        return checkBaseFields("Project",
                projectContract.name(),
                projectContract.description(),
                projectContract.startDate(),
                projectContract.endDate());
    }

    public String checkAddSprint(String projectId, BaseSprintContract sprintContract) {
        try {
            ProjectContract project = projectService.getById(projectId);
            checkSprintDetails(project, sprintContract.startDate(), sprintContract.endDate());
        } catch (NoSuchElementException error) {
                return "Project ID does not exist";
        }
        return checkBaseFields("Sprint",
                sprintContract.name(),
                sprintContract.description(),
                sprintContract.startDate(),
                sprintContract.endDate());
    }

    public String checkUpdateSprint(String sprintId, SprintContract sprintContract) {

        try {
            SprintContract sprint = sprintService.get(sprintId);
            try {
                ProjectContract project = projectService.getById(sprintContract.projectId());
                checkSprintDetails(project, sprint.startDate(), sprint.endDate());

                if (!sprintId.equals(sprintContract.sprintId())) {
                    return "Given path ID and sprint contract ID are not the same";
                }
            } catch (NoSuchElementException error) {
                return "Project ID does not exist";
            }


        } catch (NoSuchElementException error) {

            return "Sprint ID does not exist";
        }


        return checkBaseFields("Sprint",
                sprintContract.name(),
                sprintContract.description(),
                sprintContract.startDate(),
                sprintContract.endDate());
    }

    public String checkSprintDetails(ProjectContract project, Instant start, Instant end) {

        if (start.isBefore(project.startDate())) {
            return "Sprint cannot start before project start date";
        }
        if (end.isAfter(project.endDate())) {
            return "Sprint cannot end after project end date";
        }
        int numSprints = project.sprints().size();
        if (numSprints >= 1 && !start.isAfter(project.sprints().get(numSprints - 1).endDate())) {
            return "Sprint cannot begin while another sprint is still in progress";
        }
        return "Okay";

    }
}

