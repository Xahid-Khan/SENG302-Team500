package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.contract.BaseProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseSprintContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.SprintContract;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

public class ValidationService {

    @Autowired
    private ProjectService projectService;

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

        if (!name.matches("[A-Za-z0-9 _]*")) {
            return type + " name cannot contain any special characters";
        }

        if (end.isBefore(start)) {
            return type + " start date must be earlier than the end date";
        }

        if (end.isAfter(Instant.now().minus(1, ChronoUnit.YEARS))) {
            return type +" cannot start more than one year ago from now";
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

            if (sprintContract.startDate().isBefore(project.startDate())) {
                return "Sprint starts before project start date";
            }
            if (sprintContract.endDate().isAfter(project.endDate())) {
                return "Sprint ends after project end date";
            }
            int numSprints = project.sprints().size();
            if (numSprints >= 1 && !sprintContract.startDate().isAfter(project.sprints().get(numSprints - 1).endDate())) {
                return "Sprint cannot begin while another sprint is still in progress";
            }
        } catch (NoSuchElementException error) {
            return "Project ID does not exist";
        }

        return checkBaseFields("Sprint",
                sprintContract.name(),
                sprintContract.description(),
                sprintContract.startDate(),
                sprintContract.endDate());
    }

    public String checkUpdateSprint(String stringId, SprintContract sprintContract) {

        if (!stringId.equals(sprintContract.sprintId())) {
            return "Given path ID and sprint contract ID are not the same";
        }

        try {
            ProjectContract project = projectService.getById(stringId);
            if (sprintContract.startDate().isBefore(project.startDate())) {
                return "Sprint starts before project start date";
            }
            if (sprintContract.endDate().isAfter(project.endDate())) {
                return "Sprint ends after project end date";
            }
            int numSprints = project.sprints().size();
            if (numSprints >= 1 && !sprintContract.startDate().isAfter(project.sprints().get(numSprints - 1).endDate())) {
                return "Sprint cannot begin while another sprint is still in progress";
            }

        } catch (NoSuchElementException error) {
            return "Project ID does not exist";
        }

        try {
            projectService.getById(sprintContract.projectId());
        } catch (NoSuchElementException error) {
            return "Project ID does not exist";
        }

        return checkBaseFields("Sprint",
                sprintContract.name(),
                sprintContract.description(),
                sprintContract.startDate(),
                sprintContract.endDate());
    }

}

