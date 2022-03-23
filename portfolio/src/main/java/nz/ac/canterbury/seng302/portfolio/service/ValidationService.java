package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.contract.BaseProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseSprintContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.SprintContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
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
                projectContract.startDate(),
                projectContract.endDate());
    }

    public String checkBaseFields(String type, String name, Instant start, Instant end) {
        if (name.trim().equals("")) {
            return type + " name must not be empty";
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
            ProjectContract project = projectService.getById(projectId);
            for (SprintContract sprint: project.sprints()) {
                if (projectContract.startDate().isAfter(sprint.startDate())) {
                    return "Project cannot begin after one of its sprints start date";
                }
                if ((projectContract.endDate().isBefore(sprint.endDate()))) {
                    return "Project cannot end before one of its sprints end date";
                }
            }
        } catch (NoSuchElementException error) {
            return "Project ID does not exist";
        }

        return checkBaseFields("Project",
                projectContract.name(),
                projectContract.startDate(),
                projectContract.endDate());
    }

    public String checkAddSprint(String projectId, BaseSprintContract sprintContract) {
        try {
            ProjectContract project = projectService.getById(projectId);
            String response = checkSprintDetails(project, "placeholderId", sprintContract.startDate(), sprintContract.endDate());
            if (!response.equals("Okay")) {
                return response;
            }
        } catch (NoSuchElementException error) {
                return "Project ID does not exist";
        }
        return checkBaseFields("Sprint",
                sprintContract.name(),
                sprintContract.startDate(),
                sprintContract.endDate());
    }

    public String checkUpdateSprint(String sprintId, BaseSprintContract sprintContract) {

        try {
            SprintContract sprint = sprintService.get(sprintId);
            try {
                ProjectContract project = projectService.getById(sprint.projectId());
                String response = checkSprintDetails(project, sprint.sprintId(), sprint.startDate(), sprint.endDate());
                if (!response.equals("Okay")) {
                    return response;
                }

            } catch (NoSuchElementException error) {
                return "Project ID does not exist";
            }


        } catch (NoSuchElementException error) {

            return "Sprint ID does not exist";
        }


        return checkBaseFields("Sprint",
                sprintContract.name(),
                sprintContract.startDate(),
                sprintContract.endDate());
    }

    public String checkSprintDetails(ProjectContract project, String sprintId, Instant start, Instant end) {


        if (start.isBefore(project.startDate())) {
            return "Sprint cannot start before project start date";
        }
        if (end.isAfter(project.endDate())) {
            return "Sprint cannot end after project end date";
        }
        for (SprintContract sprint: project.sprints()) {
            if (start.isBefore(sprint.endDate()) && end.isAfter(sprint.startDate()) && !sprintId.equals(sprint.sprintId())) {
                return "Sprint cannot begin while another sprint is still in progress";
            }
        }
        return "Okay";

    }
}

