package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.contract.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.SchemaOutputResolver;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.NoSuchElementException;


@Service
public class ValidationService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private EventService eventService;

    @Autowired
    private MilestoneService milestoneService;

    @Autowired
    private DeadlineService deadlineService;

    public String checkAddProject(BaseProjectContract projectContract) {

        return checkBaseFields("Project",
                projectContract.name(),
                projectContract.description(),
                projectContract.startDate(),
                projectContract.endDate());
    }

    /**
     * Checks the base input fields for the user.
     */
    public String checkBaseFields(String type, String name, String description, Instant start, Instant end) {
        if (name.equals("")) {
            return type + " name must not be empty";
        }
        if (name.trim().equals("")) {
            return type + " name must not contain only whitespaces";
        }

        if (name.length() > 32) {
            return type + " name must not be more than 32 characters";
        }

        if (description != null && description.length() > 1024) {
            return type + " description must not be more than 1024 characters";
        }

        if (end.isBefore(start)) {
            return type + " start date must be earlier than the end date";
        }

        if (start.isBefore(LocalDate.now().minusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant())) {
            return type + " cannot start more than one year ago from today"; // Needs to be added to frontend
        }
        return "Okay";
    }

    /**
     * Checks dates when a project has been updated.
     */
    public String checkUpdateProject(String projectId, ProjectContract projectContract) {

        try {
            ProjectContract project = projectService.getById(projectId);
            for (SprintContract sprint: project.sprints()) {
                if (projectContract.startDate().isAfter(sprint.startDate())) {
                    return "Project cannot begin after one of its sprints start date";
                }
                if (projectContract.endDate().isBefore(sprint.endDate())) {
                    return "Project cannot end before one of its sprints end date";
                }
            }
        } catch (NoSuchElementException error) {
            return "Project ID does not exist";
        }

        return checkBaseFields("Project",
                projectContract.name(),
                projectContract.description(),
                projectContract.startDate(),
                projectContract.endDate());
    }

    /**
     * Checks sprint inputs when a sprint is added.
     */
    public String checkAddSprint(String projectId, BaseSprintContract sprintContract) {
        try {
            ProjectContract project = projectService.getById(projectId);
            String response = checkSprintDetails(project, "placeholderId", sprintContract.startDate(), sprintContract.endDate(), sprintContract.colour());
            if (!response.equals("Okay")) {
                return response;
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

    /**
     * Checks event inputs when am event is added.
     */
    public String checkAddEvent(String projectId, BaseEventContract eventContract) {
        try {
            ProjectContract project = projectService.getById(projectId);
            String response = checkEventDetails(project, eventContract.startDate(), eventContract.endDate());
            if (!response.equals("Okay")) {
                return response;
            }
        } catch (NoSuchElementException error) {
            return "Project ID does not exist";
        }
        return checkBaseFields("Event",
                eventContract.name(),
                eventContract.description(),
                eventContract.startDate(),
                eventContract.endDate());
    }

    /**
     * Checks when a sprint has been updated.
     */
    public String checkUpdateSprint(String sprintId, BaseSprintContract sprintContract) {

        try {
            SprintContract sprint = sprintService.get(sprintId);
            try {
                ProjectContract project = projectService.getById(sprint.projectId());
                String response = checkSprintDetails(project, sprint.sprintId(), sprintContract.startDate(), sprintContract.endDate(), sprintContract.colour());
                if (!response.equals("Okay")) {
                    return response;
                }
                response = checkBaseFields("Sprint", sprintContract.name(), sprintContract.description(), sprintContract.startDate(), sprintContract.endDate());
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
                sprintContract.description(),
                sprintContract.startDate(),
                sprintContract.endDate());
    }

    /**
     * Checks when an event has been updated.
     */
    public String checkUpdateEvent(String eventId, BaseEventContract eventContract) {

        try {
            EventContract event = eventService.get(eventId);
            try {
                ProjectContract project = projectService.getById(event.projectId());
                String response = checkSprintDetails(project, event.eventId(), eventContract.startDate(), eventContract.endDate());
                if (!response.equals("Okay")) {
                    return response;
                }
                response = checkBaseFields("Event", eventContract.name(), eventContract.description(), eventContract.startDate(), eventContract.endDate());
                if (!response.equals("Okay")) {
                    return response;
                }

            } catch (NoSuchElementException error) {
                return "Project ID does not exist";
            }


        } catch (NoSuchElementException error) {

            return "Event ID does not exist";
        }


        return checkBaseFields("Event",
                eventContract.name(),
                eventContract.description(),
                eventContract.startDate(),
                eventContract.endDate());
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

    /**
     * Checks sprint date details and returns respective messages.
     */
    public String checkSprintDetails(ProjectContract project, String sprintId, Instant start, Instant end, String colour) {
        if (colour == null) {
            return "Sprint requires a colour";
        }
        else if (!colour.toUpperCase().matches("^#((\\d|[A-F]){6})$")) {
            return "Sprint colour must be in the format #RRGGBB";
        }
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

    /**
     * Checks event date details and returns respective messages.
     */
    public String checkEventDetails(ProjectContract project, Instant start, Instant end) {


        if (start.isBefore(project.startDate())) {
            return "Event cannot start before project start date";
        }
        if (end.isAfter(project.endDate())) {
            return "Event cannot end after project end date";
        }

        return "Okay";

    }

    /**
     * Checks milestone inputs when a milestone is added.
     */
    public String checkAddMilestone(String projectId, BaseMilestoneContract milestoneContract) {
        try {
            ProjectContract project = projectService.getById(projectId);
            String response = checkEventDetails(project, milestoneContract.startDate(), milestoneContract.endDate());
            if (!response.equals("Okay")) {
                return response;
            }
        } catch (NoSuchElementException error) {
            return "Project ID does not exist";
        }
        return checkBaseFields("Milestone",
                milestoneContract.name(),
                milestoneContract.description(),
                milestoneContract.startDate(),
                milestoneContract.endDate());
    }


    /**
     * Checks when a milestone has been updated.
     */
    public String checkUpdateMilestone(String milestoneId, BaseMilestoneContract milestoneContract) {

        try {
            MilestoneContract milestone = milestoneService.get(milestoneId);
            try {
                ProjectContract project = projectService.getById(milestone.projectId());
                String response = checkSprintDetails(project, milestone.milestoneId(), milestoneContract.startDate(), milestoneContract.endDate());
                if (!response.equals("Okay")) {
                    return response;
                }
                response = checkBaseFields("Milestone", milestoneContract.name(), milestoneContract.description(), milestoneContract.startDate(), milestoneContract.endDate());
                if (!response.equals("Okay")) {
                    return response;
                }

            } catch (NoSuchElementException error) {
                return "Project ID does not exist";
            }


        } catch (NoSuchElementException error) {

            return "Milestone ID does not exist";
        }


        return checkBaseFields("Milestone",
                milestoneContract.name(),
                milestoneContract.description(),
                milestoneContract.startDate(),
                milestoneContract.endDate());
    }

    /**
     * Checks deadline inputs when a milestone is added.
     */
    public String checkAddDeadline(String projectId, BaseDeadlineContract deadlineContract) {
        try {
            ProjectContract project = projectService.getById(projectId);
            String response = checkEventDetails(project, deadlineContract.startDate(), deadlineContract.endDate());
            if (!response.equals("Okay")) {
                return response;
            }
        } catch (NoSuchElementException error) {
            return "Project ID does not exist";
        }
        return checkBaseFields("Deadline",
                deadlineContract.name(),
                deadlineContract.description(),
                deadlineContract.startDate(),
                deadlineContract.endDate());
    }


    /**
     * Checks when a deadline has been updated.
     */
    public String checkUpdateDeadline(String deadlineId, BaseDeadlineContract deadlineContract) {

        try {
            DeadlineContract deadline = deadlineService.get(deadlineId);
            try {
                ProjectContract project = projectService.getById(deadline.projectId());
                String response = checkSprintDetails(project, deadline.deadlineId(), deadlineContract.startDate(), deadlineContract.endDate());
                if (!response.equals("Okay")) {
                    return response;
                }
                response = checkBaseFields("Deadline", deadlineContract.name(), deadlineContract.description(), deadlineContract.startDate(), deadlineContract.endDate());
                if (!response.equals("Okay")) {
                    return response;
                }

            } catch (NoSuchElementException error) {
                return "Project ID does not exist";
            }


        } catch (NoSuchElementException error) {

            return "Deadline ID does not exist";
        }


        return checkBaseFields("Milestone",
                deadlineContract.name(),
                deadlineContract.description(),
                deadlineContract.startDate(),
                deadlineContract.endDate());
    }

}

